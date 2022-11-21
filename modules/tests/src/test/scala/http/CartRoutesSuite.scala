package http

import cats.data.Kleisli
import cats.effect.IO
import domain.{ Auth, Cart, Item }
import domain.Cart.CartTotal
import domain.Item.ItemId
import http.auth.User.CommonUser
import http.routes.secured.CartRoutes
import org.http4s.server.AuthMiddleware
import org.http4s.Method._
import org.http4s._
import org.http4s.circe.CirceEntityEncoder._
import org.http4s.client.dsl.io._
import org.http4s.implicits.http4sLiteralsSyntax
import org.scalacheck.Gen
import services.ShoppingCart
import suits.HttpSuite
import shop.Generators._
import squants.market.USD

import java.util.UUID

object CartRoutesSuite extends HttpSuite {
  test("GET shopping cart succeed") {
    val gen = for {
      user      <- commonUserGen
      cartTotal <- cartTotalGen
    } yield user -> cartTotal

    forall(gen) {
      case (user, cartTotal) =>
        val request = GET(uri"/cart")
        val routes  = CartRoutes[IO](succeedCart(cartTotal)).routes(authMiddleware(user))

        expectedHttpBodyAndStatus(routes, request)(cartTotal, Status.Ok)
    }
  }

  test("POST add item to shopping cart succeed") {
    val gen = for {
      user <- commonUserGen
      cart <- cartGen
    } yield user -> cart

    forall(gen) {
      case (user, cart) =>
        val request = POST(cart, uri"/cart")
        val routes  = CartRoutes[IO](new TestShoppingCart).routes(authMiddleware(user))

        expectedHttpStatus(routes, request)(Status.Created)
    }
  }

  test("PUT update item in shopping cart succeed") {
    val gen = for {
      user <- commonUserGen
      cart <- cartGen
    } yield user -> cart

    forall(gen) {
      case (user, cart) =>
        val request = PUT(cart, uri"/cart")
        val routes  = CartRoutes[IO](new TestShoppingCart).routes(authMiddleware(user))

        expectedHttpStatus(routes, request)(Status.Ok)
    }
  }

  private def authMiddleware(authUser: CommonUser): AuthMiddleware[IO, CommonUser] =
    AuthMiddleware(Kleisli.pure(authUser))

  private def succeedCart(cartTotal: CartTotal): ShoppingCart[IO] = new TestShoppingCart {
    override def get(userId: Auth.UserId): IO[CartTotal] = IO.pure(cartTotal)
  }

  private class TestShoppingCart extends ShoppingCart[IO] {
    def add(userId: Auth.UserId, itemId: Item.ItemId, quantity: Cart.Quantity): IO[Unit] = IO.unit
    def get(userId: Auth.UserId): IO[CartTotal]                                          = IO.pure(CartTotal(List.empty, USD(0)))
    def delete(userId: Auth.UserId): IO[Unit]                                            = IO.unit
    def removeItem(userId: Auth.UserId, itemId: Item.ItemId): IO[Unit]                   = IO.unit
    def update(userId: Auth.UserId, cart: Cart): IO[Unit]                                = IO.unit
  }
}
