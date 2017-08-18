package models

import org.specs2.Specification

class LogInRepositoryTest extends Specification{
  val logInRepo = new ModelsTest[LogInRepository]

  private val userLogIn = LogIn("umang.kesari@knoldus.in", "knoldus123")

  "LogInRepository " should {
    "user logged in successfully " in{
      val storeResult = logInRepo.result(logInRepo.repository.store(userLogIn))
      storeResult must equalTo(true)
    }
  }

}
