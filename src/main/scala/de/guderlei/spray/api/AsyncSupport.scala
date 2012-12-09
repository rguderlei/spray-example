package de.guderlei.spray.api

import akka.pattern.AskSupport
import akka.util.Timeout
import java.util.concurrent.TimeUnit._
import concurrent.duration.Duration

/**
 * Created with IntelliJ IDEA.
 * User: rguderlei
 * Date: 07.12.12
 * Time: 16:39
 * To change this template use File | Settings | File Templates.
 */
trait AsyncSupport extends AskSupport{
  implicit val timeout: Timeout = Duration(1, SECONDS)
}
