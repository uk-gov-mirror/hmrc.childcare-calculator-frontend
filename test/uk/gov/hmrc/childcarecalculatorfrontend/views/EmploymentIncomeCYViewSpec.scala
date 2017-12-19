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

package uk.gov.hmrc.childcarecalculatorfrontend.views

import play.api.data.Form
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.forms.EmploymentIncomeCYForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.{EmploymentIncomeCY, NormalMode}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.TaxYearInfo
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.QuestionViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.employmentIncomeCY

class EmploymentIncomeCYViewSpec extends QuestionViewBehaviours[EmploymentIncomeCY] {

  val messageKeyPrefix = "employmentIncomeCY"

  val taxYearInfo = new TaxYearInfo

  override val form = new EmploymentIncomeCYForm(frontendAppConfig).apply()

  def createView = () => employmentIncomeCY(frontendAppConfig, form, NormalMode, taxYearInfo)(fakeRequest, messages)

  def createViewUsingForm = (form: Form[EmploymentIncomeCY]) => employmentIncomeCY(frontendAppConfig, form, NormalMode, taxYearInfo)(fakeRequest, messages)

  "EmploymentIncomeCY view" must {

    behave like normalPage(createView, messageKeyPrefix, "hint")

    behave like pageWithBackLink(createView)

    behave like pageWithTextFields(
      createViewUsingForm,
      messageKeyPrefix,
      routes.EmploymentIncomeCYController.onSubmit(NormalMode).url,
      "parentEmploymentIncomeCY", "partnerEmploymentIncomeCY")
  }

  "contain tax year info" in {
    val doc = asDocument(createView())
    assertContainsText(doc, messages(s"$messageKeyPrefix.tax_year", taxYearInfo.currentTaxYearStart, taxYearInfo.currentTaxYearEnd))
  }

  "contain the currencySymbol class and £ " in {
    val doc = asDocument(createView())
    val parentCurrencySymbol = doc.getElementById("parentEmploymentIncomeCY").firstElementSibling().text()
    val partnerCurrencySymbol = doc.getElementById("partnerEmploymentIncomeCY").firstElementSibling().text()

    parentCurrencySymbol mustBe "£"
    partnerCurrencySymbol mustBe "£"

  }
}
