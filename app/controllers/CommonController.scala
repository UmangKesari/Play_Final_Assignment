package controllers

import javax.inject._

import scala.concurrent.ExecutionContext.Implicits.global
import com.google.inject.Singleton
import models.{AssignmentRepository, UpdateUserDb, UserRepository}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Controller, Request}

import scala.concurrent.Future

@Singleton
class CommonController @Inject()(val messagesApi: MessagesApi, userRepository: UserRepository,
                                 userProfileFill: UserProfileFill, assignmentRepository: AssignmentRepository)
  extends Controller with I18nSupport {

  implicit val message = messagesApi

  def showProfileData(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>

    request.session.get("email").fold(Future.successful(Redirect(routes.HomeController.index()))) {
      email =>
        val userData = userRepository.getUserData(email)
        userData.map {
          case Some(userData) =>
            Ok(views.html.userOrAdmin(userProfileFill.userProfileForm.fill(UserProfile(
              userData.firstName, userData.middleName, userData.lastName, userData.mobileNumber,userData.gender))))

          case None =>
            Redirect(routes.SignUpController.signUp())
        }
    }
  }

  def displayAssignments(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    request.session.get("email").fold(Future.successful(Redirect(routes.HomeController.index()))) {

      _ =>
        Logger.info("displaying list of assignments")
        val assignments = assignmentRepository.getAssignmentList()
        assignments.map {
          assignPart =>
            Logger.info("List Assignments " + assignPart)
            Ok(views.html.assignmentPage(assignPart))
        }
    }
  }

  def deleteAssignment(assignmentId: Int): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    request.session.get("email").fold(Future.successful(Redirect(routes.HomeController.index()))) {
      _ =>
        Logger.info("At delete assignment")
        request.session("isAdmin") match {
          case "false" =>
            Future.successful(Redirect(routes.CommonController.showProfileData()))
          case "true" =>
            Logger.info("Removing assignment with id " + assignmentId)
            val remove = assignmentRepository.removeAssignment(assignmentId)
            remove.map {
              case true =>
                Redirect(routes.CommonController.displayAssignments())
              case false =>
                Ok("Something went wrong, try again")
            }
        }
    }
  }

  def updateProfile(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    request.session.get("email").fold(Future.successful(Redirect(routes.HomeController.index()))) {
      email =>
        Logger.info("Updating Profile of" + email)
        userProfileFill.userProfileForm.bindFromRequest.fold(
          formWithErrors => {
            Logger.error("Form With Errors" + formWithErrors)
            Future.successful(BadRequest(views.html.userOrAdmin(formWithErrors)))
          },
          modifiedUserData => {
            Logger.info("Updating your Data " + modifiedUserData)
            val updateUserData = userRepository.updateUserProfile(UpdateUserDb(
              modifiedUserData.firstName, modifiedUserData.middleName, modifiedUserData.lastName,
              modifiedUserData.mobileNumber),email)
            updateUserData.map{
              case false => Ok("Something went wrong, Try again")
              case true => Redirect(routes.CommonController.showProfileData())

            }
          })
    }
  }
}