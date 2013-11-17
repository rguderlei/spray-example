package de.guderlei.spray.database

import org.squeryl.{Session, SessionFactory}
import org.squeryl.adapters.H2Adapter
import de.guderlei.spray.domain.TodoItem
import java.util.Date
import org.squeryl.PrimitiveTypeMode._
import scala.Some
import com.jolbox.bonecp.{BoneCP, BoneCPConfig}
import org.slf4j.LoggerFactory

/**
 * sample squeryl configuration with an in-memory H2 database
 */
trait DatabaseConfiguration {
  Class.forName("org.h2.Driver")

  val poolConfig = new BoneCPConfig()
  // see http://stackoverflow.com/questions/4162557/timeout-error-trying-to-lock-table-in-h2
  poolConfig.setJdbcUrl("jdbc:h2:mem:test")
  poolConfig.setUsername("sa")
  poolConfig.setPassword("")
  val connectionPool = new BoneCP(poolConfig)

  val log = LoggerFactory.getLogger("Database")


  SessionFactory.concreteFactory = Some(
    ()=> Session.create(connectionPool.getConnection(), new H2Adapter)
  )

  initializeSchema()
  /**
   * initialize the database schema. The schema is created iff it does not
   * exist in the database.
   */
  def initializeSchema() {
    log.info("initialize database")

      try {
        transaction {
          from( Todos.todos ) (s => select(s)).toList
        }
      } catch {
        case e: Exception => {
          try {
            transaction{
              log.info("create schema")
              Todos.create
            }
            transaction{
              from( Todos.todos ) (s => select(s)).toList
            }
          } catch {
            case e:Exception => {
              log.error(e.getMessage, e)
            }
          }
        }
    }
  }

}

