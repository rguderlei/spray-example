package de.guderlei.spray

import akka.actor.{Props, ActorSystem}
import api.MyServiceActor
import core.TodoItemActor
import domain.{TodoItem, Todos}
import spray.can.server.HttpServer
import spray.io._
import org.squeryl.{Session, SessionFactory}
import org.squeryl.adapters.H2Adapter
import org.squeryl.PrimitiveTypeMode._
import java.util.Date
import concurrent.duration.Duration
import akka.util.Timeout
import java.util.concurrent.TimeUnit._


object Boot extends App {
  // Initialize Squeryl, use a H2 in-memory database
  Class.forName("org.h2.Driver");
  SessionFactory.concreteFactory = Some(()=>
  Session.create(
      java.sql.DriverManager.getConnection("jdbc:h2:mem:test;TRACE_LEVEL_FILE=4"),
      new H2Adapter()))
  transaction {
    Todos.create
    Todos.todos.insert(new TodoItem(0,dueDate = new Date(), text = "urgent"))
    Todos.todos.insert(new TodoItem(0,dueDate = new Date(), text = "urgent"))
    Todos.todos.insert(new TodoItem(0,dueDate = new Date(), text = "urgent"))
    Console.println("Data inserted")
  }

  // we need an ActorSystem to host our application in
  val system = ActorSystem("demo")

  // every spray-can HttpServer (and HttpClient) needs an IOBridge for low-level network IO
  // (but several servers and/or clients can share one)
  val ioBridge = new IOBridge(system).start()

  // create and start our service actor
  val service = system.actorOf(Props[MyServiceActor], "demo-service")
  system.actorOf(Props[TodoItemActor], "todo-service")
  // create
  implicit val timeout: Timeout = Duration(1, SECONDS)
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