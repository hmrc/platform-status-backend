/*
 * Copyright 2025 HM Revenue & Customs
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

import com.fasterxml.jackson.databind.{ObjectWriter, ObjectMapper}
import play.api.mvc.{AbstractController, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.metrics.Metrics

import java.io.StringWriter
import javax.inject.Inject

class MetricsController @Inject() (
  met: Metrics,
  controllerComponents: ControllerComponents
) extends AbstractController(controllerComponents):

  private val mapper: ObjectMapper = new ObjectMapper()

  private val logger = play.api.Logger(getClass)

  def toJson(met: Metrics): String = {
    val writer: ObjectWriter = mapper.writerWithDefaultPrettyPrinter()
    val stringWriter = new StringWriter()
    writer.writeValue(stringWriter, met.defaultRegistry)
    stringWriter.toString
  }

  def metrics = Action {
    Ok(toJson(met))
      .as("application/json")
      .withHeaders("Cache-Control" -> "must-revalidate,no-cache,no-store")
  }

  //import java.lang.management.*
  //val runtimeMxBean = ManagementFactory.getRuntimeMXBean();
  //logger.info("InputArguments" + runtimeMxBean.getInputArguments().toScala.filter(_.startsWith("-XX:")))

  val processors = Runtime.getRuntime.availableProcessors
  logger.info(s"Available processors: $processors")
