package de.guderlei.spray.api

import akka.actor._
import spray.http.StatusCodes._
import spray.http.StatusCode
import akka.actor.SupervisorStrategy.Stop
import scala.concurrent.duration._
import spray.routing.RequestContext
import akka.actor.OneForOneStrategy
import de.guderlei.spray.domain._
import de.guderlei.spray.api.PerRequest.{WithProps, WithActorRef}
import spray.httpx.Json4sSupport
import org.json4s.DefaultFormats

/**
 * Created by rguderlei on 25.02.14.
 */
trait PerRequest extends Actor with Json4sSupport{
    def r: RequestContext
    def target: ActorRef
    def message: RequestMessage

    import context._

    val json4sFormats = DefaultFormats

    setReceiveTimeout(2.seconds)

    target ! message

    def receive = {
      case res: Created => complete(spray.http.StatusCodes.Created, res.location)
      case res: SingleItem => complete(OK, res.item)
      case res: ItemList => complete(OK, res.items)
      case res: de.guderlei.spray.domain.Success => complete(OK, res.message)
      case res: Error => complete(BadRequest, res.message)
      case ReceiveTimeout => complete(GatewayTimeout, "Request timeout")
    }

    def complete[T <: AnyRef](status: StatusCode, obj: T) = {
      r.complete(status, obj)
      stop(self)
    }

    override val supervisorStrategy =
      OneForOneStrategy() {
        case e => {
          complete(InternalServerError, Error(e.getMessage))
          Stop
        }
      }
}

object PerRequest {
  case class WithActorRef(r: RequestContext, target: ActorRef, message: RequestMessage) extends PerRequest

  case class WithProps(r: RequestContext, props: Props, message: RequestMessage) extends PerRequest {
    lazy val target = context.actorOf(props)
  }
}

trait PerRequestCreator {
  this: Actor =>

  def perRequest(r: RequestContext, target: ActorRef, message: RequestMessage) =
    context.actorOf(Props(new WithActorRef(r, target, message)))

  def perRequest(r: RequestContext, props: Props, message: RequestMessage) =
    context.actorOf(Props(new WithProps(r, props, message)))
}
