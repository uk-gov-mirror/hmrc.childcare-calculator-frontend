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

package uk.gov.hmrc.childcarecalculatorfrontend.forms

import play.api.data.{Form, FormError}
import play.api.data.Forms._
import play.api.data.format.Formatter

object PartnerStatutoryPayPerWeekForm extends FormErrorHelper {

  val requiredKey = "partnerStatutoryPayPerWeek.error.required"
  val invalidKey = "partnerStatutoryPayPerWeek.error.invalid"

  def apply(statutoryType: String): Form[BigDecimal] =
    Form(
      "value" -> decimal(requiredKey, invalidKey, statutoryType)
        .verifying(inRange[BigDecimal](1, 99.99, invalidKey, statutoryType))
    )
}
