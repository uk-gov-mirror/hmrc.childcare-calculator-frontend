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

package uk.gov.hmrc.childcarecalculatorfrontend.cascadeUpserts

import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.*
import uk.gov.hmrc.childcarecalculatorfrontend.models.BenefitsIncomeCY
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.YouPartnerBoth
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CacheMap

class BenefitsCascadeUpsertSpec extends SpecBase with CascadeUpsertBase {

  "Benefits CY" when {
    "Save YouAnyTheseBenefitsCY data " must {
      "remove YouBenefitsIncomeCY page data when user selects no option" in {
        val originalCacheMap = CacheMap.of(
          YouBenefitsIncomeCYId.of(20)
        )

        val result = cascadeUpsert(YouAnyTheseBenefitsCYId, false, originalCacheMap)

        result.data mustBe Map(YouAnyTheseBenefitsCYId.of(false))
      }

      "return original cache map when user selects yes option" in {
        val originalCacheMap = CacheMap.of(YouBenefitsIncomeCYId.of(20))

        val result = cascadeUpsert(YouAnyTheseBenefitsCYId, true, originalCacheMap)

        result.data mustBe Map(
          YouAnyTheseBenefitsCYId.of(true),
          YouBenefitsIncomeCYId.of(20)
        )
      }

    }

    "Save BothAnyTheseBenefitsCY data " must {
      "remove whosHadBenefits, youBenefitsIncomeCY, partnerBenefitsIncomeCY and BenefitsIncomeCY pages data" +
        " when user selects no option" in {
          val originalCacheMap = CacheMap.of(
            YouBenefitsIncomeCYId.of(20),
            PartnerBenefitsIncomeCYId.of(20),
            BenefitsIncomeCYId.of(BenefitsIncomeCY(10, 20)),
            WhosHadBenefitsId.of(YouPartnerBoth.You)
          )

          val result = cascadeUpsert(BothAnyTheseBenefitsCYId, false, originalCacheMap)

          result.data mustBe Map(BothAnyTheseBenefitsCYId.of(false))
        }

      "return original cache map when user selects yes option" in {
        val originalCacheMap = CacheMap.of(WhosHadBenefitsId.of(YouPartnerBoth.You), YouBenefitsIncomeCYId.of(20))

        val result = cascadeUpsert(BothAnyTheseBenefitsCYId, true, originalCacheMap)

        result.data mustBe Map(
          BothAnyTheseBenefitsCYId.of(true),
          WhosHadBenefitsId.of(YouPartnerBoth.You),
          YouBenefitsIncomeCYId.of(20)
        )
      }

    }

    "Save WhosHadBenefits CY data " must {
      "remove PartnerBenefitsIncomeCY and BenefitsIncomeCY page data when user selects you option" in {
        val originalCacheMap = CacheMap.of(
          YouBenefitsIncomeCYId.of(10),
          PartnerBenefitsIncomeCYId.of(20),
          BenefitsIncomeCYId.of(BenefitsIncomeCY(10, 20))
        )

        val result = cascadeUpsert(WhosHadBenefitsId, YouPartnerBoth.You, originalCacheMap)

        result.data mustBe Map(
          WhosHadBenefitsId.of(YouPartnerBoth.You),
          YouBenefitsIncomeCYId.of(10)
        )
      }

      "remove youBenefitsIncomeCY and BenefitsIncomeCY page data when user selects partner option" in {
        val originalCacheMap = CacheMap.of(
          YouBenefitsIncomeCYId.of(10),
          PartnerBenefitsIncomeCYId.of(20),
          BenefitsIncomeCYId.of(BenefitsIncomeCY(10, 20))
        )

        val result = cascadeUpsert(WhosHadBenefitsId, YouPartnerBoth.Partner, originalCacheMap)

        result.data mustBe Map(
          WhosHadBenefitsId.of(YouPartnerBoth.Partner),
          PartnerBenefitsIncomeCYId.of(20)
        )
      }

      "remove PartnerBenefitsIncomeCY and youBenefitsIncomeCY page data when user selects both option" in {
        val originalCacheMap = CacheMap.of(
          YouBenefitsIncomeCYId.of(10),
          PartnerBenefitsIncomeCYId.of(20),
          BenefitsIncomeCYId.of(BenefitsIncomeCY(10, 20))
        )

        val result = cascadeUpsert(WhosHadBenefitsId, YouPartnerBoth.Both, originalCacheMap)

        result.data mustBe Map(
          WhosHadBenefitsId.of(YouPartnerBoth.Both),
          BenefitsIncomeCYId.of(BenefitsIncomeCY(10, 20))
        )
      }
    }
  }

}
