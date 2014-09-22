package de.guderlei.spray.domain

import java.sql.Timestamp

/**
 * Created by rguderlei on 25.02.14.
 */
sealed trait ResultMessage

case class Created(location: String) extends ResultMessage
case class Success(message: String) extends ResultMessage
case class SingleItem(item: Option[CountryCode]) extends ResultMessage
case class ItemList(items: List[CountryCode]) extends ResultMessage

case class Error(message: String)


sealed trait RequestMessage
/**
 * Message object for a request to load an item
 * identified by a given id
 */
case class Get(two: String) extends RequestMessage

/**
 * Message object for a request to delete an item
 * identified by a given id
 */
case class Delete(two: String) extends RequestMessage
/**
 * Message object for a request to modify an item.
 * The item to modify is passed along with the message object.
 */
case class Update(item: CountryCode) extends RequestMessage
/**
 * Message object for a request to create a new item.
 */
case class Create(two: String, three: String, name: String) extends RequestMessage
/**
 * Message object for a request to load all existing items.
 */
case object All extends RequestMessage
