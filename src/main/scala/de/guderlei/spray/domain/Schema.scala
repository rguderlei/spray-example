package de.guderlei.spray.domain

import org.squeryl.Schema


/**
 * simplest possible schema
 */
object Todos extends Schema {
      val todos = table[TodoItem]
}
