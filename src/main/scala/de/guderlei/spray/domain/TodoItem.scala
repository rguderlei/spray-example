package de.guderlei.spray.domain

import java.util.Date
import org.squeryl.PrimitiveTypeMode._


/**
 * simple model for Todos
 */
case class TodoItem(id: Long, dueDate: Date, text:String);
