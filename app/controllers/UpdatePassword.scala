package controllers

import play.api.data.Form
import play.api.data.Forms._

case class UpdatePassword(email :String, password :String, confirmPassword :String)

class UpdatePasswordFill {
  val passwordConstraint = new PasswordConstraint

  val updatePasswordForm = Form(mapping(
    "emailId" -> email,
    "password" -> nonEmptyText.verifying(passwordConstraint.passwordCheckConstraint),
    "confirmPasssword" -> nonEmptyText.verifying(passwordConstraint.passwordCheckConstraint)
  )(UpdatePasswordFill.apply)(UpdatePasswordFill.unapply)
    .verifying("Passwords do not match", matchPassword =>{
      matchPassword.password == matchPassword.confirmPassword
    }))

}
