package controllers

import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}

import scala.util.matching.Regex

class PasswordConstraint {

  private val passwordRegex ="""(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\S+$).{8,}""".r
  val passwordCheckConstraint: Constraint[String] =
    Constraint("constraints.passwordCheck")({
      {
        case passwordRegex() => Invalid(ValidationError("Password is all numbers or letter"))
        case _ => Valid
      }
    })
}
