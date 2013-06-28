package de.guderlei.spray.api

import akka.actor.Actor
import spray.routing._
import de.guderlei.spray.core.{Delete, Get, All, Update, Create}
import de.guderlei.spray.domain.TodoItem
import java.util.Date
import reflect.ClassTag
import spray.http.HttpResponse
import spray.can.parsing.Result.Ok

// magic import

import scala.concurrent.{ExecutionContext, Future}
import spray.httpx.marshalling.Marshaller
import scala.concurrent.ExecutionContext.Implicits.global

// magic import


class TodoWebServiceActor extends Actor with TodoWebService {

  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute)
}

// we don't implement our route structure directly in the service actor because
// we want to be able to test it independently, without having to spin up an actor
// the HttpService trait defines only one abstract member, which
// connects the services environment to the enclosing actor or test
trait TodoWebService extends HttpService with AsyncSupport with MyJsonMarshaller {

  val myRoute =

    path("items" / LongNumber) {
      id: Long =>
        get {
          complete {
            (actorRefFactory.actorFor("/user/todo-service") ? Get(id)).mapTo[Option[TodoItem]]
          }
        } ~ put {
          entity(as[TodoItem]) {
            item =>
              complete {
                (actorRefFactory.actorFor("/user/todo-service") ? Update(new TodoItem(id, item.dueDate, item.text))).mapTo[TodoItem]
              }
          }

        } ~ delete {
          complete {
            (actorRefFactory.actorFor("/user/todo-service") ? Delete(id))
            "item deleted"
          }
        }
    } ~
      path("items") {
        get {
          complete {
            (actorRefFactory.actorFor("/user/todo-service") ? All).mapTo[List[TodoItem]]
          }
        } ~ post {
          entity(as[TodoItem]) {
            item =>
              complete {
                (actorRefFactory.actorFor("/user/todo-service") ? Create(item.dueDate, item.text)).mapTo[TodoItem]
              }
          }

        }
      }

}



