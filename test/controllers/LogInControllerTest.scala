package controllers

import models.{LogInRepository, UserRepository}
import org.scalatest.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.i18n.MessagesApi
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.test.WithApplication


class LogInControllerTest extends PlaySpec with MockitoSugar {

  val mockedUserRepository = mock[UserRepository]
  val messageApi = mock[MessagesApi]
  val mockUserLogInForm = mock[UserLogIn]
  //val signUpControllerObj = new SignUpController()
  val logInControllerObj = new LogInController(mockedUserRepository,messageApi)


  "LogIn Controller" should {
    "be able to log In successfully" in {

      val result =logInControllerObj.checkUserSignIn.apply(FakeRequest(POST,"/userLogIn").withFormUrlEncodedBody(
        "email" -> "umang.kesar@knoldus.in", "password" ->"knoldus123"))

      /*val result = logInControllerObj.checkUserSignIn()(FakeRequest(POST,"/").withFormUrlEncodedBody(
        "email" -> "umang.kesar@knoldus.in", "password" ->"knoldus123"))*/

      redirectLocation(result) mustBe Some("/userSuccessLogIn")
    }
  }


}
