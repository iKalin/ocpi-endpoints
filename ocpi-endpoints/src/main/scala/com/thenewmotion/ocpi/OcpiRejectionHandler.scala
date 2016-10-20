package com.thenewmotion.ocpi

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.{MalformedRequestContentRejection, RejectionHandler}
import akka.http.scaladsl.server.directives.BasicDirectives
import com.thenewmotion.ocpi.msgs.v2_0.CommonTypes.ErrorResp
import com.thenewmotion.ocpi.msgs.v2_0.OcpiStatusCodes._
import akka.http.scaladsl.model._
import akka.http.scaladsl.server._
import StatusCodes._
import Directives._

object OcpiRejectionHandler extends BasicDirectives with SprayJsonSupport {

  import com.thenewmotion.ocpi.msgs.v2_0.OcpiJsonProtocol._

  val Default = RejectionHandler.newBuilder()
    .handle {
      case MalformedRequestContentRejection(msg, cause) => complete {
        ( BadRequest,
          ErrorResp(
            GenericClientFailure.code,
            msg))
      }
    }.handle {
      case r@UnsupportedVersionRejection(version: String) => complete {
        (BadRequest,
          ErrorResp(
            UnsupportedVersion.code,
            s"${UnsupportedVersion.default_message}: $version"))
      }
    }.handle {
      case r@AuthenticationFailedRejection(AuthenticationFailedRejection.CredentialsMissing, challengeHeaders) =>
        complete {
          ( BadRequest,
            ErrorResp(
              MissingHeader.code,
              MissingHeader.default_message))
        }
    }.handle {
      case r@AuthenticationFailedRejection(AuthenticationFailedRejection.CredentialsRejected, challengeHeaders) =>
        complete {
          ( BadRequest,
            ErrorResp(
              AuthenticationFailed.code,
              s"${AuthenticationFailed.default_message}"))
        }
    }.handle {
      case r@MissingHeaderRejection(header) => complete {
        (BadRequest,
          ErrorResp(
            MissingHeader.code,
            s"${MissingHeader.default_message}: '$header'"))
      }
    }.handleAll[Rejection] { rejections =>
      complete {
        (BadRequest,
          ErrorResp(
            GenericClientFailure.code,
            rejections.mkString(", ")))
      }
    }.result()
}
