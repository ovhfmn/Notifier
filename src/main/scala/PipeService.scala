import cats.effect.Async
import cats.implicits.*
import com.httpService.http.AccountEvent
import fs2.Pipe
import fs2.kafka.{CommittableConsumerRecord, CommittableOffset}
import io.circe.parser.decode
import org.typelevel.log4cats.Logger

object PipeService {
  def processPipe[F[_] : Async : Logger](
                                          notifier: Notifier[F],
                                          concurrency: Int
                                        ): Pipe[F, CommittableConsumerRecord[F, String, String], CommittableOffset[F]] = {
    _.mapAsync(concurrency) { commitable =>
      val record = commitable.record
      decode[AccountEvent](commitable.record.value) match {
        case Right(event) => notifier.send(event).as(commitable.offset)
        case Left(err) =>
          Logger[F].error(s"Failed to decode: ${record.value} - Error: $err") >>
            Async[F].pure(commitable.offset)
      }
    }
  }
}
