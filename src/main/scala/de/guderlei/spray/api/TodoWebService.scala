package de.guderlei.spray.api

import akka.actor.{Props, Actor}
import spray.routing._
import de.guderlei.spray.core._
import de.guderlei.spray.domain.TodoItem
import java.util.Date
import reflect.ClassTag
import spray.http.HttpResponse
import akka.routing.{RoundRobinRouter, FromConfig}
import de.guderlei.spray.core.Update
import de.guderlei.spray.domain.TodoItem
import de.guderlei.spray.core.Get
import de.guderlei.spray.core.Create
import de.guderlei.spray.core.Delete

// magic import

import scala.concurrent.{ExecutionContext, Future}
import spray.httpx.marshalling.Marshaller
import scala.concurrent.ExecutionContext.Implicits.global

// magic import


/**
 * Actor to provide the routes of the rest services
 */
class TodoWebServiceActor extends Actor with TodoWebService {

  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(myRoute)
}

/**
 * trait to define the routes of the application
 *
 * we don't implement our route structure directly in the service actor because
we want to be able to test it independently, without having to spin up an actor
the HttpService trait defines only one abstract member, which connects the services environment to the enclosing actor or test */
trait TodoWebService extends HttpService with AsyncSupport with MyJsonMarshaller {
  def backend = actorRefFactory.actorOf(Props[TodoItemActor].withRouter(RoundRobinRouter(nrOfInstances = 50)))

  val myRoute =

    path("items" / LongNumber) {
      id: Long =>
        get {
          rejectEmptyResponse {
            complete {
              (backend ? Get(id)).mapTo[Option[TodoItem]]
            }
          }
        } ~ put {
          entity(as[TodoItem]) {
            item =>
              complete {
                (backend ? Update(new TodoItem(id, item.dueDate, item.text))).mapTo[Option[TodoItem]]
              }
          }

        } ~ delete {
          complete {
            (backend ? Delete(id))
            "item deleted"
          }
        }
    } ~  path("items") {
          get {

              complete {
                (backend ? All).mapTo[List[TodoItem]]
              }
          }
      } ~ post {
            entity(as[TodoItem]) {
              item =>
                complete {
                  (backend ? Create(item.dueDate, item.text)).mapTo[TodoItem]
                }
            }
          }



}



