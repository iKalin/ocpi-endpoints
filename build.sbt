val logging = Seq(
  "ch.qos.logback"               % "logback-classic"          %   "1.1.7" % "test",
  "org.slf4j"                    % "slf4j-api"                %   "1.7.21")

val `spray-json` = Seq("io.spray" %% "spray-json"             %   "1.3.2")

val akka = {
  def akkaModule(name: String) = "com.typesafe.akka" %% s"akka-$name" % "2.4.11"

  Seq(akkaModule("actor"), akkaModule("http-experimental"),
    akkaModule("http-spray-json-experimental"), akkaModule("http-testkit") % "test")
}

val scalaz = Seq("org.scalaz"        %% "scalaz-core"         %   "7.1.10")

val misc = Seq(
  "com.thenewmotion"            %% "joda-money-ext"           %   "1.0.0",
  "com.thenewmotion"            %% "time"                     %   "2.8",
  "com.thenewmotion"            %% "mobilityid"               %   "0.13")

val testing = Seq(
  "org.specs2"                  %% "specs2-core"              %   "2.4.17" % "test",
  "org.specs2"                  %% "specs2-junit"             %   "2.4.17" % "test",
  "org.specs2"                  %% "specs2-mock"              %   "2.4.17" % "test",
  "net.virtual-void"            %% "json-lenses"              %   "0.6.1"  % "test")

val commonSettings = Seq(
  organization := "com.thenewmotion.ocpi",
  licenses += ("Apache License, Version 2.0", url("http://www.apache.org/licenses/LICENSE-2.0"))
)

val `ocpi-prelude` = project
  .enablePlugins(OssLibPlugin)
  .settings(
    commonSettings,
    description := "Definitions that are useful across all OCPI modules")

val `ocpi-msgs` = project
  .enablePlugins(OssLibPlugin)
  .dependsOn(`ocpi-prelude`)
  .settings(
    commonSettings,
    description := "OCPI serialization library",
    libraryDependencies :=`spray-json` ++ misc ++ testing)

val `ocpi-endpoints` = project
  .enablePlugins(OssLibPlugin)
  .dependsOn(`ocpi-prelude`, `ocpi-msgs`)
  .settings(
    commonSettings,
    description := "OCPI endpoints",
    libraryDependencies := logging ++ akka ++ scalaz ++ misc ++ testing)

val `ocpi-endpoints-root` = (project in file("."))
  .aggregate(
    `ocpi-prelude`,
    `ocpi-msgs`,
    `ocpi-endpoints`)
  .enablePlugins(OssLibPlugin)
  .settings(
    commonSettings,
    publish := {}
  )

