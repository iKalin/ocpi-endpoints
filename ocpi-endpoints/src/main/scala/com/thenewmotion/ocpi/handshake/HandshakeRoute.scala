package com.thenewmotion.ocpi
package handshake

import com.thenewmotion.ocpi.msgs.v2_0.OcpiStatusCodes.GenericSuccess
import org.joda.time.DateTime

import scala.concurrent.ExecutionContext
import scala.concurrent._
import scalaz._
import ErrorMarshalling._
import akka.http.scaladsl.server.{Rejection, Route}
import akka.http.scaladsl.server.directives.FutureDirectives


case class HandshakeErrorRejection(error: HandshakeError) extends Rejection

trait HandshakeApi extends JsonApi {
  private val logger = Logger(getClass)

  protected def futLeftToRejection[T](errOrX: Future[HandshakeError \/ T])(f: T => Route)
    (implicit ec: ExecutionContext): Route = {
    FutureDirectives.onSuccess(errOrX) {
      case -\/(err) => logger.error(s"HandshakeErrorRejection just happened with reason: ${err.reason}"); reject(HandshakeErrorRejection(err))
      case \/-(res) => f(res)
    }
  }
}

class HandshakeRoute(service: HandshakeService, currentTime: => DateTime = DateTime.now) extends HandshakeApi {
  import com.thenewmotion.ocpi.msgs.v2_0.OcpiJsonProtocol._
  import com.thenewmotion.ocpi.msgs.v2_0.Credentials._

  def route(accessedVersion: Version, tokenToConnectToUs: AuthToken)(implicit ec: ExecutionContext) = {
    post {
      entity(as[Creds]) { credsToConnectToThem =>
        complete {
          service
            .reactToHandshakeRequest(accessedVersion, tokenToConnectToUs, credsToConnectToThem)
            .map(_.map(CredsResp(GenericSuccess.code, Some(GenericSuccess.default_message), currentTime, _)))
        }
      }
    } ~
    get {
      complete {
        service
          .credsToConnectToUs(tokenToConnectToUs)
          .map(CredsResp(GenericSuccess.code, None, currentTime, _))
      }
    } ~
    put {
      entity(as[Creds]) { credsToConnectToThem =>
        complete {
          service
            .reactToUpdateCredsRequest(accessedVersion, tokenToConnectToUs, credsToConnectToThem)
            .map(_.map(CredsResp(GenericSuccess.code, Some(GenericSuccess.default_message), currentTime, _)))
        }
      }
    }
  }
}

class InitiateHandshakeRoute(service: HandshakeService, currentTime: => DateTime = DateTime.now) extends HandshakeApi {
  import com.thenewmotion.ocpi.msgs.v2_0.OcpiJsonProtocol._
  import com.thenewmotion.ocpi.msgs.v2_0.Credentials._
  import com.thenewmotion.ocpi.msgs.v2_0.Versions._

  def route(implicit ec: ExecutionContext) = {
    post {
      entity(as[VersionsRequest]) { theirVersionsUrlInfo =>
        complete {
          import theirVersionsUrlInfo._
          service
            .initiateHandshakeProcess(party_name, country_code, party_id, token, url)
            .map(_.map(CredsResp(GenericSuccess.code,Some(GenericSuccess.default_message), currentTime, _)))
        }
      }
    }
  }
}
