import akka.actor.Props
import de.guderlei.spray.core.TodoItemActor
import de.guderlei.spray.database.DatabaseConfiguration
import de.guderlei.spray.domain.TodoItem
import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import spray.routing.HttpService
import de.guderlei.spray.api._
import spray.httpx.SprayJsonSupport._
import spray.http.StatusCodes._

class RouteTest  extends Specification with Specs2RouteTest  with TodoWebService with DatabaseConfiguration{
    def actorRefFactory = system // connect the DSL to the test ActorSystem
    actorRefFactory.actorOf(Props[TodoItemActor], "todo-service")

  "The service" should {

      "return list of todoitems" in {
        Get("/items") ~> myRoute ~> check {
          status === OK
          val content = entityAs[List[TodoItem]]
          print(content)
          content must beEmpty
        }
      }
      /*
      "return a 'PONG!' response for GET requests to /ping" in {
        Get("/ping") ~> myRoute ~> check {
          entityAs[String] === "PONG!"
        }
      }

      "leave GET requests to other paths unhandled" in {
        Get("/kermit") ~> myRoute ~> check {
          handled must beFalse
        }
      }     */
    }

}
