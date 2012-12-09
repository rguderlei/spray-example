package de.guderlei.spray

import akka.actor.{Props, ActorSystem}
import database.DatabaseConfiguration
import de.guderlei.spray.api.{TodoWebServiceActor}
import de.guderlei.spray.core.TodoItemActor
import spray.can.server.HttpServer
import spray.io._



object Boot extends App with DatabaseConfiguration {
  // we need an ActorSystem to host our application in
  val system = ActorSystem("demo")

  // every spray-can HttpServer (and HttpClient) needs an IOBridge for low-level network IO
  // (but several servers and/or clients can share one)
  val ioBridge = new IOBridge(system).start()

  // create and start our service actor
  val service = system.actorOf(Props[TodoWebServiceActor], "demo-service")
  val backend = system.actorOf(Props[TodoItemActor], "todo-service")

  // create and start the spray-can HttpServer, telling it that
  // we want requests to be handled by our singleton service actor
  val httpServer = system.actorOf(
    Props(new HttpServer(ioBridge, SingletonHandler(service))),
    name = "http-server"
  )

  // a running HttpServer can be bound, unbound and rebound
  // initially to need to tell it where to bind to
  httpServer ! HttpServer.Bind("localhost", 8080)

  // finally we drop the main thread but hook the shutdown of
  // our IOBridge into the shutdown of the applications ActorSystem
  system.registerOnTermination {
    ioBridge.stop()
  }
}