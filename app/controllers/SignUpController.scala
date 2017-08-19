package controllers

import com.google.inject.{Inject, Singleton}
import models.{UserDb, UserRepository}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Controller, Request}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class SignUpController @Inject()(userRepository: UserRepository, signUpFill: SignUpFill,
                                 val messagesApi: MessagesApi) extends Controller with I18nSupport{

  implicit val messages = messagesApi

  def signUp(): Action[AnyContent] = Action { implicit request =>
      Ok(views.html.signUp(signUpFill.userSignUpFormConstraints))
  }

  def signUpBinding(): Action[AnyContent] = Action.async {
    implicit request: Request[AnyContent] =>
      signUpFill.userSignUpFormConstraints.bindFromRequest.fold(
        formWithErrors => {
          Logger.info("SignUp form Error" + formWithErrors)
          Future.successful(BadRequest(views.html.signUp(formWithErrors)))
        },
        signUpData => {
          Logger.info("New User" + signUpData)
          val checkEmail = userRepository.checkUserIfExist(signUpData.email)
          checkEmail.flatMap {
            case true =>
              Logger.error("User Already Registered")
              Future.successful(Redirect(routes.SignUpController.signUp()).flashing("error" -> "Your eamil is already Registered"))
            case false =>
              val newUserData = userRepository.store(UserDb(0,signUpData.firstName, signUpData.middleName,
                signUpData.lastName,signUpData.email, signUpData.mobileNumber,  signUpData.gender,
                signUpData.password, false, true))

              newUserData.map {
                case true => Redirect(routes.CommonController.showProfileData()).flashing(
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
