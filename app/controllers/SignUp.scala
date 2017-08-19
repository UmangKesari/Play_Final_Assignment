package controllers

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}

case class SignUp(firstName: String, middleName : Option[String], lastName: String, email : String,
                      mobileNumber : Long, gender : String, password : String,confirmPassword : String)

class SignUpFill{

  val passwordConstraint = new PasswordConstraint

  val userSignUpFormConstraints = Form(mapping(
    "firstName" -> nonEmptyText,
    "middleName" -> optional(text),
    "lastName" -> nonEmptyText,
    "email" -> email,
    "mobileNumber" -> longNumber,
    "gender" -> nonEmptyText,
    "password" -> nonEmptyText.verifying(passwordConstraint.passwordCheckConstraint),
    "confirmPassword" ->nonEmptyText.verifying(passwordConstraint.passwordCheckConstraint)
    )(SignUp.apply)(SignUp.unapply).verifying("Passwords do not match", matchPassword =>{
    matchPassword.password == matchPassword.confirmPassword
  }
  ))

}
