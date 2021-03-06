package com.thenewmotion.ocpi

class DefaultOcpiRestActor(routingConfig: OcpiRoutingConfig) extends OcpiRestActor(routingConfig) {

  import context.dispatcher

  override def receive: Receive = runRoute(topLevelRoute)

  implicit private val rejectionHandler = OcpiRejectionHandler.Default

  implicit private val exceptionHandler = OcpiExceptionHandler.Default

}
