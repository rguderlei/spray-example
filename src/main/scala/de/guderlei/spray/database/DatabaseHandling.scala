package de.guderlei.spray.database

import org.squeryl.{Session, SessionFactory}
import org.squeryl.adapters.H2Adapter

/**
 * sample squeryl configuration
 */
trait DbConnection {
  SessionFactory.newSession.bindToCurrentThread

  /*
  def initialize() {
    transaction {
      Todos.create
      Todos.todos.insert(new TodoItem(0, new Date(), text = "urgent"))
      Todos.todos.insert(new TodoItem(1, new Date(), text = "foo"))
      Todos.todos.insert(new TodoItem(2, new Date(), text = "blubb"))
      Console.println("Data inserted")
    }
  } */

}

trait DatabaseConfiguration {
  Class.forName("org.h2.Driver")
  SessionFactory.concreteFactory = Some(()=> Session.create(java.sql.DriverManager.getConnection("jdbc:h2:/home/rguderlei/tmp/spray/test;TRACE_LEVEL_FILE=4"), new H2Adapter) )
}

