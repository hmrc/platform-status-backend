/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.gov.hmrc.platformstatusbackend.services

import com.google.inject.Inject
import org.mongodb.scala._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.ReplaceOptions
import play.api.Logger
import play.api.libs.concurrent.Futures
import play.api.libs.concurrent.Futures._
import uk.gov.hmrc.http.client.HttpClientV2
import uk.gov.hmrc.http.{HeaderCarrier, HttpReads, HttpResponse, StringContextOps}
import uk.gov.hmrc.platformstatusbackend.config.AppConfig

import javax.inject.Singleton
import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}


@Singleton
class StatusChecker @Inject()(
  httpClientV2: HttpClientV2,
  appConfig   : AppConfig
):
  import uk.gov.hmrc.http.HttpReads.Implicits._

  private val logger = Logger(this.getClass)

  val baseIteration3Status =
    PlatformStatus(
      enabled     = true,
      name        = "iteration 3",
      isWorking   = true,
      description = "Call through to service in protected zone that can read/write to protected Mongo"
    )

  val baseIteration5Status =
    PlatformStatus(
      enabled     = true,
      name        = "iteration 5",
      isWorking   = true,
      description = "Call through to service in protected zone that can call a HOD via DES"
    )

  def iteration3Status()(using ExecutionContext, Futures): Future[PlatformStatus] =
    try
      checkMongoConnection(appConfig.dbUrl)
        .withTimeout(2.seconds)
        .recoverWith:
          case ex: Exception =>
            logger.warn("Failed to connect to Mongo", ex)
            genericError(baseIteration3Status, ex)
    catch
      case ex: Exception => genericError(baseIteration3Status, ex)

  def iteration5Status()(using ExecutionContext, Futures, HeaderCarrier): Future[PlatformStatus] =
    try
      checkDesHealthcheck(appConfig)
        .withTimeout(2.seconds)
        .recoverWith:
          case ex: Exception =>
            logger.warn("Failed to connect to Des Healthcheck", ex)
            genericError(baseIteration5Status, ex)
    catch
      case ex: Exception => genericError(baseIteration5Status, ex)

  private def checkMongoConnection(dbUrl: String)(using ExecutionContext): Future[PlatformStatus] =
    val collection: MongoCollection[Document] =
      MongoClient(dbUrl).getDatabase("platform-status-backend").getCollection("status")

    val doc: Document = Document("_id" -> 0, "name" -> "MongoDB")

    for {
      _      <- collection.replaceOne(equal(fieldName = "_id", value = 0), doc, ReplaceOptions().upsert(true)).toFuture()
      result =  baseIteration3Status
      // TODO - handle error states better
    } yield result

  private def checkDesHealthcheck(appConfig: AppConfig)(using ExecutionContext, HeaderCarrier): Future[PlatformStatus] =
    httpClientV2
      .get(url"${appConfig.desBaseUrl}/health-check-des")
      .setHeader(
        "Authorization" -> s"Bearer ${appConfig.desAuthToken}",
        "Environment"   -> appConfig.desEnvironment
      )
      .execute[HttpResponse]
      .map { response =>
        response.status match {
          case 200 =>
            logger.info("Successful DES health-check: " + response.json.toString())
            baseIteration5Status
          case x =>
            val reasons =
              List(s"statusCode: $x") ++
              response.header("CorrelationId").map(id => s"CorrelationId: $id") ++
              List(s"body: ${response.body}")
            logger.warn("Unsuccessful DES health-check: " + reasons.mkString(","))
            baseIteration5Status.copy(isWorking = false)
      }
    }

  private def genericError(status: PlatformStatus, ex: Exception): Future[PlatformStatus] =
    Future.successful(status.copy(isWorking = false, reason = Some(ex.getMessage)))
