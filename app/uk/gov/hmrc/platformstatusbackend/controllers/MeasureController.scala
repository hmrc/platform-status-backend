/*
 * Copyright 2020 HM Revenue & Customs
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

import java.nio.charset.StandardCharsets

import javax.inject.{Inject, Singleton}
import play.api.Logger
import play.api.libs.concurrent.Futures
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.platformstatusbackend.services.StatusChecker
import uk.gov.hmrc.play.bootstrap.controller.BackendController

import scala.concurrent.ExecutionContext

@Singleton()
class MeasureController @Inject()(cc: ControllerComponents, statusChecker: StatusChecker)(implicit executionContext: ExecutionContext, futures: Futures)
    extends BackendController(cc) {

  val logger: Logger = Logger(this.getClass)

  def measureRequest(): Action[AnyContent] = Action { implicit request =>
    val host = request.headers.get(HOST).getOrElse("?")
    val contentLength = request.headers.get(CONTENT_LENGTH).map(_.toInt).getOrElse(-1)

    val testHeader = (for {
      name <- request.headers.get("X-Test-Header-Name")
      testHeader <- request.headers.get(name)
      byteSize = testHeader.getBytes(StandardCharsets.UTF_8).length
    } yield s", test header '$name' length: $byteSize").getOrElse("")

    val msg = s"Received request from $host, body length: $contentLength" + testHeader
    logger.info(msg)
    Ok(msg)
  }
}
