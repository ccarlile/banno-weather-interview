package weather

import cats.effect.Concurrent
import cats.implicits.*
import io.circe.generic.auto.*
import org.http4s.circe.CirceEntityCodec.*
import org.http4s.client.Client
import org.http4s.implicits.*
import org.http4s.Uri

trait WeatherAPIClient[F[_]] {
  def getForecast(lat: Latitude, lon: Longitude): F[ForecastResponsePeriod]
}

object WeatherAPIClient {
  def impl[F[_]: Concurrent](client: Client[F]) = new WeatherAPIClient[F] {

    val apiBase = uri"https://api.weather.gov"

    def getForecast(lat: Latitude, lon: Longitude): F[ForecastResponsePeriod] =
      for
        gpr <- client.expect[GridpointResponse](apiBase / "points" / s"$lat,$lon")
        forecastUrl <- Uri.fromString(gpr.properties.forecast).liftTo[F]
        forecastResponse <- client.expect[ForecastResponse](gpr.properties.forecast)
        forecast <- forecastResponse.properties.periods.headOption
          .liftTo[F](new RuntimeException("Empty Forecast from NWS"))
      yield forecast

  }
}

case class GridpointResponse(properties: GridpointResponseProperties)
case class GridpointResponseProperties(forecast: String)

case class ForecastResponse(properties: ForecastResponseProperties)
case class ForecastResponseProperties(periods: List[ForecastResponsePeriod])
case class ForecastResponsePeriod(temperature: Int, shortForecast: String)
