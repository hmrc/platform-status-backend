/*
 * Copyright 2019 HM Revenue & Customs
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

import javax.inject.Singleton
import org.mongodb.scala._
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.ReplaceOptions
import play.api.Logger
import scala.concurrent.{ExecutionContext, Future}
import play.api.libs.concurrent.{Futures, Timeout}
import play.api.libs.concurrent.Futures._
import scala.concurrent.duration._


@Singleton
class StatusChecker () {

  val logger = Logger(this.getClass)

  val baseIteration3Status = PlatformStatus(name = "iteration 3",
    isWorking = true,
    description = "Call through to service in protected zone that can read/write to protected Mongo")

  val baseIteration4Status = PlatformStatus(name = "iteration 4",
    isWorking = true,
    description = "Call through to service in protected zone that can call out to internet via squid")

  val baseIteration5Status = PlatformStatus(name = "iteration 5",
    isWorking = true,
    description = "Call through to service in protected zone that can call a HOD via DES")



  def iteration3Status(dbUrl: String)(implicit executionContext: ExecutionContext, futures: Futures): Future[PlatformStatus] = {
    checkMongoConnection(dbUrl).withTimeout(2.seconds).recoverWith {
      case ex: Exception => {
        logger.warn("Failed to connect to Mongo")
        Future(baseIteration3Status.copy(isWorking = false, reason = Some(ex.getMessage)))
      }
    }
  }

  def iteration4Status() = baseIteration4Status.copy(isWorking = false, reason = Some("Test not yet implemented"))
  def iteration5Status() = baseIteration5Status.copy(isWorking = false, reason = Some("Test not yet implemented"))



//  def checkMongoConnection(dbUrl: String): Boolean = {
//    val mongoClient: MongoClient = MongoClient(dbUrl)
//    val database: MongoDatabase = mongoClient.getDatabase("platform-status-backend")
//    val collection: MongoCollection[Document] = database.getCollection("status");
//
//    val doc: Document = Document("_id" -> 0, "name" -> "MongoDB")
//    try {
////      collection.replaceOne(equal(fieldName = "_id", value = 0), doc, ReplaceOptions().upsert(true))
//      collection.replaceOne(equal(fieldName = "_id", value = 0), doc, ReplaceOptions().upsert(true)).results(secondsToWait = 2)
//    } catch {
//      case ex: Exception => {
//        Logger.warn("Failed to connect to Mongo")
//        throw new IllegalStateException("Failed to connect to MongoDB", ex)
//      }
//    }
//    Logger.info("Successfully connected to Mongo")
//    true
//  }

  private def checkMongoConnection(dbUrl: String)(implicit executionContext: ExecutionContext, futures: Futures): Future[PlatformStatus] = {
    val mongoClient: MongoClient = MongoClient(dbUrl)
    val database: MongoDatabase = mongoClient.getDatabase("platform-status-backend")
    val collection: MongoCollection[Document] = database.getCollection("status");
    val doc: Document = Document("_id" -> 0, "name" -> "MongoDB")

    for {
      _ <- collection.replaceOne(equal(fieldName = "_id", value = 0), doc, ReplaceOptions().upsert(true)).toFuture()
      result = baseIteration3Status
      // TODO - handle error states better
    } yield result
  }

}
