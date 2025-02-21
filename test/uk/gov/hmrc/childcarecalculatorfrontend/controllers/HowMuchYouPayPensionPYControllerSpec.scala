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

package uk.gov.hmrc.childcarecalculatorfrontend.controllers

import play.api.data.Form
import play.api.libs.json.JsNumber
import uk.gov.hmrc.http.cache.client.CacheMap
import uk.gov.hmrc.childcarecalculatorfrontend.FakeNavigator
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.FakeDataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.forms.HowMuchYouPayPensionPYForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.HowMuchYouPayPensionPYId
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.howMuchYouPayPensionPY
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._

import scala.concurrent.ExecutionContext.Implicits.global

class HowMuchYouPayPensionPYControllerSpec extends ControllerSpecBase {

  val view = application.injector.instanceOf[howMuchYouPayPensionPY]
  def onwardRoute = routes.WhatToTellTheCalculatorController.onPageLoad()

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new HowMuchYouPayPensionPYController(frontendAppConfig, mcc, FakeDataCacheConnector, new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction, new DataRequiredAction, view)

  def viewAsString(form: Form[BigDecimal] = HowMuchYouPayPensionPYForm()) = view(frontendAppConfig, form, NormalMode)(fakeRequest, messages).toString

  val testNumber = 123

  "HowMuchYouPayPensionPY Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe OK
      contentAsString(result) mustBe viewAsString()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData = Map(HowMuchYouPayPensionPYId.toString -> JsNumber(testNumber))
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      contentAsString(result) mustBe viewAsString(HowMuchYouPayPensionPYForm().fill(testNumber))
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", testNumber.toString))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", "invalid value"))
      val boundForm = HowMuchYouPayPensionPYForm(howMuchYouPayPensionPYInvalidErrorKey).bind(Map("value" -> "invalid value"))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustBe BAD_REQUEST
      contentAsString(result) mustBe viewAsString(boundForm)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", testNumber.toString))
      val result = controller(dontGetAnyData).onSubmit(NormalMode)(postRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
    }
  }
}
