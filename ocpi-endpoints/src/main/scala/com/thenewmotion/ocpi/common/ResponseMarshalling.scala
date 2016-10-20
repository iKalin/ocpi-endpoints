package com.thenewmotion.ocpi.common

import akka.http.scaladsl.marshalling._
import scala.concurrent.ExecutionContext
import scalaz.\/

trait ResponseMarshalling {
  implicit def disjToResponseMarshaller[L, R]
    (implicit lm: ToResponseMarshaller[L], rm: ToResponseMarshaller[R]) =
    Marshaller {
      implicit ex: ExecutionContext =>
        value: \/[L, R] => value.fold(lm(_), rm(_))
    }
}
