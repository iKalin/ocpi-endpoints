package com.thenewmotion.ocpi.locations

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.{AuthorizationFailedRejection, RejectionHandler}
import akka.http.scaladsl.server.directives.{BasicDirectives, MiscDirectives}
import com.thenewmotion.ocpi.locations.LocationsError._
import com.thenewmotion.ocpi.msgs.v2_0.CommonTypes.ErrorResp
import com.thenewmotion.ocpi.msgs.v2_0.OcpiStatusCodes.GenericClientFailure
import org.joda.time.DateTime
import akka.http.scaladsl.model._
import akka.http.scaladsl.server._
import StatusCodes._
import Directives._

object LocationsRejectionHandler extends BasicDirectives with MiscDirectives with SprayJsonSupport {

  import com.thenewmotion.ocpi.msgs.v2_0.OcpiJsonProtocol._

  val DefaultErrorMsg = "An error occurred."
  val Default = RejectionHandler.newBuilder()
      .handle {
        case AuthorizationFailedRejection =>
          extractUri { uri => complete {
            (Forbidden,
              ErrorResp(
                GenericClientFailure.code,
                s"The client is not authorized to access ${uri.toRelative}",
                DateTime.now()))
            }
          }
      }.handle {
        case LocationsErrorRejection(e@LocationNotFound(reason)) => complete {
          ( NotFound,
            ErrorResp(
              GenericClientFailure.code,
              reason getOrElse DefaultErrorMsg,
              DateTime.now()))
        }
      }.handle {
        case LocationsErrorRejection(e@LocationCreationFailed(reason)) => complete {
          ( BadRequest,
            ErrorResp(
              GenericClientFailure.code,
              reason getOrElse DefaultErrorMsg,
              DateTime.now()))
        }
      }.handle {
        case LocationsErrorRejection(e@EvseNotFound(reason)) => complete {
          ( NotFound,
            ErrorResp(
              GenericClientFailure.code,
              reason getOrElse DefaultErrorMsg,
              DateTime.now()))
        }
      }.handle {
        case LocationsErrorRejection(e@EvseCreationFailed(reason)) => complete {
          (BadRequest,
            ErrorResp(
              GenericClientFailure.code,
              reason getOrElse DefaultErrorMsg,
              DateTime.now()))
        }
      }.handle {
        case LocationsErrorRejection(e@ConnectorNotFound(reason)) => complete {
          (NotFound,
            ErrorResp(
              GenericClientFailure.code,
              reason getOrElse DefaultErrorMsg,
              DateTime.now()))
        }
      }.handle {
        case LocationsErrorRejection(e@ConnectorCreationFailed(reason)) => complete {
          ( BadRequest,
            ErrorResp(
              GenericClientFailure.code,
              reason getOrElse DefaultErrorMsg,
              DateTime.now()))
        }
      }.result()
}
