package controllers

import javax.inject._

import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc._

@Singleton
class HomeController @Inject()(val messagesApi: MessagesApi) extends Controller with I18nSupport {

  implicit val message = messagesApi

  def index(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Ok(views.html.index())
  }

  def login(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Redirect(routes.LogInController.login())
  }

  def signUp(): Action[AnyContent] = Action { implicit request: Request[AnyContent] =>
    Redirect(routes.SignUpController.signUp())
  }

}
