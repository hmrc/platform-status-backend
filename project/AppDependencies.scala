import sbt._

object AppDependencies {

  private val bootstrapVersion = "5.23.0"

  val compile = Seq(
    "uk.gov.hmrc"             %% "bootstrap-backend-play-28" % bootstrapVersion,
    "org.mongodb.scala"       %% "mongo-scala-driver"        % "4.3.2"
  )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-28"    % bootstrapVersion % Test
  )

}
