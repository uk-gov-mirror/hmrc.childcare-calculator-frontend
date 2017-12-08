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

package uk.gov.hmrc.childcarecalculatorfrontend.services

import org.joda.time.LocalDate
import org.mockito.Matchers.any
import org.mockito.Mockito._
import org.scalatest.mockito.MockitoSugar
import play.api.libs.json.JsValue
import uk.gov.hmrc.childcarecalculatorfrontend.FrontendAppConfig
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.EligibilityConnector
import uk.gov.hmrc.childcarecalculatorfrontend.models.WhichBenefitsEnum.{CARERSALLOWANCE, HIGHRATEDISABILITYBENEFITS}
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.{SchemeSpec, TaxCredits}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{UserAnswers, Utils}
import uk.gov.hmrc.http.HeaderCarrier
import uk.gov.hmrc.http.cache.client.CacheMap

import scala.concurrent.{ExecutionContext, Future}
import org.scalatest.concurrent.ScalaFutures
import play.api.mvc.Request
import scala.concurrent.ExecutionContext.Implicits.global

class EligibilityServiceSpec extends SchemeSpec with MockitoSugar with ScalaFutures {

  def userAnswers(answers: (String, JsValue)*): UserAnswers = new UserAnswers(CacheMap("", Map(answers: _*)))
  val frontendAppConfig: FrontendAppConfig = mock[FrontendAppConfig]
  val utils = mock[Utils]
  val taxCredits = mock[TaxCredits]
  val connector = mock[EligibilityConnector]
  implicit val hc = HeaderCarrier()
  implicit val ec = ExecutionContext
  implicit val req = mock[Request[_]]

  def eligibilityService = new EligibilityService(frontendAppConfig, utils, taxCredits, connector)
  val todaysDate = LocalDate.now()

  "EligibilityService" should {

    "return a SchemeResults object when given minimum data" in {
      val schemeResults = SchemeResults(Nil, false, false)
      val answers = spy(userAnswers())

      when(answers.location) thenReturn Some(Location.ENGLAND)
      when(connector.getEligibility(any())(any())) thenReturn Future(schemeResults)

      val futureResult = eligibilityService.eligibility(answers)
      whenReady(futureResult) { result =>
        result mustBe schemeResults
      }
    }

  }

}
