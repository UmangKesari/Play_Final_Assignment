package controllers

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}

case class LogIn(email: String, password: String)

class LogInFill {

  val logInForm = Form(mapping(
    "email" -> email,
    "password" -> nonEmptyText
  )(LogIn.apply)(LogIn.unapply)
  )




}