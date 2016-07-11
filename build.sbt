lazy val http4sVersion = "0.15.0-SNAPSHOT"

lazy val evaluator = (project in file("."))
  .settings(
    name := "evaluator",
    scalaVersion := "2.11.8",
    resolvers += Resolver.sonatypeRepo("snapshots"),
    libraryDependencies ++= Seq(
      "com.twitter" %% "util-eval" % "6.34.0",
      "io.monix" %% "monix" % "2.0-RC8",
      "org.http4s" %% "http4s-dsl" % http4sVersion,
      "org.http4s" %% "http4s-blaze-server" % http4sVersion,
      "org.http4s" %% "http4s-blaze-client" % http4sVersion,
      "org.log4s" %% "log4s" % "1.3.0",
      "org.slf4j" % "slf4j-simple" % "1.7.21",
      "org.scalatest" %% "scalatest" % "2.2.4" % "test"
    )
)

enablePlugins(JavaAppPackaging)
