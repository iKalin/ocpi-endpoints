package com.thenewmotion.ocpi.locations

import akka.actor.ActorSystem
import akka.http.scaladsl.client.RequestBuilding._
import com.thenewmotion.ocpi.common.OcpiClient
import com.thenewmotion.ocpi.locations.LocationsError._
import com.thenewmotion.ocpi.msgs.v2_0.Locations.LocationsResp
import akka.http.scaladsl.model.Uri
import akka.stream.ActorMaterializer
import scala.concurrent.Future
import scala.util.{Failure, Success}
import scalaz.{-\/, \/, \/-}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

class LocationsClient(implicit actorSystem: ActorSystem, materializer: ActorMaterializer) extends OcpiClient {

  import com.thenewmotion.ocpi.msgs.v2_0.OcpiJsonProtocol._

  def getLocations(uri: Uri, auth: String): Future[LocationsError \/ LocationsResp] = {

    val resp = singleRequest[LocationsResp](Get(uri), auth)

    bimap(resp) {
      case Success(locations) => \/-(locations)
      case Failure(t) =>
        logger.error(s"Failed to get locations from $uri. Reason: ${t.getLocalizedMessage}", t)
        -\/(LocationNotFound())
    }
  }
}
