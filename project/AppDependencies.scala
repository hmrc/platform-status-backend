import sbt._

object AppDependencies {

  private val bootstrapVersion = "9.4.0"

  val compile = Seq(
    "uk.gov.hmrc"       %% "bootstrap-backend-play-30" % bootstrapVersion,
    "org.mongodb.scala" %% "mongo-scala-driver"        % "5.1.2"          cross CrossVersion.for3Use2_13
  )

  val test = Seq(
    "uk.gov.hmrc"       %% "bootstrap-test-play-30"    % bootstrapVersion % Test
  )

}
