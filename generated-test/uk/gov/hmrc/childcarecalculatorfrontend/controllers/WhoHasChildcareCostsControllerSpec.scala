package uk.gov.hmrc.childcarecalculatorfrontend.controllers

import play.api.data.Form
import play.api.libs.json.Json
import play.api.test.Helpers._
import uk.gov.hmrc.childcarecalculatorfrontend.FakeNavigator
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.FakeDataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions._
import uk.gov.hmrc.childcarecalculatorfrontend.forms.WhoHasChildcareCostsForm
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.WhoHasChildcareCostsId
import uk.gov.hmrc.childcarecalculatorfrontend.models.NormalMode
import uk.gov.hmrc.childcarecalculatorfrontend.views.html.whoHasChildcareCosts
import uk.gov.hmrc.http.cache.client.CacheMap

class WhoHasChildcareCostsControllerSpec extends ControllerSpecBase {

  def onwardRoute = routes.WhatToTellTheCalculatorController.onPageLoad()

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap) =
    new WhoHasChildcareCostsController(frontendAppConfig, messagesApi, FakeDataCacheConnector, new FakeNavigator(desiredRoute = onwardRoute),
      dataRetrievalAction, new DataRequiredActionImpl)

  def viewAsString(form: Form[Set[String]] = WhoHasChildcareCostsForm()) = whoHasChildcareCosts(frontendAppConfig, form, NormalMode)(fakeRequest, messages).toString

  "WhoHasChildcareCosts Controller" must {

    "return OK and the correct view for a GET" in {
      val result = controller().onPageLoad(NormalMode)(fakeRequest)

      status(result) mustEqual OK
      contentAsString(result) mustEqual viewAsString()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {
      val validData = Map(
        WhoHasChildcareCostsId.toString -> Json.toJson(Seq(WhoHasChildcareCostsForm.options.head._2))
      )
      val getRelevantData = new FakeDataRetrievalAction(Some(CacheMap(cacheMapId, validData)))

      val result = controller(getRelevantData).onPageLoad(NormalMode)(fakeRequest)

      contentAsString(result) mustEqual viewAsString(WhoHasChildcareCostsForm().fill(Set(WhoHasChildcareCostsForm.options.head._2)))
    }

    "redirect to the next page when valid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value[0]", WhoHasChildcareCostsForm.options.head._2))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustEqual Some(onwardRoute.url)
    }

    "return a Bad Request and errors when invalid data is submitted" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value[0]", "invalid value"))
      val boundForm = WhoHasChildcareCostsForm().bind(Map("value[0]" -> "invalid value"))

      val result = controller().onSubmit(NormalMode)(postRequest)

      status(result) mustEqual BAD_REQUEST
      contentAsString(result) mustEqual viewAsString(boundForm)
    }

    "redirect to Session Expired for a GET if no existing data is found" in {
      val result = controller(dontGetAnyData).onPageLoad(NormalMode)(fakeRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustEqual Some(routes.SessionExpiredController.onPageLoad().url)
    }

    "redirect to Session Expired for a POST if no existing data is found" in {
      val postRequest = fakeRequest.withFormUrlEncodedBody(("value", WhoHasChildcareCostsForm.options.head._2))
      val result = controller(dontGetAnyData).onSubmit(NormalMode)(postRequest)

      status(result) mustEqual SEE_OTHER
      redirectLocation(result) mustEqual Some(routes.SessionExpiredController.onPageLoad().url)
    }
  }
}
