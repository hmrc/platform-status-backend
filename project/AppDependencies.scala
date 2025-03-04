import sbt._

object AppDependencies {

  private val bootstrapVersion = "9.11.0"

  val compile = Seq(
    "uk.gov.hmrc"       %% "bootstrap-backend-play-30" % bootstrapVersion,
    "org.mongodb.scala" %% "mongo-scala-driver"        % "5.3.1"          cross CrossVersion.for3Use2_13
  )

  val test = Seq(
    "uk.gov.hmrc"       %% "bootstrap-test-play-30"    % bootstrapVersion % Test
  )

}
