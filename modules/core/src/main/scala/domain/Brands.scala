package domain

import eu.timepit.refined.types.string.NonEmptyString
import io.circe.generic.semiauto.deriveEncoder
import io.circe.refined.{ refinedDecoder, refinedEncoder }
import io.circe.{ Decoder, Encoder }

import java.util.UUID

object Brands {
  case class BrandId(value: UUID)
  case class BrandName(value: String)

  case class BrandParam(value: NonEmptyString) {
    def toDomain: BrandName = BrandName(value.value.toLowerCase.capitalize)
  }

  object BrandParam {
    implicit val jsonEncoder: Encoder[BrandParam] =
      Encoder.forProduct1("name")(_.value)

    implicit val jsonDecoder: Decoder[BrandParam] =
      Decoder.forProduct1("name")(BrandParam.apply)
  }

  final case class Brand(uuid: BrandId, name: BrandName)
  object Brand {
    implicit val brandNameEncoder: Encoder[BrandName] = deriveEncoder[BrandName]
    implicit val brandIdEncoder: Encoder[BrandId]     = deriveEncoder[BrandId]
    implicit val brandEncoder: Encoder[Brand]         = deriveEncoder[Brand]
  }
}
