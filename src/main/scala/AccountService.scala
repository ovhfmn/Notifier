import cats.effect.IO
import com.httpService.http.AccountEvent
import fs2.kafka.{KafkaProducer, ProducerRecord, ProducerRecords, ProducerSettings}
import io.circe.syntax.*

object AccountService {
  def produceEvent(broker: String, topic: String, event: AccountEvent): IO[Unit] = {
    val settings = ProducerSettings[IO, String, String]
      .withBootstrapServers(broker)

    KafkaProducer.resource(settings).use { producer =>
      val key = event match {
        case e: AccountEvent.AccountCreated => e.id
        case e: AccountEvent.MoneyDebited => e.id
        case e: AccountEvent.MoneyCredited => e.id
      }
      val record = ProducerRecord(topic, key, event.asJson.noSpaces)
      producer.produce(ProducerRecords.one(record))
    }.flatten.void
  }
}
