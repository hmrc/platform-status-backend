import sbt._

object AppDependencies {

  private val bootstrapVersion = "8.3.0"

  val compile = Seq(
    "uk.gov.hmrc"             %% "bootstrap-backend-play-30" % bootstrapVersion,
    "org.mongodb.scala"       %% "mongo-scala-driver"        % "4.11.1"
  )

  val test = Seq(
    "uk.gov.hmrc"             %% "bootstrap-test-play-30"    % bootstrapVersion % Test
  )

}
