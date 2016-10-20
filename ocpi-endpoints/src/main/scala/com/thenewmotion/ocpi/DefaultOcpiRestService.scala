package com.thenewmotion.ocpi

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.Materializer
import scala.concurrent.ExecutionContext

class DefaultOcpiRestService(routingConfig: OcpiRoutingConfig)
                            (implicit actorSystem: ActorSystem, materializer: Materializer) extends OcpiRestService(routingConfig) {

  implicit private val rejectionHandler = OcpiRejectionHandler.Default

  implicit private val exceptionHandler = OcpiExceptionHandler.Default

  def bindAndHandle(interface: String, port: Int)(implicit ec: ExecutionContext) =
    Http().bindAndHandle(topLevelRoute, interface, port)

}
