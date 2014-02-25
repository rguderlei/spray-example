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


/**
 * Created by rguderlei on 25.02.14.
 */
trait PerRequest extends Actor with MyJsonMarshaller{
    def r: RequestContext
    def target: ActorRef
    def message: RequestMessage

    import context._

    setReceiveTimeout(2.seconds)

    target ! message

    def receive = {
      case res: Created => complete(spray.http.StatusCodes.Created, res);
      case res: ResultMessage => complete(OK, res)
      case ReceiveTimeout => complete(GatewayTimeout, Error("Request timeout"))
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
