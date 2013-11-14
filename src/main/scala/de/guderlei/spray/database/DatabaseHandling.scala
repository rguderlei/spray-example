package de.guderlei.spray.database

import org.squeryl.{Session, SessionFactory}
import org.squeryl.adapters.H2Adapter
import de.guderlei.spray.domain.TodoItem
import java.util.Date
import org.squeryl.PrimitiveTypeMode._
import de.guderlei.spray.domain.TodoItem
import scala.Some

/**
 * sample squeryl session configuration
 */
trait DbConnection {
  SessionFactory.newSession.bindToCurrentThread

  /**
   * initialize the database schema. The schema is created iff it does not
   * exist in the database.
   */
  def initialize() {
    transaction {
      try {
        from( Todos.todos ) (s => select(s)).toList
      } catch {
        case e: Exception => {
          try {
            Todos.create
          } catch {
            case e:Exception => {

            }
          }
        }
      }
    }
  }

}

/**
 * sample squeryl configuration with an in-memory H2 database
 */
trait DatabaseConfiguration {
  Class.forName("org.h2.Driver")
  // see http://stackoverflow.com/questions/4162557/timeout-error-trying-to-lock-table-in-h2
  SessionFactory.concreteFactory = Some(()=> Session.create(java.sql.DriverManager.getConnection("jdbc:h2:mem:test;TRACE_LEVEL_FILE=4;MVCC=true"), new H2Adapter) )
}

