package models

import play.api.Application

import scala.concurrent.{Await, Future}
import scala.reflect.ClassTag
import play.api.test.WithApplicationLoader
import scala.concurrent.duration

class ModelsTest[T: ClassTag] extends WithApplicationLoader {
  lazy val app2dao = Application.instanceCache[T] // cache that class

  lazy val repository: T = app2dao(app)

  def result[R](response: Future[R]): R =
    Await.result(response, 2.seconds)
}


