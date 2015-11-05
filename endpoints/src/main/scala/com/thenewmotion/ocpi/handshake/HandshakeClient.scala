package com.thenewmotion.ocpi.handshake

import akka.actor.ActorRefFactory
import akka.util.Timeout
import com.thenewmotion.ocpi._
import com.thenewmotion.ocpi.handshake.Errors._
import com.thenewmotion.ocpi.msgs.v2_0.CommonTypes.SuccessResp
import com.thenewmotion.ocpi.msgs.v2_0.Credentials.Creds
import com.thenewmotion.ocpi.msgs.v2_0.Versions._
import spray.client.pipelining._
import spray.http._
import spray.httpx.unmarshalling._
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration._
import scalaz.Scalaz._
import scalaz._

class HandshakeClient(implicit refFactory: ActorRefFactory) {

  import com.thenewmotion.ocpi.msgs.v2_0.OcpiJsonProtocol._
  import spray.httpx.SprayJsonSupport._

  private val logger = Logger(getClass)

  // setup request/response logging
  val logRequest: HttpRequest => HttpRequest = { r => logger.debug(r.toString); r }
  val logResponse: HttpResponse => HttpResponse = { r => logger.debug(r.toString); r }

  implicit val timeout = Timeout(10.seconds)

  def request(auth: String)(implicit ec: ExecutionContext) = (
    addCredentials(GenericHttpCredentials("Token", auth, Map()))
      ~> logRequest
      ~> sendReceive
      ~> logResponse
    )

  def unmarshalToOption[T](implicit unmarshaller: FromResponseUnmarshaller[T], ec: ExecutionContext):
  Future[HttpResponse] => Future[Option[T]] = {

    _.map { res =>
      if (res.status.isFailure) None
      else Some(unmarshal[T](unmarshaller)(res))
    }
  }

  def getVersions(uri: Uri, auth: String)(implicit ec: ExecutionContext): Future[HandshakeError \/ VersionsResp] = {
    val pipeline = request(auth) ~> unmarshalToOption[VersionsResp]
    pipeline(Get(uri)) map { toRight(_)(VersionsRetrievalFailed) }
  }

  def getVersionDetails(uri: Uri, auth: String)
    (implicit ec: ExecutionContext): Future[HandshakeError \/ VersionDetailsResp] = {
    val pipeline = request(auth) ~> unmarshalToOption[VersionDetailsResp]
    pipeline(Get(uri)) map { toRight(_)(VersionDetailsRetrievalFailed) }
  }

  def sendCredentials(uri: Uri, auth: String, creds: Creds)
    (implicit ec: ExecutionContext): Future[HandshakeError \/ SuccessResp] = {
    val pipeline = request(auth) ~> unmarshalToOption[SuccessResp]
    pipeline(Post(uri, creds)) map { toRight(_)(SendingCredentialsFailed) }
  }

}
