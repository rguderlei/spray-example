package de.guderlei.spray.database

import org.squeryl.Schema
import de.guderlei.spray.domain.{CountryCode}


/**
 * simplest possible schema
 */
object Countries extends Schema {
      val countries = table[CountryCode]
}
