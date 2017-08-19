package controllers

import com.google.inject.{Inject, Singleton}
import models.{AssignmentDb, AssignmentRepository,UserRepository}
import play.api.Logger
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, Controller, Request}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

@Singleton
class AdminController @Inject()(userRepository: UserRepository, val messagesApi: MessagesApi,
                                assignmentFill: AssignmentFill,assignmentRepository: AssignmentRepository
                               ) extends Controller with I18nSupport {

  def listOfUsers(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    request.session.get("email").fold(Future.successful(Redirect(routes.HomeController.index()))) {
      email =>
        request.session("isAdmin") match {
          case "false" =>
            Logger.error(email + "is not an Admin")
            Future.successful(Redirect(routes.CommonController.showProfileData()))
          case "true" =>
            val usersList = userRepository.getUsers()
            usersList.map {
              usersList =>
                Ok(views.html.userList(usersList))
            }
        }

    }
  }

  def enableDisableUser(email: String, isEnable: Boolean): Action[AnyContent] = Action.async {
    implicit request: Request[AnyContent] =>
      request.session.get("email").fold(
        Future.successful(Redirect(routes.HomeController.index()))) { checkUserRole =>
        request.session.get("isAdmin") match {
          case Some("false") => Future.successful(Redirect(routes.CommonController.showProfileData()))

          case Some("true") =>
            Logger.info("I am an Admin ")
            val mode = userRepository.changeMode(email, !isEnable)
            mode.map {
              case true =>
                Redirect(routes.AdminController.listOfUsers())

              case false => Ok("Could Not update changes")
            }
        }

      }

  }

  def addAssignment(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    request.session.get("email").fold(Redirect(routes.HomeController.index())) {
      _ =>
        Logger.info("Check if user is Admin")
        request.session("isAdmin") match {
          case "false" => Redirect(routes.CommonController.showProfileData())
          case "true" => Ok(views.html.addAssignment(assignmentFill.addAssignmentForm))
        }
    }
  }

  def addAssignmentBinding(): Action[AnyContent] = Action.async { implicit request: Request[AnyContent] =>
    request.session.get("email").fold(Future.successful(Redirect(routes.HomeController.index()))) {
      _ =>
        request.session("isAdmin") match {
          case "false" => Future.successful(Redirect(routes.CommonController.showProfileData()))
          case "true" =>
            assignmentFill.addAssignmentForm.bindFromRequest.fold(
              formWithErrors => {
                Logger.error("Could not Assignment" +formWithErrors)
                Future.successful(BadRequest(views.html.addAssignment(formWithErrors)))
              },
              assignmentInfo=> {
                Logger.info("Adding assignment")
                val addAssignment = assignmentRepository.addAssignment(AssignmentDb(assignmentInfo.title,
                  assignmentInfo.description))
                addAssignment.map {
                  case false => Ok("Something went wrong, Try again")
                  case true => Redirect(routes.AdminController.addAssignment())
                }
              })
        }
    }
  }


}
