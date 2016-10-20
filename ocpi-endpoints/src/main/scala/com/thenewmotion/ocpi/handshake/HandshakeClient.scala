package com.thenewmotion.ocpi.handshake

import akka.actor.ActorSystem
import com.thenewmotion.ocpi.common.OcpiClient
import com.thenewmotion.ocpi.handshake.HandshakeError._
import com.thenewmotion.ocpi.msgs.v2_0.CommonTypes.Url
import com.thenewmotion.ocpi.msgs.v2_0.Credentials.{Creds, CredsResp}
import com.thenewmotion.ocpi.msgs.v2_0.Versions._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.client.RequestBuilding._
import akka.stream.ActorMaterializer
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import scalaz.{-\/, \/, \/-}

class HandshakeClient(implicit actorSystem: ActorSystem, materializer: ActorMaterializer) extends OcpiClient {
  import com.thenewmotion.ocpi.msgs.v2_0.OcpiJsonProtocol._

  def getTheirVersions(uri: Uri, token: String)(implicit ec: ExecutionContext): Future[HandshakeError \/ VersionsResp] = {
    val resp = singleRequest[VersionsResp](Get(uri), token)
    bimap(resp) {
      case Success(versions) => \/-(versions)
      case Failure(t) =>
        logger.error(s"Could not retrieve the versions information from $uri with token $token. Reason: ${t.getLocalizedMessage}", t)
        -\/(VersionsRetrievalFailed)
    }
  }

  def getTheirVersionDetails(uri: Uri, token: String)
      (implicit ec: ExecutionContext): Future[HandshakeError \/ VersionDetailsResp] = {
    val resp = singleRequest[VersionDetailsResp](Get(uri), token)
    bimap(resp) {
      case Success(versionDet) => \/-(versionDet)
      case Failure(t) =>
        logger.error(s"Could not retrieve the version details from $uri with token $token. Reason: ${t.getLocalizedMessage}", t)
        -\/(VersionDetailsRetrievalFailed)
    }
  }

  def sendCredentials(theirCredUrl: Url, tokenToConnectToThem: String, credToConnectToUs: Creds)
      (implicit ec: ExecutionContext): Future[HandshakeError \/ CredsResp] = {
    val resp = singleRequest[CredsResp](Post(theirCredUrl, credToConnectToUs), tokenToConnectToThem)
    bimap(resp) {
      case Success(theirCreds) => \/-(theirCreds)
      case Failure(t) =>
        logger.error( s"Could not retrieve their credentials from $theirCredUrl with token" +
          s"$tokenToConnectToThem when sending our credentials $credToConnectToUs. Reason: ${t.getLocalizedMessage}", t )
        -\/(SendingCredentialsFailed)
    }
  }
}
