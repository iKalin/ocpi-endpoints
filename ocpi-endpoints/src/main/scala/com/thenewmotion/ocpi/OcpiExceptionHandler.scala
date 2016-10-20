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

  import com.thenewmotion.ocpi.msgs.v2_0.OcpiJsonProtocol._

  val Default = ExceptionHandler {

    case exception => complete {
        ( InternalServerError,
            ErrorResp(
              GenericClientFailure.code,
              exception.toString))
      }
  }
}
