# OCPI endpoints [![Build Status](https://travis-ci.org/thenewmotion/ocpi-endpoints.png?branch=master)](https://travis-ci.org/thenewmotion/ocpi-endpoints)

ATTENTION! As of the 30th of September 2015, expect breaking API changes with every commit!


The New Motion implementation of common endpoints of the OCPI protocol.

See http://github.com/ocpi/ocpi

# Version
The version of ocpi the library implements is hardcoded in `ocpi.ourVersion`

# Notions
To make clear when the parameters are about the application making use of the application or the party the application is
connecting to they are referred as: `our`/`us` or `theirs`/`them`

# Dependencies
To find out if there is any dependency to be updated you can run `sbt dependencyUpdates` to see what could be updated.
See https://github.com/rtimush/sbt-updates

# Usage
* add to your dependencies like this:
`"com.thenewmotion.ocpi" %% "ocpi-endpoints" % "0.8-SNAPSHOT"`
* Implement a `MyOcpiRestActor` instance by subclassing the abstract `OcpiRestActor` or use the supplied default implementation `DefaultOcpiRestActor`
* if using `spray-can`, you can instantiate your actor like this:

    `val serviceActor = system.actorOf(Props(new MyOcpiRestActor(system,routingConfig)))`

* then bind it to an address like this:

  `IO(Http) ! Http.Bind(serviceActor, host, port)`

* `system` is just an Akka ActorSystem
* routingConfig is a map that defines the available versions and endpoints:

    ```
      lazy val routingConfig = OcpiRoutingConfig(
        "cpo",
        "versions",
        Map("2.0" -> OcpiVersionConfig(EndpointIdentifierEnum.Locations -> locRoute))
      )
    ```
