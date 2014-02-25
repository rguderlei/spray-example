package de.guderlei.spray.domain

import java.util.Date

/**
 * Created by rguderlei on 25.02.14.
 */
sealed trait ResultMessage

case class Created(location: String) extends ResultMessage
case class Success(message: String) extends ResultMessage
case class SingleItem(item: Option[TodoItem]) extends ResultMessage
case class ItemList(items: List[TodoItem]) extends ResultMessage

case class Error(message: String)


sealed trait RequestMessage
/**
 * Message object for a request to load an item
 * identified by a given id
 */
case class Get(id:Long) extends RequestMessage

/**
 * Message object for a request to delete an item
 * identified by a given id
 */
case class Delete(id: Long) extends RequestMessage
/**
 * Message object for a request to modify an item.
 * The item to modify is passed along with the message object.
 */
case class Update(item: TodoItem) extends RequestMessage
/**
 * Message object for a request to create a new item.
 */
case class Create(dueDate: Date, text: String) extends RequestMessage
/**
 * Message object for a request to load all existing items.
 */
case object All extends RequestMessage
