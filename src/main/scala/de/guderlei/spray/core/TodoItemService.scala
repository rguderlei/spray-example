package de.guderlei.spray.core

import akka.actor.Actor
import de.guderlei.spray.domain.Todos
import org.squeryl.PrimitiveTypeMode._

/**
 * Created with IntelliJ IDEA.
 * User: rguderlei
 * Date: 24.11.12
 * Time: 13:53
 * To change this template use File | Settings | File Templates.
 */

case class Get(id:Long)
case object All

trait TodoItemOperations {
    def getItemById(id:Long) = Todos.todos.where(i => i.id === id).single
    def allItems() = Todos.todos
}

class TodoItemActor extends Actor with TodoItemOperations {
  protected def receive = {
    case Get(id) =>
      sender ! getItemById(id)

    case All =>
      sender ! allItems()

  }


}
