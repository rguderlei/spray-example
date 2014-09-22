package de.guderlei.spray.domain

import org.squeryl.PrimitiveTypeMode._
import java.sql.Timestamp


/**
 * simple model for CountryCode
 */
case class CountryCode(two:String, three:String, name:String);
