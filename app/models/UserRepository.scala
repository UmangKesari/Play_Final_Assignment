package models

import javax.inject._

import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import scala.concurrent.Future

case class UserDb(user_id : Int, firstName: String, middleName : Option[String], lastName: String, email : String,
                  mobileNumber : Long, gender : String, password : String,isAdmin :Boolean,isEnable: Boolean )

case class LogInDb(email : String, password : String)

trait UserTrait extends HasDatabaseConfigProvider[JdbcProfile]
{

  import driver.api._
  val userQuery :TableQuery[UserQuery] = TableQuery[UserQuery] // LogIn below defined class, which should be present inside the trait

  class UserQuery (tag :Tag) extends Table[UserDb](tag,"user_details") { // UserSignUpDb is case class which should be similar to database table of sign up

    def userId :Rep[Int] = column[Int]("user_id")

    def firstName :Rep[String] = column[String]("firstname")

    def middleName : Rep[String] = column[String]("middlename")

    def lastName : Rep[String] = column[String]("lastname")

    def email : Rep[String]= column[String]("email")

    def mobileNumber : Rep[Long]= column[Long]("mobilenumber")

    def gender : Rep[String]= column[String]("gender")

    def password : Rep[String]= column[String]("password")

    def isAdmin : Rep[Boolean]= column[Boolean]("isAdmin")

    def isEnable : Rep[Boolean]= column[Boolean]("isEnable")

    def * = (userId,firstName,middleName,lastName,email,mobileNumber,gender,password,isAdmin,isEnable) <>
      (UserDb.apply,UserDb.unapply)
  }
}

class UserRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
                                extends UserTrait {
  /**
    *  Storing User details while Sign Up
    * @param userDb
    *
    */
  def store(userDb: UserDb) :Future[Boolean] =
    db.run(userQuery += userDb).map(_>0)

  def checkUserIfExist(email :String) : Future[Boolean] = {
    db.run(userQuery.filter(_.email === email).result).map {
      user => user.headOption.fold(false)(_ => true)
    }
  }

  def getUserData(email :String) : Future[Option[UserDb]] = {
    db.run(userQuery.filter(user => user.email === email).result.headOption)
  }

  def updateUserPassword(email : String, password :String) :Future[Boolean] ={
    db.run(userQuery.filter(_.email === email).map(_.password).update(password)).map (_ > 0)
  }

  def changeMode(email :String, change :Boolean) : Future[Boolean] ={
    db.run(userQuery.filter(_.email === email).map{
      _.isEnable
    }.update(change)).map (_ > 0)
  }

  def getUsers(): Future[List[UserDb]] = {
    db.run(userQuery.filter(_.isAdmin === false).to[List].result)
  }

  def updateUserProfile(userDb: UserDb): Future[Boolean] = {
    db.run(userQuery.filter(_.email === userDb.email).map(user => (user.firstName, user.middleName, user.lastName,
      user.mobileNumber).update(userDb.firstName, userDb.middleName, userDb.lastName, userDb.mobileNumber) map (_ > 0)
  }

  def checkUserLogIn(email : String, password : String) : Future[Boolean] = {
    val user = db.run(userQuery.filter(_.email === email)).to[List].result
    user.map{ usr =>
      if(usr.isEmpty){
        false
      }
      else if(_.password === password){
        true
      }
    }
  }
}




