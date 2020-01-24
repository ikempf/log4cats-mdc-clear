package com.ikempf

import java.util.UUID

import cats.effect.Sync

object Uuid {

  def random[F[_]: Sync]: F[String] =
    Sync[F].delay(UUID.randomUUID().toString)

}
