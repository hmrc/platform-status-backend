/*
 * Copyright 2021 HM Revenue & Customs
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

import com.sun.management.VMOption
import play.api.libs.json.Json.toJson
import play.api.mvc.{Action, AnyContent, ControllerComponents}
import uk.gov.hmrc.play.bootstrap.backend.controller.BackendController

import javax.inject.{Inject, Singleton}
import scala.collection.SortedMap

@Singleton
class PrintVmDiagnosticController @Inject()(cc: ControllerComponents)
  extends BackendController(cc) {

  def printVmOptions(): Action[AnyContent] = Action { request =>

    import scala.collection.JavaConverters._

    val flagClass = Class.forName("sun.management.Flag")
    val getAllFlagsMethod = flagClass.getDeclaredMethod("getAllFlags")
    val getVMOptionMethod = flagClass.getDeclaredMethod("getVMOption")
    getAllFlagsMethod.setAccessible(true)
    getVMOptionMethod.setAccessible(true)
    val result = getAllFlagsMethod.invoke(null)
    val flags = result.asInstanceOf[java.util.List[_]]

    val vmOptions = flags.asScala.map(getVMOptionMethod.invoke(_).asInstanceOf[VMOption])

    Ok(toJson(SortedMap(vmOptions.map(e => e.getName -> e.getValue):_*)))
  }

}
