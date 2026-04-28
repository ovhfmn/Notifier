import cats.effect.Async
import cats.implicits.*
import com.httpService.http.AccountEvent
import org.typelevel.log4cats.Logger

import scala.concurrent.duration.DurationInt

trait Notifier[F[_]] {
  def send(event: AccountEvent): F[Unit]
}

class EmailNotifier[F[_] : Async : Logger] extends Notifier[F] {

  override def send(event: AccountEvent): F[Unit] =
    def attempt(retries: Int): F[Unit] = {
      Logger[F].info(s"Attempting to send email (Retries left $retries)") >>
        Async[F].realTime.map(_.toMillis % 5 == 0).flatMap {
          case true if retries > 0 =>
            Logger[F].warn("Faked Network Error!") >>
              Async[F].sleep(1.second) >>
              attempt(retries - 1)
          case true => Async[F].raiseError(new RuntimeException("Email service exhausted"))
          case false => Logger[F].info(s"Email sent successfully for $event")
        }
    }

    attempt(3)
}