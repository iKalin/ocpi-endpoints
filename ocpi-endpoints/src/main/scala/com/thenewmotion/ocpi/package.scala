package com.thenewmotion

import akka.http.scaladsl.server.Route
import org.slf4j.LoggerFactory

package object ocpi {
  def Logger(cls: Class[_]) = LoggerFactory.getLogger(cls)

  type Version = String
  type AuthToken = String
  type URI = String

  type GuardedRoute = (Version, ApiUser) => Route

  val ourVersion: Version = "2.0"
}
