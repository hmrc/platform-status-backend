import uk.gov.hmrc.DefaultBuildSettings.integrationTestSettings
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings

lazy val microservice = Project("platform-status-backend", file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .settings(
    majorVersion        := 0,
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    scalaVersion        := "3.3.5"
  )
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(PlayKeys.playDefaultPort := 8463)
  .settings(scalacOptions ++= Seq(
    "-Wconf:msg=unused&src=.*routes/.*:s"
  , "-Wconf:msg=Flag.*repeatedly:s"
  ))
