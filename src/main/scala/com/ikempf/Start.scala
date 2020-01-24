package com.ikempf

import cats.Parallel
import cats.effect.{ExitCode, IO, IOApp}
import cats.implicits._
import io.chrisdavenport.log4cats.slf4j.Slf4jLogger

import scala.concurrent.duration._

object Start extends IOApp {
  val logger = Slf4jLogger.fromName[IO]("logger").unsafeRunSync()
  val delay = 200.milliseconds

  val loop1: IO[Unit] =
    logger
      .info("Without correlation id")
      .productR(IO.sleep(delay))
      .productR(IO.suspend(loop1))

  val loop2: IO[Unit] =
    Uuid
      .random[IO]
      .flatMap(uuid =>
        IO.race(
          logger.info("Not settings correlation id"),
          logger.info(Map("CorrelationId" -> uuid))("Settings correlation id"))
      )
      .productR(IO.sleep(delay))
      .productR(IO.suspend(loop2))

  override def run(args: List[String]): IO[ExitCode] =
    Parallel
      .parTuple2(loop1, loop2)
      .as(ExitCode.Success)

}
