package controllers

import com.google.inject.{Inject, Singleton}
import models.{UserDb, UserRepository}
import play.api.Logger
import play.api.i18n.MessagesApi
import play.api.mvc.{Action, Request}
import play.mvc.BodyParser.AnyContent

import scala.concurrent.Future

@Singleton
class SignUpController @Inject()(userRepository: UserRepository, signUpFill: SignUpFill,
                                 val messagesApi: MessagesApi) {

  implicit val messages = messagesApi

  def signUp(): Action[AnyContent] = Action.async {
    implicit request =>
      Ok(views.html.signup(signUpFill.userSignUpFormConstraints))
  }

  def signUpBinding(): Action[AnyContent] = Action.async {
    implicit request: Request[AnyContent] =>
      signUpFill.userSignUpFormConstraints.bindFromRequest.fold(
        formWithErrors => {
          Logger.info("SignUp form Error" + formWithErrors)
          Future.successful(BadRequest(views.html.signup(formWithErrors)))
        },
        signUpData => {
          Logger.info("New User" + signUpData)
          val checkEmail = userRepository.checkUserIfExist(signUpData.email)
          checkEmail.flatmap {
            case true =>
              Logger.error("User Already Registered")
              Future.successful(Redirect(routes.SignUpController.signUp())
                .flashing("error" -> "Your eamil is already Registered"))

            case false =>
              val newUserData = userRepository.store(UserDb(newUserData.firstName, newUserData.middleName,
                newUserData.lastName, newUserData.mobileNumber, userData.email, userData.gender,
                newUserData.password, false, true))

              newUserData.map {
                case true => Redirect(routes.CommonController.home()).flashing(
                  "success" -> "You have successfully registered").withSession(
                  "email" -> signUpData.email, "isAdmin" -> "false"
                )

                case false => Ok("Error while  registraiton")
              }
          }
        }
      )
  }



}
