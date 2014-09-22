package de.guderlei.spray.database

import org.squeryl.{Session, SessionFactory}
import org.squeryl.adapters.{MySQLAdapter, H2Adapter}
import org.squeryl.PrimitiveTypeMode._
import scala.Some
import com.jolbox.bonecp.{BoneCP, BoneCPConfig}
import org.slf4j.LoggerFactory

/**
 * sample squeryl configuration with mysql database
 */
trait DatabaseConfiguration {
  //Class.forName("com.mysql.jdbc.Driver")
  Class.forName("org.h2.Driver")
  //configure an instantiate BoneCP connection pool
  val poolConfig = new BoneCPConfig()
  poolConfig.setJdbcUrl("jdbc:mysql://localhost:3306/countries")
  //poolConfig.setJdbcUrl("jdbc:h2:mem:test")
  poolConfig.setUsername("root")
  poolConfig.setPassword("")
  poolConfig.setMinConnectionsPerPartition(5);
  poolConfig.setMaxConnectionsPerPartition(1000);
  poolConfig.setPartitionCount(1);
  val connectionPool = new BoneCP(poolConfig)

  // create a squeryl session factory
  val log = LoggerFactory.getLogger("Database")
  SessionFactory.concreteFactory = Some(
    ()=> Session.create(connectionPool.getConnection(), new H2Adapter())
  )

  // initalize database schema on the fly
  initializeSchema()
  /**
   * initialize the database schema. The schema is created iff it does not
   * exist in the database.
   */
  def initializeSchema() {
    log.info("initialize database")

      try {
        transaction {
          from( Countries.countries ) (s => select(s)).toList
        }
      } catch {
        case e: Exception => {
          try {
            transaction{
              log.info("create schema")
              Countries.create
            }
            transaction{
              from( Countries.countries ) (s => select(s)).toList
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

