package controllers

import javax.inject._

import com.google.inject.Singleton
import models.{AssignmentRepository, UserDb, UserRepository, UserWithoutPassword}
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
        userData.flatMap {
          case Some(userData) =>
            Ok(views.html.userOrAdmin(userProfileFill.userProfileForm.fill(UserProfile(
              userData.firstName, userData.middleName, userData.lastName, userData.mobileNumber))))

          case None =>
            Future.successful(Redirect(routes.SignUpController.signUp()))
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
            Future.successful(Redirect(routes.CommonController.home()))
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
            BadRequest(views.html.userOrAdmin(formWithErrors))
          },
          modifiedUserData => {
            Logger.info("Updating your Data " + modifiedUserData)
            val updateUserData = userRepository.updateUserData(UserDb(
              modifiedUserData.firstName, modifiedUserData.middleName, modifiedUserData.lastName,
              modifiedUserData.mobileNumber))
            updateUserData.map{
              case false => Ok("Something went wrong, Try again")
              case true => Redirect(routes.CommonController.addAssignment())

            }
          }
    })
  }
}