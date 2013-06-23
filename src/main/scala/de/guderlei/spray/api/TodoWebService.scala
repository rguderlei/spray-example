package de.guderlei.spray.api

import akka.actor.Actor
import spray.routing._
import de.guderlei.spray.core.{Get, All}
import de.guderlei.spray.domain.TodoItem
import reflect.ClassTag
import scala.concurrent.{ExecutionContext, Future}
import spray.httpx.marshalling.Marshaller
import scala.concurrent.ExecutionContext.Implicits.global



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
    path("items" / LongNumber) { id: Long =>
      get {

  /*      implicitly[Marshaller[TodoItem]]
        implicitly[Marshaller[Option[TodoItem]]]
        implicitly[Marshaller[Future[TodoItem]]]
        implicitly[Marshaller[Future[Option[TodoItem]]]]*/
          complete {
            (actorRefFactory.actorFor("/user/todo-service") ? Get(id)).mapTo[Option[TodoItem]]
          }
      }
    } ~
    path("items") {
      get {
          complete {
            (actorRefFactory.actorFor("/user/todo-service") ? All).mapTo[List[TodoItem]]
          }
      }
    }
}

