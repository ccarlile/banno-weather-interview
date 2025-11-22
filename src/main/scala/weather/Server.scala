package weather

import cats.effect.kernel.Resource
import cats.effect.std.Console
import cats.effect.IO
import com.comcast.ip4s.*
import org.http4s.client.middleware.{Logger => ClientLogger}
import org.http4s.ember.client.EmberClientBuilder
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.server.middleware.{Logger => ServerLogger}
import org.http4s.HttpApp

object Server:
  def apply(): Resource[IO, org.http4s.server.Server] =
    val printToConsole = (msg: String) => Console[IO].println(msg)
    val weatherClient = EmberClientBuilder
      .default[IO]
      .build
      .map(c => ClientLogger(logAction = Some(printToConsole), logBody = true, logHeaders = false)(c))

    val httpApp: Resource[IO, HttpApp[IO]] =
      weatherClient.map(wClient =>
        ServerLogger
          .httpApp(
            logHeaders = false,
            logBody = true,
            logAction = Some(printToConsole)
          )(
            Routes
              .getWeatherRoute(
                GetWeatherService.live[IO](WeatherAPIClient.impl(wClient))
              )
              .orNotFound
          )
      )

    httpApp.flatMap: app =>
      EmberServerBuilder
        .default[IO]
        .withHost(ipv4"0.0.0.0")
        .withPort(port"8080")
        .withHttpApp(app)
        .build
