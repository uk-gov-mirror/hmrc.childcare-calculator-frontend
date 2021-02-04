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

package uk.gov.hmrc.childcarecalculatorfrontend.services

import javax.inject.Inject

import uk.gov.hmrc.childcarecalculatorfrontend.FrontendAppConfig
import uk.gov.hmrc.childcarecalculatorfrontend.connectors.EligibilityConnector
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.models.mappings.UserAnswerToHousehold
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.TaxCredits
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{UserAnswers, Utils}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait SubmissionService {
  def eligibility(answers: UserAnswers)(implicit req: play.api.mvc.Request[_], hc: HeaderCarrier): Future[SchemeResults]
}

class EligibilityService @Inject()(appConfig: FrontendAppConfig, utils: Utils, tc: TaxCredits, connector: EligibilityConnector) extends SubmissionService {

  def userAnswerToHousehold: UserAnswerToHousehold = new UserAnswerToHousehold(appConfig, utils, tc)

  def eligibility(answers: UserAnswers)(implicit req: play.api.mvc.Request[_], hc: HeaderCarrier): Future[SchemeResults] = {

    val household = userAnswerToHousehold.convert(answers)
    connector.getEligibility(household).map {
      results => {
        results
      }
    }
  }
}
