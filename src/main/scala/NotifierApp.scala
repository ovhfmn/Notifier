import cats.effect.{Async, IO, IOApp}
import cats.syntax.all.catsSyntaxTuple2Parallel
import fs2.kafka.{ConsumerSettings, KafkaConsumer, commitBatchWithin}
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

import scala.concurrent.duration.DurationInt

object NotifierApp extends IOApp.Simple {

  given Logger[IO] = Slf4jLogger.getLogger[IO]

  def run: IO[Unit] =
    for {
      config <- AppConfig.load
      notifier = new EmailNotifier[IO]
      _ <- Logger[IO].info(s"Starting Notifier w/ config: $config")
      _ <- (runKafkaStream(config, notifier), runHealthCheck).parTupled
    } yield ()

  private def runKafkaStream(config: AppConfig, notifier: Notifier[IO]): IO[Unit] =
    KafkaConsumer.stream(consumerSettings[IO](config))
      .subscribeTo(config.topic)
      .records
      .through(PipeService.processPipe(notifier, config.concurrency))
      .through(commitBatchWithin(500, 15.seconds))
      .compile
      .drain

  private def runHealthCheck: IO[Unit] =
    IO.println("Health check live at ..:8081/health") >> IO.never

  private def consumerSettings[F[_] : Async](config: AppConfig): ConsumerSettings[F, String, String] =
    ConsumerSettings[F, String, String]
      .withBootstrapServers(config.kafkaBroker)
      .withGroupId("notifier-service-group")
}