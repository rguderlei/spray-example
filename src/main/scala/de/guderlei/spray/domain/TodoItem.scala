package de.guderlei.spray.domain

import java.util.Date

/**
 * simple model for Todos
 */
case class TodoItem(id: Long, dueDate: Date, text:String);
