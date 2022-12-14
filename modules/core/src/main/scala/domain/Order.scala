package domain

import cats.Show
import domain.Cart.Quantity
import domain.Item.ItemId
import domain.Order.{ OrderId, PaymentId }
import io.circe.Encoder
import io.circe.generic.semiauto.deriveEncoder
import monocle.Iso
import optics.IsUUID
import squants.market.Money

import java.util.UUID
import scala.util.control.NoStackTrace

case class Order(
  id: OrderId,
  pid: PaymentId,
  items: Map[ItemId, Quantity],
  total: Money
)
object Order {
  final case class OrderId(uuid: UUID)

  final case class PaymentId(uuid: UUID)

  implicit val orderIdShow: Show[OrderId]     = (orderId: OrderId) => orderId.toString
  implicit val paymentIdShow: Show[PaymentId] = (paymentId: PaymentId) => paymentId.toString

  implicit val isOrderId: IsUUID[OrderId] = new IsUUID[OrderId] {
    def _UUID: Iso[UUID, OrderId] = Iso[UUID, OrderId](uuid => OrderId(uuid))(orderId => orderId.uuid)
  }

  implicit val orderIdEncoder: Encoder[OrderId]     = deriveEncoder[OrderId]
  implicit val paymentIdEncoder: Encoder[PaymentId] = deriveEncoder[PaymentId]
  implicit val orderEncoder: Encoder[Order]         = deriveEncoder[Order]

  case object EmptyCartError extends NoStackTrace

  sealed trait OrderOrPaymentError extends NoStackTrace {
    def cause: String
  }

  final case class OrderError(cause: String) extends OrderOrPaymentError

  final case class PaymentError(cause: String) extends OrderOrPaymentError
}
