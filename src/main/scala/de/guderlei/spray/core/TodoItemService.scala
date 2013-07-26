package de.guderlei.spray.core

import akka.actor.Actor
import de.guderlei.spray.database.{Todos,DbConnection}
import org.squeryl.PrimitiveTypeMode._
import akka.event.Logging
import de.guderlei.spray.domain.TodoItem
import java.util.Date
import akka.event.Logging


/**
 * Message object for a request to load an item
 * identified by a given id
 */
case class Get(id:Long)

/**
 * Message object for a request to delete an item
 * identified by a given id
 */
case class Delete(id: Long)
/**
 * Message object for a request to modify an item.
 * The item to modify is passed along with the message object.
 */
case class Update(item: TodoItem)
/**
 * Message object for a request to create a new item.
 */
case class Create(dueDate: Date, text: String)
/**
 * Message object for a request to load all existing items.
 */
case object All


/**
 * Trait to define the Operations on TodoItems
 */
trait TodoItemOperations extends DbConnection {

  initialize()

  /**
   * load an TodoItem by its database id
   *
   * @param id
   * @return an Option, None iff the TodoItem wasn't found in the database
   */
  def getById(id: Long) = transaction {
     from(Todos.todos) (i => where(i.id === id ) select(i)).toList.headOption
  }

  /**
   * load all TodoItems from the database
   * @return
   */
  def all = transaction {
     from( Todos.todos ) (s => select(s)).toList
  }

  /**
   * delete a TodoItem
   * @param id
   * @return
   */
  def delete(id: Long) = transaction{
     Todos.todos.deleteWhere(i => i.id === id)
  }

  /**
   * create a new TodoItem
   * @param dueDate the dueDate of the new item
   * @param text the text of the item
   * @return the newly created item
   */
  def create (dueDate: Date, text: String) = transaction {
    // I know, using system time as pk is a bad idea but this is an example ...
     Todos.todos.insert(new TodoItem(System.currentTimeMillis, dueDate, text))
  }

  /**
   * modify an existing TodoItem
   *
   * @param item the item to modify
   * @return the modified item
   */
  def update (item: TodoItem) = transaction {
     Todos.todos.update(i => where(i.id===item.id) set(i.dueDate := item.dueDate, i.text := item.text ))
     getById(item.id)
  }
}

/**
 * Actor to provide the Operations on TodoItems
 */
class TodoItemActor extends Actor with TodoItemOperations{
  val log = Logging(context.system, this)
  def receive = {
      case Get(id) => {
        log.info(id.toString())
        val item = getById(id)
        item match {
          case None => log.info("nothing found")
          case Some(x) => log.info(x.text)
        }

        sender ! item
      }
      case Update(item) => sender ! update(item)
      case Delete(id) =>{
        log.info("delete called")
        sender ! delete(id)
      }
      case Create(dueDate, text) => sender ! create(dueDate, text)
      case All => sender ! all
  }
}
