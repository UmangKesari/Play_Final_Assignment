package controllers

import javax.inject._

import models.{UserDb, UserRepository}
import play.api._
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.data.validation.{Constraint, Invalid, Valid, ValidationError}
import play.api.mvc._

import scala.concurrent.Future

/**
  * This controller creates an `Action` to handle HTTP requests to the
  * application's home page.
  */
class LogInController @Inject()(userRepository: UserRepository, val messagesApi: MessagesApi,
                                logInFill: LogInFill) extends Controller with I18nSupport {


  implicit val message = messagesApi

  def login(): Action[AnyContent] = Action { implicit request =>
    Ok(views.html.login(logInFill.logInForm))
  }

  def logInBinding() :Action[AnyContent] =Action.async{
    logInFill.logInForm.bindFromRequest.fold(
      formWithErrors =>{
        Logger.info("Login error " +formWithErrors)
        Future.successful(BadRequest(views.html.logIn(formWithErrors)))
      },
      logInData => {
        val userInfo = userRepository.getUserData(logInData.email)

        userInfo.map {
          case Some(userDb: UserDb) =>
            if(logInData.password === userDb.password && userDb.isEnable)
            {
              Logger.info( s"Welcome ${userDb.firstName} ${userDb.lastName}")
              Redirect(routes.UserLogIn.home()).flashing("success" -> "Successful Login").withSession(
                "email" -> userDb.email, "isAdmin" -> s"${userDb.isAdmin}")
            }
            else
            {
              Logger.error(" Error in logIn " +user.email)
              Redirect(routes.LogInController.login()).flashing("error" -> "log in error")
            }

          case None =>
            Logger.error("You are not registered")
            Redirect(routes.SignUpController.signUp()).flashing("error" -> "Invalid User")
        }

      }
    )
  }

  def updatePassword() :Action[AnyContent] = Action{ implicit request =>
    Ok(views.html.updatePassword(logInFill.updatePasswordForm))
  }

  def updatePasswordBinding(): Action[AnyContent] =Action.async {
    logInFill.updatePasswordForm.bindFromRequest.fold(
      formWithError => {
        Logger.error("updating password error" +formWithError)
        Future.successful(BadRequest(views.html.updatePassword(formWithError)))
      },
      updateUserPassword =>{
      Logger.info(updateUserPassword +" updating user passsword")
      val passwordInfo = userRepository.updateUserPassword(updateUserPassword.email,updateUserPassword.password)
      passwordInfo.map {
        case false => Redirect(routes.LogInController.login()).flashing("error" -> "user does not exist")
        case true =>
          Redirect(routes.LogInController.login())
            .flashing("success" -> "successfully changed password")
      }
      })

  }

}