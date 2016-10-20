package com.thenewmotion.ocpi.common

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.headers.GenericHttpCredentials
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.unmarshalling.{FromResponseUnmarshaller, Unmarshal}
import akka.stream.ActorMaterializer
import com.thenewmotion.ocpi._
import scala.concurrent.{Future, Promise}
import scala.util.Try

abstract class OcpiClient(implicit actorSystem: ActorSystem, materializer: ActorMaterializer) {

  import actorSystem.dispatcher

  private val http = Http()

  protected val logger = Logger(getClass)

  // setup request/response logging
  private val logRequest: HttpRequest => HttpRequest = { r => logger.debug(r.toString); r }
  private val logResponse: HttpResponse => HttpResponse = { r => logger.debug(r.toString); r }

  protected def singleRequest[T : FromResponseUnmarshaller](req: HttpRequest, auth: String) = {
    http.singleRequest(logRequest(req.addCredentials(GenericHttpCredentials("Token", auth, Map())))).flatMap { response =>
      logResponse(response)
      response.status match {
        case status if status.isSuccess => Unmarshal(response).to[T]
        case status =>
          Future.failed(new RuntimeException(s"Request failed with status ${response.status}"))
      }
    }
  }

  protected def bimap[T, M](f: Future[T])(pf: PartialFunction[Try[T], M]): Future[M] = {
    val p = Promise[M]()
    f.onComplete(r => p.complete(Try(pf(r))))
    p.future
  }
}
