import cats.effect.IO
import cats.syntax.parallel.*
import ciris.*

case class AppConfig(
                      kafkaBroker: String,
                      topic: String,
                      concurrency: Int
                    )

object AppConfig {
  def load: IO[AppConfig] = (
    env("KAFKA_BROKER").as[String].default("localhost:9092"),
    env("KAFKA_TOPIC").as[String].default("account-events"),
    env("CONCURRENCY").as[Int].default(16)
  ).parMapN(AppConfig.apply).load[IO]
}
