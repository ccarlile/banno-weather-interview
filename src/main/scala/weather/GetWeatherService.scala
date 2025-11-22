package weather

import cats.{Applicative, Functor}
import cats.implicits.*

trait GetWeatherService[F[_]] {
  def getWeather(lat: Latitude, lon: Longitude): F[Weather]
}

object GetWeatherService {
  def stub[F[_]: Applicative] = new GetWeatherService[F] {
    def getWeather(lat: Latitude, lon: Longitude): F[Weather] =
      Applicative[F].pure(Weather("Sunny", FeelsLike.Moderate))
  }

  def live[F[_]: Functor](client: WeatherAPIClient[F]) =
    new GetWeatherService[F] {
      def getWeather(lat: Latitude, lon: Longitude): F[Weather] =
        client.getForecast(lat, lon).map { fc =>
          Weather(fc.shortForecast, FeelsLike(fc.temperature))
        }
    }
}

case class Weather(
    condition: String,
    characterization: FeelsLike
)

enum FeelsLike:
  case Cold, Moderate, Hot

object FeelsLike:
  def apply(t: Int) =
    if (t < 70) Cold else if (t < 90) Moderate else Hot
