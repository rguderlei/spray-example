package de.guderlei.spray.core

import akka.actor.Actor
import de.guderlei.spray.database.{Todos,DbConnection}
import org.squeryl.PrimitiveTypeMode._
import akka.event.Logging
import de.guderlei.spray.domain.TodoItem


case class Get(id:Long)
case object All

trait TodoItemOperations extends DbConnection {
  def getById(id: Long) = transaction {
     Todos.todos.where(i =>  i.id === id ).toList.headOption
   }
   def all = transaction {
     from( Todos.todos ) (s => select(s)).toList
   }
}

class TodoItemActor extends Actor with TodoItemOperations{

  def receive = {
      case Get(id) => sender ! getById(id)
      case All => sender ! all
  }
}
