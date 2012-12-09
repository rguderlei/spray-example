package de.guderlei.spray.database

import org.squeryl.Schema
import de.guderlei.spray.domain.TodoItem


/**
 * simplest possible schema
 */
object Todos extends Schema {
      val todos = table[TodoItem]
}
