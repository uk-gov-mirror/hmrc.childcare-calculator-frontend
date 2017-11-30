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

package uk.gov.hmrc.childcarecalculatorfrontend.models.household

import play.api.libs.json.Json
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes.TaxCredits
import uk.gov.hmrc.childcarecalculatorfrontend.utils.UserAnswers


case class Household(parent: Claimant,
                     partner: Option[Claimant] = None)

object Household {

  def apply(answers: UserAnswers, tcScheme: TaxCredits): Household = {
    Household(getParent(answers, tcScheme),
      getPartner(answers, tcScheme))
  }

  private def getPartner(answers: UserAnswers,
                         tcScheme: TaxCredits) =
    if (answers.doYouLiveWithPartner.getOrElse(false)) {
      Some(Claimant(answers, tcScheme, true))
    } else None

  private def getParent(answers: UserAnswers,
                        tcScheme: TaxCredits) = Claimant(answers, tcScheme, false)

  implicit val formatHousehold = Json.format[Household]
}
