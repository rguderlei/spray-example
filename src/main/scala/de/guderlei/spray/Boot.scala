package de.guderlei.spray

import akka.actor.{Props, ActorSystem}
import database.DatabaseConfiguration
import de.guderlei.spray.api.{TodoWebServiceActor}
import de.guderlei.spray.core.TodoItemActor
import akka.io.IO
import spray.can.Http


object Boot extends App with DatabaseConfiguration {

  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("demo")

  // create and start our service actor
  val service = system.actorOf(Props[TodoWebServiceActor], "demo-service")

  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ! Http.Bind(service, interface = "localhost", port = 9090)
}