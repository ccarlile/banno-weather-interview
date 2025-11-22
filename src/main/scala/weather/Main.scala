package weather

import cats.effect.{ExitCode, IO, IOApp}
import cats.effect.std.Console

object Main extends IOApp:
  def run(args: List[String]): IO[ExitCode] =
    Console[IO].println("Starting App...") *>
      Server().use(_ => IO.never).as(ExitCode.Success)
