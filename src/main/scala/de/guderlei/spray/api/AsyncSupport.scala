package de.guderlei.spray.api

import akka.pattern.AskSupport
import akka.util.Timeout
import java.util.concurrent.TimeUnit._
import concurrent.duration.Duration

/**
 * simple configuration of akkas asyncronous AskSupport
 */
trait AsyncSupport extends AskSupport{
  implicit val timeout: Timeout = Duration(5, SECONDS)
}
