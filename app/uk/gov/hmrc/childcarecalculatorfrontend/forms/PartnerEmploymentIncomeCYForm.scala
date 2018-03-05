/*
 * Copyright 2018 HM Revenue & Customs
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

import play.api.data
import play.api.data.{Form, FormError}
import play.api.data.Forms._
import play.api.data.format.Formatter
import uk.gov.hmrc.childcarecalculatorfrontend.FrontendAppConfig
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._

class PartnerEmploymentIncomeCYForm @Inject()(appConfig: FrontendAppConfig) extends FormErrorHelper {

  val minValue: Double = appConfig.minEmploymentIncome
  val maxValue: Double = appConfig.maxEmploymentIncome

  val errorKeyBlank: String = partnerEmploymentIncomeBlankErrorKey
  val errorKeyInvalid: String = partnerEmploymentIncomeInvalidErrorKey


  def partnerEmploymentIncomeFormatter(errorKeyBlank: String, errorKeyInvalid: String)
  = new Formatter[BigDecimal] {

    val decimalRegex = """\d+(\.\d{1,2})?"""

    def bind(key: String, data: Map[String, String]): Either[Seq[FormError], BigDecimal] = {
      data.get(key) match {
        case None => produceError(key, errorKeyBlank)
        case Some("") => produceError(key, errorKeyBlank)
        case Some(s) if s.matches(decimalRegex) => Right(BigDecimal(s))
        case _ => produceError(key, errorKeyInvalid)
      }
    }

    def unbind(key: String, value: BigDecimal) = Map(key -> value.toString())
  }

  def apply(): Form[BigDecimal] =
    Form(single("value" -> of(partnerEmploymentIncomeFormatter(errorKeyBlank, errorKeyInvalid))
      .verifying(minimumValue[BigDecimal](minValue, errorKeyInvalid))
      .verifying(maximumValue[BigDecimal](maxValue, errorKeyInvalid))
    )
    )
}


