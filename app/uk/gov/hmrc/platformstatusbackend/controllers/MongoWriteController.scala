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

package uk.gov.hmrc.platformstatusbackend.controllers

import javax.inject.{Inject, Singleton}
import play.api.libs.concurrent.Futures
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.platformstatusbackend.config.AppConfig
import uk.gov.hmrc.platformstatusbackend.services.StatusChecker
import uk.gov.hmrc.play.bootstrap.controller.BackendController
import play.api.libs.json.Json.toJson
import play.api.libs.json._

import scala.concurrent.{ExecutionContext, Future}

@Singleton()
class MongoWriteController @Inject()(appConfig: AppConfig, cc: ControllerComponents, statusChecker: StatusChecker)(implicit executionContext: ExecutionContext, futures: Futures)
    extends BackendController(cc) {

  def iteration3(): Action[AnyContent] = Action.async { implicit request =>
    for {
      status <- statusChecker.iteration3Status(appConfig.dbUrl)
    } yield Ok(toJson(status))
  }
}
