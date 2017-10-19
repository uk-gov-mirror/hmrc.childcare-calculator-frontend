/*
 * Copyright 2017 HM Revenue & Customs
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

package uk.gov.hmrc.childcarecalculatorfrontend.forms

import javax.inject.{Inject, Singleton}

import play.api.data.{Form, FormError}
import play.api.data.Forms._
import play.api.data.format.Formatter
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.FrontendAppConfig



@Singleton
class YouNoWeeksStatPayCYForm @Inject() (appConfig: FrontendAppConfig) extends FormErrorHelper {

  def youNoWeeksStatPayCYFormatter(errorKeyBlank: String, errorKeyValue: String, errorKeyInvalid: String) = new Formatter[Int] {

    val intRegex = """^(\d+)$"""
//    val decimalRegex = """^(\d*\.\d*)$""".r
    val minValue: Double = appConfig.minNoWeeksStatPay
    val maxValue: Double = appConfig.maxNoWeeksStatPay

    def bind(key: String, data: Map[String, String]) = {
      data.get(key).map(_.trim.replaceAll(",", "")) match {
        case None => produceError(key, errorKeyBlank)
        case Some("") => produceError(key, errorKeyBlank)
        case Some(s) if s.matches(intRegex) =>
          val value = s.toInt
            if (validateInRange(value, minValue, maxValue)) {
              Right(value)
            } else {
              produceError(key, errorKeyValue)
            }
        case _ =>
          produceError(key, errorKeyInvalid)
        }
      }

    def unbind(key: String, value: Int) = Map(key -> value.toString)
  }

  def apply(errorKeyBlank: String = youNoWeeksStatPayCYErrorKey,
             errorKeyDecimal: String = youNoWeeksStatPayCYInvalidErrorKey,
             errorKeyValue: String = youNoWeeksStatPayCYNumericErrorKey): Form[Int] =
    Form(single("value" -> of(youNoWeeksStatPayCYFormatter(errorKeyBlank, errorKeyDecimal, errorKeyValue))))
}
