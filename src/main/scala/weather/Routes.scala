package weather

import cats.effect.IO
import io.circe.generic.auto.*
import org.http4s.*
import org.http4s.circe.CirceEntityEncoder.circeEntityEncoder
import org.http4s.dsl.io.*

object Routes:
  def getWeatherRoute(svc: GetWeatherService[IO]) = HttpRoutes.of[IO] {
    case GET -> Root / "weather" :? LongitudeQueryParamMatcher(lon) :? LatitudeQueryParamMatcher(lat) =>
      svc
        .getWeather(Latitude(lat), Longitude(lon))
        .flatMap: result =>
          Ok(
            WeatherResponse(result.condition, result.characterization.toString)
          )

  }

opaque type Latitude = Double
object Latitude:
  def apply(lat: Double): Latitude = lat

opaque type Longitude = Double
object Longitude:
  def apply(lon: Double): Longitude = lon

object LatitudeQueryParamMatcher extends QueryParamDecoderMatcher[Double]("lat")
object LongitudeQueryParamMatcher extends QueryParamDecoderMatcher[Double]("lon")

case class WeatherResponse(
    shortForecast: String,
    characterization: String
)
