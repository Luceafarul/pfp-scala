package http.auth

import dev.profunktor.auth.jwt.JwtSymmetricAuth
import domain.Auth.UserId
import domain.Users.{ EncryptedPassword, UserName }

object Users {
  final case class AdminJwtAuth(value: JwtSymmetricAuth)
  final case class UserJwtAuth(value: JwtSymmetricAuth)

  final case class User(id: UserId, name: UserName)
  final case class UserWithPassword(id: UserId, name: UserName, password: EncryptedPassword)

  final case class CommonUser(user: User)
  final case class AdminUser(user: User)
}
