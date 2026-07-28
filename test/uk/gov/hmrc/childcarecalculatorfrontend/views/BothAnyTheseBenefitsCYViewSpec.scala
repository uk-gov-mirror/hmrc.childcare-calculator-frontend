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

package uk.gov.hmrc.childcarecalculatorfrontend.views

import play.api.data.Form
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.forms.BooleanForm
import uk.gov.hmrc.childcarecalculatorfrontend.models.Location
import uk.gov.hmrc.childcarecalculatorfrontend.utils.TaxYearInfo
import uk.gov.hmrc.childcarecalculatorfrontend.views.behaviours.NewYesNoViewBehaviours
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.bothAnyTheseBenefitsCY

class BothAnyTheseBenefitsCYViewSpec extends NewYesNoViewBehaviours {

  override val form: Form[Boolean] = BooleanForm()
  val taxYearInfo                  = new TaxYearInfo
  val view                         = inject[bothAnyTheseBenefitsCY]
  val messageKeyPrefix             = "bothAnyTheseBenefitsCY"

  def createView(location: Location) = () =>
    view(frontendAppConfig, BooleanForm(), taxYearInfo, location)(fakeRequest, messages)

  def createViewUsingForm(location: Location) = (form: Form[Boolean]) =>
    view(frontendAppConfig, form, taxYearInfo, location)(fakeRequest, messages)

  "BothAnyTheseBenefitsCY view for non scottish users" must {

    val location: Location = Location.England
    behave.like(
      normalPage(
        createView(location: Location),
        messageKeyPrefix,
        "li.income_support",
        "li.jobseekers_allowance",
        "li.carers",
        "li.employment_support",
        "li.pensions",
        "li.disability"
      )
    )

    behave.like(pageWithBackLink(createView(location: Location)))

    behave.like(
      yesNoPage(
        createViewUsingForm(location: Location),
        messageKeyPrefix,
        routes.BothAnyTheseBenefitsCYController.onSubmit().url
      )
    )

    "contain tax year info" in {
      val doc = asDocument(createView(location: Location)())
      assertContainsText(
        doc,
        messages(s"$messageKeyPrefix.tax_year", taxYearInfo.currentTaxYearStart, taxYearInfo.currentTaxYearEnd)
      )
    }
  }

  "BothAnyTheseBenefitsCY view for scottish users" must {
    val location: Location = Location.Scotland
    behave.like(
      normalPage(
        createView(location: Location),
        messageKeyPrefix,
        "li.income_support",
        "li.jobseekers_allowance",
        "li.scottishCarersAllowance",
        "li.employment_support",
        "li.pensions",
        "li.disability"
      )
    )

    behave.like(pageWithBackLink(createView(location: Location)))

    behave.like(
      yesNoPage(
        createViewUsingForm(location: Location),
        messageKeyPrefix,
        routes.BothAnyTheseBenefitsCYController.onSubmit().url
      )
    )

    "contain tax year info" in {
      val doc = asDocument(createView(location: Location)())
      assertContainsText(
        doc,
        messages(s"$messageKeyPrefix.tax_year", taxYearInfo.currentTaxYearStart, taxYearInfo.currentTaxYearEnd)
      )
    }
  }

}
