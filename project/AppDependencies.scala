import play.core.PlayVersion.current
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  val compile = Seq(

    "uk.gov.hmrc"             %% "bootstrap-backend-play-28" % "5.14.0",
    "org.mongodb.scala"       %% "mongo-scala-driver"        % "4.3.2"
  )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-28"   % "5.14.0" % "test",
    "org.pegdown"             %  "pegdown"                  % "1.6.0"  % "test, it"
  )

}
