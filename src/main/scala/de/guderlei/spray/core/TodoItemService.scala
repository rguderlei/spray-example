package de.guderlei.spray.core

import akka.actor.Actor
import de.guderlei.spray.database.Countries
import org.squeryl.PrimitiveTypeMode._
import akka.event.Logging
import de.guderlei.spray.domain._
import org.slf4j.LoggerFactory


/**
 * Trait to define the Operations on Countries
 */
trait CountryCodeOperations {

  /**
   * load a country by its database id
   *
   * @param two
   * @return an Option, None iff the CountryCode wasn't found in the database
  */
  def getByCode(two: String) = transaction {
     SingleItem(from(Countries.countries) (i => where(i.two === two) select(i)).toList.headOption)
  }

  /**
   * load all CountryCode from the database
   * @return
   */
  def all() = transaction {
    try{
      ItemList(from( Countries.countries ) (s => select(s)).toList)
    } catch{
      case e:Exception => {
        println(e.getMessage())
        List()
      }
    }
  }

  /**
   * delete a CountryCode
   * @param two
   * @return
   */
  def delete(two: String) = transaction{
    Countries.countries.deleteWhere(i => i.two === two)
    Success("deleted successfully")
  }

  /**
   * create a new CountryCode
   * @param two the 2 chars code of the country
   * @param three the 3 hars code of the country
   * @param name the name of the country
   * @return the newly created country
   */
  def create (two:String, three:String, name: String) = transaction {
     val item = Countries.countries.insert(new CountryCode(two, three, name))
     Created("/items/"+item.two)
  }

  /**
   * modify an existing CountryCode
   *
   * @param item the item to modify
   * @return the modified item
   */
  def update (item: CountryCode) = {
    transaction {
      Countries.countries.update(i => where(i.two===item.two)set(i.two := item.two, i.three := item.three, i.name := item.name))
      }
      getByCode(item.two)
  }
}

/**
 * Actor to provide the Operations on Countries
 */
class CountryCodeActor extends Actor with CountryCodeOperations{
  val log = Logging(context.system, this)
  def receive = {
      case Get(two) => sender ! getByCode(two)
      case Update(item) => sender ! update(item)
      case Delete(two) => sender ! delete(two)
      case Create(two, three, name) => sender ! create(two, three, name)
      case All => sender ! all()
  }
}