package de.guderlei.spray.core

import akka.actor.Actor
import de.guderlei.spray.database.{Todos,DbConnection}
import org.squeryl.PrimitiveTypeMode._
import akka.event.Logging
import de.guderlei.spray.domain.TodoItem
import java.util.Date


case class Get(id:Long)
case class Delete(id: Long)
case class Update(item: TodoItem)
case class Create(dueDate: Date, text: String)
case object All

trait TodoItemOperations extends DbConnection {
  initialize()

  def getById(id: Long) = transaction {
     Todos.todos.where(i =>  i.id === id ).toList.headOption
  }
  def all = transaction {
     from( Todos.todos ) (s => select(s)).toList
  }
  def delete(id: Long) = transaction{
     Todos.todos.deleteWhere(i => i.id === id)
  }
  def create (dueDate: Date, text: String) = transaction {
    // I know, using system time as pk is a bad idea but this is an example ...
     Todos.todos.insert(new TodoItem(System.currentTimeMillis, dueDate, text))
  }
  def update (item: TodoItem) = transaction {
     Todos.todos.update(i => where(i.id===item.id) set(i.dueDate := item.dueDate, i.text := item.text ))
     getById(item.id)
  }
}

class TodoItemActor extends Actor with TodoItemOperations{

  def receive = {
      case Get(id) => sender ! getById(id)
      case Update(item) => sender ! update(item)
      case Delete(id) => sender ! delete(id)
      case Create(dueDate, text) => sender ! create(dueDate, text)
      case All => sender ! all
  }
}
