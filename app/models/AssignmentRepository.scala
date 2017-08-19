package models

import com.google.inject.{Inject, Singleton}
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.lifted.ProvenShape
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class AssignmentDb(title: String, description: String,assign_id: Int = 1)

trait AssignmentTrait extends HasDatabaseConfigProvider[JdbcProfile] {

  import driver.api._

  val assignmentQuery: TableQuery[AssignmentQuery] = TableQuery[AssignmentQuery]

  class AssignmentQuery(tag: Tag) extends Table[AssignmentDb](tag, "assignments") {

    def * : ProvenShape[AssignmentDb] = (title, description,assign_id) <>
      (AssignmentDb.tupled, AssignmentDb.unapply)

    def assign_id: Rep[Int] = column[Int]("assign_id", O.PrimaryKey, O.AutoInc)

    def title: Rep[String] = column[String]("title")

    def description: Rep[String] = column[String]("description")
  }

}

@Singleton
class AssignmentRepository @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
  extends AssignmentTrait {
    /**
      * Adding Assignments
      */

    import driver.api._

    def addAssignment(assignmentDb: AssignmentDb): Future[Boolean] = {
      db.run(assignmentQuery += assignmentDb).map(_ > 0)
    }

    /**
      * Retrieving Assignment list
      */


    def getAssignmentList(): Future[List[AssignmentDb]] = {
      db.run(assignmentQuery.to[List].result)
    }

    /**
      * remove Assignment
      */

    def removeAssignment(assignmentId: Int): Future[Boolean] = {
      db.run(assignmentQuery.filter(_.assign_id === assignmentId).delete).map(_ > 0)
    }
  }
