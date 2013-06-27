package de.guderlei.spray.database

import org.squeryl.{Session, SessionFactory}
import org.squeryl.adapters.H2Adapter
import de.guderlei.spray.domain.TodoItem
import java.util.Date
import org.squeryl.PrimitiveTypeMode._
import de.guderlei.spray.domain.TodoItem
import scala.Some

/**
 * sample squeryl configuration
 */
trait DbConnection {
  SessionFactory.newSession.bindToCurrentThread


  def initialize() {
    transaction {
      try {
        from( Todos.todos ) (s => select(s)).toList
      } catch {
        case e: Exception => {
          Todos.create
          Todos.todos.insert(new TodoItem(0, new Date(), text = "urgent"))
        }
      }
    }
  }

}

trait DatabaseConfiguration {
  Class.forName("org.h2.Driver")
  SessionFactory.concreteFactory = Some(()=> Session.create(java.sql.DriverManager.getConnection("jdbc:h2:/home/rguderlei/tmp/spray/test;TRACE_LEVEL_FILE=4"), new H2Adapter) )
}

