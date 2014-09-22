package de.guderlei.spray.api

import akka.actor.{Props, Actor}
import spray.routing._
import de.guderlei.spray.core._
import de.guderlei.spray.domain._
import spray.httpx.Json4sSupport
import org.json4s.DefaultFormats
import org.slf4j.LoggerFactory
import java.sql.Timestamp


/**
 * Actor to provide the routes of the rest services
 */
class CountriesWebServiceActor extends Actor with CountriesWebService {

  def actorRefFactory = context

  // this actor only runs our route, but you could add
  // other things here, like request stream processing
  // or timeout handling
  def receive = runRoute(itemRoute)


}

trait CountriesWebService extends HttpService with PerRequestCreator with Json4sSupport {
  implicit def executionContext = actorRefFactory.dispatcher

  val json4sFormats = DefaultFormats
  val itemRoute =
    pathPrefix("items") {
      path(Segment) { two:String =>
          get {
            rejectEmptyResponse {
              handlePerRequest {
                Get(two)
              }
            }
          } ~ put {
            entity(as[CountryCode]) {
              item =>
                handlePerRequest {
                  Update(new CountryCode(item.two, item.three, item.name))
                }
            }
          } ~ delete {
            handlePerRequest {
              Delete(two)
            }
          }
      } ~ pathEnd {
        get {
          handlePerRequest {
            All
          }
        } ~ post {
          entity(as[CountryCode]) {
            item =>
              handlePerRequest {
                Create(item.two, item.three, item.name)
              }
          }
        }
    }}


  def handlePerRequest(message: RequestMessage): Route =
    ctx => perRequest(actorRefFactory, ctx, Props[CountryCodeActor], message)
}



