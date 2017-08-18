package controllers

import play.api.data.Form
import play.api.data.Forms._

case class Assignment(title : String, description : String)

class AssignmentFill {
  val addAssignmentForm = Form(mapping(
    "title"->nonEmptyText,
    "description"->nonEmptyText
  )(Assignment.apply)(Assignment.unapply))

}
