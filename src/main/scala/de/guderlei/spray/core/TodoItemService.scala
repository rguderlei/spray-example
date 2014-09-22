package de.guderlei.spray.core

import akka.actor.Actor
import de.guderlei.spray.database.Todos
import org.squeryl.PrimitiveTypeMode._
import akka.event.Logging
import de.guderlei.spray.domain._
import java.sql.Timestamp
import org.slf4j.LoggerFactory


/**
 * Trait to define the Operations on TodoItems
 */
trait TodoItemOperations {

  /**
   * load an TodoItem by its database id
   *
   * @param id
   * @return an Option, None iff the TodoItem wasn't found in the database
   */
  def getById(id: Long) = transaction {
    LoggerFactory.getLogger("Database").info(""+Todos.todos.toList.length)
     SingleItem(from(Todos.todos) (i => where(i.id === id) select(i)).toList.headOption)
  }

  /**
   * load all TodoItems from the database
   * @return
   */
  def all() = transaction {
    try{
      LoggerFactory.getLogger("Database").info("all")
      ItemList(from( Todos.todos ) (s => select(s)).toList)
    } catch{
      case e:Exception => {
        println(e.getMessage())
        List()
      }
    }
  }

  /**
   * delete a TodoItem
   * @param id
   * @return
   */
  def delete(id: Long) = transaction{
     Todos.todos.deleteWhere(i => i.id === id)
    Success("deleted successfully")
  }

  /**
   * create a new TodoItem
   * @param text the text of the item
   * @return the newly created item
   */
  def create ( text: String) = transaction {
    // I know, using system time as pk is a bad idea but this is an example ...
     val item = Todos.todos.insert(new TodoItem(System.currentTimeMillis, text))
     Created("/items/"+item.id)
  }

  /**
   * modify an existing TodoItem
   *
   * @param item the item to modify
   * @return the modified item
   */
  def update (item: TodoItem) = {
    transaction {
      Todos.todos.update(i => where(i.id===item.id) set(i.text := item.text ))
    }
    getById(item.id)
  }
}

/**
 * Actor to provide the Operations on TodoItems
 */
class TodoItemActor extends Actor with TodoItemOperations{
  val log = Logging(context.system, this)
  def receive = {
      case Get(id) => sender ! getById(id)
      case Update(item) => sender ! update(item)
      case Delete(id) => sender ! delete(id)
      case Create(text) => sender ! create(text)
      case All => sender ! all()
  }
}
