import akka.actor.Props
import de.guderlei.spray.core.TodoItemActor
import de.guderlei.spray.database.DatabaseConfiguration
import de.guderlei.spray.domain.TodoItem
import org.specs2.mutable.Specification
import spray.testkit.Specs2RouteTest
import de.guderlei.spray.api._
import spray.http.StatusCodes._
import java.util.Date

class RouteTest  extends Specification with Specs2RouteTest  with TodoWebService with DatabaseConfiguration{
    def actorRefFactory = system // connect the DSL to the test ActorSystem
   // start the backend actor, the database is started using the DatabaseConfiguration mixin
    actorRefFactory.actorOf(Props[TodoItemActor], "todo-service")
   var id: Long = 0

  "The service" should {
      "return empty list of todoitems" in {
        Get("/items") ~> myRoute ~> check {
          status === OK
          val content = entityAs[List[TodoItem]]
          content must beEmpty
        }
      }

      "return a todoitem when trying to create a valid new one" in {
        Post("/items", new TodoItem(-1, new Date(), "test")) ~> myRoute ~> check {
          val item = entityAs[TodoItem]
          id = item.id
          status === OK
          item.text === "test"
        }
      }

     "the previously created item by its direct url" in {
        Get("/items/" + id.toString) ~> myRoute ~> check {
          status === OK
          val item = entityAs[Option[TodoItem]]
          item.get.text === "test"
        }
      }
    }

}
