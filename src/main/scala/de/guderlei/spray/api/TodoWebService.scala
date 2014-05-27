package de.guderlei.spray.api

import akka.actor.{Props, Actor}
import spray.routing._
import de.guderlei.spray.core._
import de.guderlei.spray.domain._
import spray.httpx.Json4sSupport
import org.json4s.DefaultFormats


/**
 * Actor to provide the routes of the rest services
 */
class TodoWebServiceActor extends Actor with TodoWebService {

  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(itemRoute)


}

trait TodoWebService extends HttpService with PerRequestCreator with Json4sSupport {
  implicit def executionContext = actorRefFactory.dispatcher

  val json4sFormats = DefaultFormats
  val itemRoute =
    pathPrefix("items") {
      path(LongNumber) { id: Long =>
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
      } ~ pathEnd {
        get {
          handlePerRequest {
            All
          }
        } ~ post {
          entity(as[TodoItem]) {
            item =>
              handlePerRequest {
                Create(item.dueDate, item.text)
              }
          }
        }
    }}


  def handlePerRequest(message: RequestMessage): Route =
    ctx => perRequest(actorRefFactory, ctx, Props[TodoItemActor], message)
}



