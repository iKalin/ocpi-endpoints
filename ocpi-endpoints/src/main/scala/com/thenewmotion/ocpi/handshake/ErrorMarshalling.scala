package com.thenewmotion.ocpi.handshake

import com.thenewmotion.ocpi.msgs.v2_0.CommonTypes.ErrorResp
import com.thenewmotion.ocpi.msgs.v2_0.OcpiStatusCodes
import OcpiStatusCodes._
import com.thenewmotion.ocpi.msgs.v2_0.OcpiJsonProtocol._
import com.thenewmotion.ocpi.common.ResponseMarshalling
import HandshakeError._
import akka.http.scaladsl.marshalling.ToResponseMarshaller
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

object ErrorMarshalling extends ResponseMarshalling {

  implicit val handshakeErrorToResponseMarshaller: ToResponseMarshaller[HandshakeError] =
    implicitly[ToResponseMarshaller[(StatusCode, ErrorResp)]]
    .compose[HandshakeError] { e =>
      val (status, cec) = e match {
        case VersionsRetrievalFailed => (FailedDependency, UnableToUseApi)
        case VersionDetailsRetrievalFailed => (FailedDependency, UnableToUseApi)
        case SendingCredentialsFailed => (BadRequest, UnableToUseApi)
        case SelectedVersionNotHostedByUs(v) => (BadRequest, UnsupportedVersion)
        case CouldNotFindMutualVersion => (BadRequest, UnsupportedVersion)
        case SelectedVersionNotHostedByThem(_) => (BadRequest, UnsupportedVersion)
        case HandshakeError.UnknownEndpointType(_) => (InternalServerError, OcpiStatusCodes.UnknownEndpointType)
        case CouldNotPersistCredsForUs => (InternalServerError, GenericServerFailure)
        case CouldNotPersistNewCredsForUs => (InternalServerError, GenericServerFailure)
        case CouldNotPersistNewToken(_) => (InternalServerError, GenericServerFailure)
        case CouldNotPersistNewEndpoint(_) => (InternalServerError, GenericServerFailure)
        case CouldNotUpdateEndpoints => (InternalServerError, GenericServerFailure)
        case CouldNotPersistNewParty(p) => (InternalServerError, GenericServerFailure)
        case AlreadyExistingParty(p, c, v) => (Conflict, PartyAlreadyRegistered)
        case UnknownPartyToken(t) => (BadRequest, AuthenticationFailed)
        case WaitingForRegistrationRequest => (BadRequest, RegistrationNotCompletedYetByParty)
      }

      (status, ErrorResp(cec.code, e.reason))
    }
}
