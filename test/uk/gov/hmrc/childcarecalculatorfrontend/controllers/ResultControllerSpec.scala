/*
 * Copyright 2019 HM Revenue & Customs
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

import org.mockito.ArgumentMatchers._
import org.mockito.Mockito._
import org.scalatestplus.mockito.MockitoSugar
import play.api.i18n.Lang
import play.api.libs.json.{JsBoolean, JsString}
import play.api.test.Helpers._
import services.MoreInfoService
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.FakeDataCacheConnector
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.actions.{DataRequiredAction, DataRetrievalAction, FakeDataRetrievalAction}
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.{LocationId, YourChildcareVouchersId}
import uk.gov.hmrc.childcarecalculatorfrontend.models.views.ResultsViewModel
import uk.gov.hmrc.childcarecalculatorfrontend.models.{Location, NormalMode}
import uk.gov.hmrc.childcarecalculatorfrontend.services.ResultsService
import uk.gov.hmrc.childcarecalculatorfrontend.utils.Utils
import uk.gov.hmrc.http.cache.client.CacheMap

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ResultControllerSpec extends ControllerSpecBase with MockitoSugar{

  val mockMoreInfoService: MoreInfoService  = mock[MoreInfoService]
  val resultService: ResultsService         = mock[ResultsService]

  implicit val l: Lang = mock[Lang]

  val location = Location.ENGLAND

  val cacheMapWithLocation = new CacheMap("id", Map(LocationId.toString -> JsString(location.toString)))

  val cacheMapWithNoLocation = new CacheMap("id", Map("test" -> JsString(location.toString)))

  val cacheMapNotEligibleForTaxCredits = new CacheMap( "id", Map(
    LocationId.toString -> JsString(location.toString),
    YourChildcareVouchersId.toString -> JsBoolean(true)
  ))

  val cacheMapEligibleForTaxCredits = new CacheMap( "id", Map(
    LocationId.toString -> JsString(location.toString),
    YourChildcareVouchersId.toString -> JsBoolean(false)
  ))

  def controller(dataRetrievalAction: DataRetrievalAction = getEmptyCacheMap,
                 resultService: ResultsService): ResultController =
    new ResultController(frontendAppConfig,
      mcc,
      FakeDataCacheConnector,
      dataRetrievalAction,
      new DataRequiredAction,
      resultService,
      mockMoreInfoService,
      new Utils)

  "Result Controller" must {
    "return OK and with ResultViewModel for a GET" in {
      when(resultService.getResultsViewModel(any(),any())(any(),any(),any())) thenReturn Future.successful(
        ResultsViewModel(freeHours = Some(15), tc = Some(500), tfc = Some(600), esc = Some(1000), location = location, hasChildcareCosts = true, hasCostsWithApprovedProvider = true, isAnyoneInPaidEmployment = true, livesWithPartner = true))

      when(mockMoreInfoService.getSchemeContent(any(), any(), any())(any())) thenReturn List.empty
      when(mockMoreInfoService.getSummary(any(), any())(any())) thenReturn None

      val getRelevantData = new FakeDataRetrievalAction(Some(cacheMapWithLocation))
      val resultPage = controller(getRelevantData, resultService).onPageLoad(fakeRequest)
      status(resultPage) mustBe OK
      contentAsString(resultPage) must include("Help you could get with your childcare costs")
    }


    "redirect to Location controller when there is no location data" in {
      val getRelevantData = new FakeDataRetrievalAction(Some(cacheMapWithNoLocation))
      val result = controller(getRelevantData, resultService).onPageLoad(fakeRequest)

      status(result) mustBe SEE_OTHER
      redirectLocation(result).get mustBe routes.LocationController.onPageLoad(NormalMode).url
    }

    "redirect to Session Expired" when {
      "we do a GET and no data is found" in {
        val result = controller(dontGetAnyData, resultService).onPageLoad(fakeRequest)

        status(result) mustBe SEE_OTHER
        redirectLocation(result) mustBe Some(routes.SessionExpiredController.onPageLoad().url)
      }
    }

    "suppress tax credit information" when {
      "when user is NOT eligible for tax credits" in {
        when(mockMoreInfoService.getSchemeContent(any(), any(), any())(any())) thenReturn List.empty
        when(mockMoreInfoService.getSummary(any(), any())(any())) thenReturn None

        val getRelevantData = new FakeDataRetrievalAction(Some(cacheMapNotEligibleForTaxCredits))
        val resultPage = controller(getRelevantData, resultService).onPageLoad(fakeRequest)
        status(resultPage) mustBe OK
        contentAsString(resultPage) mustNot include("tax credit")
      }
    }

    "NOT suppress tax credit information" when {
      "when user IS eligible for tax credits" in {
        when(mockMoreInfoService.getSchemeContent(any(), any(), any())(any())) thenReturn List.empty
        when(mockMoreInfoService.getSummary(any(), any())(any())) thenReturn None

        val getRelevantData = new FakeDataRetrievalAction(Some(cacheMapEligibleForTaxCredits))
        val resultPage = controller(getRelevantData, resultService).onPageLoad(fakeRequest)
        status(resultPage) mustBe OK
        contentAsString(resultPage) must include("tax credit")
      }
    }
  }

}
