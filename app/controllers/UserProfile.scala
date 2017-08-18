package controllers

import play.api.data.Form
import play.api.data.Forms._

case class UserProfile(firstName: String, middleName: Option[String],lastName: String, mobileNumber: Long,
                       gender: String)

case class UserList(firstName: String, middleName: Option[String],lastName: String, email :String,
                    isEnable : Boolean)

class UserProfileFill {

  val userProfileForm = Form(mapping(
    "firstName" -> nonEmptyText,
    "middleName" -> optional(text),
    "lastName" -> nonEmptyText,
    "mobileNumber" -> longNumber,
    "gender" -> nonEmptyText
  )(UserProfile.apply)(UserProfile.unapply))

}


