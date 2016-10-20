package com.thenewmotion.ocpi

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.ExceptionHandler
import akka.http.scaladsl.server.directives.BasicDirectives
import com.thenewmotion.ocpi.msgs.v2_0.CommonTypes.ErrorResp
import com.thenewmotion.ocpi.msgs.v2_0.OcpiStatusCodes._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server._
import StatusCodes._
import Directives._

object OcpiExceptionHandler extends BasicDirectives with SprayJsonSupport {

  private val logger = Logger(getClass)

  import com.thenewmotion.ocpi.msgs.v2_0.OcpiJsonProtocol._

  val Default = ExceptionHandler {

    case exception =>
      logger.error("An error occurred while processing the Http request", exception)
      complete {
        ( InternalServerError,
            ErrorResp(
              GenericClientFailure.code,
              exception.toString))
      }
  }
}
