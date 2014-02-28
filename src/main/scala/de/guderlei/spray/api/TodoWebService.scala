package de.guderlei.spray.api

import akka.actor.{Props, Actor}
import spray.routing._
import de.guderlei.spray.core._
import de.guderlei.spray.domain._
import reflect.ClassTag
import spray.httpx.Json4sSupport
import org.json4s.DefaultFormats

// magic import

import scala.concurrent.{ExecutionContext, Future}
import spray.httpx.marshalling.Marshaller
import scala.concurrent.ExecutionContext.Implicits.global

// magic import


/**
 * Actor to provide the routes of the rest services
 */
class TodoWebServiceActor extends Actor with HttpService with PerRequestCreator with Json4sSupport {

  implicit def actorRefFactory = context

  val json4sFormats = DefaultFormats

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(itemRoute)

  val itemRoute =
    path("items" / LongNumber) {
      id: Long =>
        get {
          rejectEmptyResponse {
            handlePerRequest {
              Get(id)
            }
          }
        } ~ put {
          entity(as[TodoItem]) {
            item =>
              handlePerRequest {
                Update(new TodoItem(id, item.dueDate, item.text))
              }
          }
        } ~ delete {
          handlePerRequest {
            Delete(id)
          }
        }
    } ~ path("items") {
      get {

        handlePerRequest {
          All
        }
      }
    } ~ post {
      entity(as[TodoItem]) {
        item =>
          handlePerRequest {
            Create(item.dueDate, item.text)
          }
      }
    }


  def handlePerRequest(message: RequestMessage): Route =
    ctx => perRequest(ctx, Props[TodoItemActor], message)
}



