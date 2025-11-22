ThisBuild / scalaVersion := "3.3.1"
ThisBuild / organization := "com.example"

lazy val root = (project in file("."))
  .settings(
    name := "weather-api",
    libraryDependencies ++= Seq(
      "org.typelevel" %% "cats-effect" % "3.5.2",
      "org.http4s" %% "http4s-ember-server" % "0.23.23",
      "org.http4s" %% "http4s-ember-client" % "0.23.23",
      "org.http4s" %% "http4s-circe" % "0.23.23",
      "org.http4s" %% "http4s-dsl" % "0.23.23",
      "io.circe" %% "circe-generic" % "0.14.6",
      "io.circe" %% "circe-parser" % "0.14.6",
      "io.scalaland" %% "chimney" % "1.8.2"
    ),
    scalacOptions += "-Wunused:all"
  )

Compile / run / fork := true
