package de.guderlei.spray.domain

import org.squeryl.PrimitiveTypeMode._
import java.sql.Timestamp


/**
 * simple model for Todos
 */
case class TodoItem(id: Long, text:String);
