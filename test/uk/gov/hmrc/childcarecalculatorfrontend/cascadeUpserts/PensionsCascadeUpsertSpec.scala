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
import uk.gov.hmrc.childcarecalculatorfrontend.models.HowMuchBothPayPension
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.YouPartnerBoth
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CacheMap

class PensionsCascadeUpsertSpec extends SpecBase with CascadeUpsertBase {

  "Paid Pension CY" when {
    "Save  YouPaidPensionCY data " must {
      "remove howMuchYouPayPension page data when user selects no option" in {
        val originalCacheMap = CacheMap.of(HowMuchYouPayPensionId.of(20))

        val result = cascadeUpsert(YouPaidPensionCYId, false, originalCacheMap)

        result.data mustBe Map(YouPaidPensionCYId.of(false))
      }

      "return original cache map when user selects yes option" in {
        val originalCacheMap = CacheMap.of(HowMuchYouPayPensionId.of(20))

        val result = cascadeUpsert(YouPaidPensionCYId, true, originalCacheMap)

        result.data mustBe Map(
          YouPaidPensionCYId.of(true),
          HowMuchYouPayPensionId.of(20)
        )
      }
    }

    "Save PartnerPaidPensionCY data " must {
      "remove howMuchPartnerPayPension page data when user selects no option" in {
        val originalCacheMap = CacheMap.of(HowMuchPartnerPayPensionId.of(20))

        val result = cascadeUpsert(PartnerPaidPensionCYId, false, originalCacheMap)

        result.data mustBe Map(PartnerPaidPensionCYId.of(false))
      }

      "return original cache map when user selects yes option" in {
        val originalCacheMap = CacheMap.of(HowMuchPartnerPayPensionId.of(20))

        val result = cascadeUpsert(PartnerPaidPensionCYId, true, originalCacheMap)

        result.data mustBe Map(
          PartnerPaidPensionCYId.of(true),
          HowMuchPartnerPayPensionId.of(20)
        )
      }
    }

    "Save BothPaidPensionCY data " must {
      "remove WhoPaysIntoPension, howMuchYouPayPension, howMuchPartnerPayPension and howMuchBothPayPension pages data" +
        " when user selects no option" in {
          val originalCacheMap = CacheMap.of(
            HowMuchYouPayPensionId.of(20),
            HowMuchPartnerPayPensionId.of(20),
            HowMuchBothPayPensionId.of(HowMuchBothPayPension(20, 20)),
            WhoPaysIntoPensionId.of(YouPartnerBoth.You)
          )

          val result = cascadeUpsert(BothPaidPensionCYId, false, originalCacheMap)

          result.data mustBe Map(BothPaidPensionCYId.of(false))
        }

      "return original cache map when user selects yes option" in {
        val originalCacheMap = CacheMap.of(
          WhoPaysIntoPensionId.of(YouPartnerBoth.You),
          HowMuchYouPayPensionId.of(20)
        )

        val result = cascadeUpsert(BothPaidPensionCYId, true, originalCacheMap)

        result.data mustBe Map(
          BothPaidPensionCYId.of(true),
          WhoPaysIntoPensionId.of(YouPartnerBoth.You),
          HowMuchYouPayPensionId.of(20)
        )
      }

    }

    "Save WhoPaysIntoPension data " must {
      "remove HowMuchPartnerPayPension and HowMuchBothPayPension page data when user selects you option" in {
        val originalCacheMap = CacheMap.of(
          HowMuchYouPayPensionId.of(20),
          HowMuchPartnerPayPensionId.of(20),
          HowMuchBothPayPensionId.of(HowMuchBothPayPension(20, 20))
        )

        val result = cascadeUpsert(WhoPaysIntoPensionId, YouPartnerBoth.You, originalCacheMap)

        result.data mustBe Map(
          WhoPaysIntoPensionId.of(YouPartnerBoth.You),
          HowMuchYouPayPensionId.of(20)
        )
      }

      "remove HowMuchYouPayPension and HowMuchBothPayPension page data when user selects partner option" in {
        val originalCacheMap = CacheMap.of(
          HowMuchYouPayPensionId.of(20),
          HowMuchPartnerPayPensionId.of(20),
          HowMuchBothPayPensionId.of(HowMuchBothPayPension(20, 20))
        )

        val result = cascadeUpsert(WhoPaysIntoPensionId, YouPartnerBoth.Partner, originalCacheMap)

        result.data mustBe Map(
          WhoPaysIntoPensionId.of(YouPartnerBoth.Partner),
          HowMuchPartnerPayPensionId.of(20)
        )
      }

      "remove HowMuchPartnerPayPension and HowMuchYouPayPension page data when user selects both option" in {
        val originalCacheMap = CacheMap.of(
          HowMuchYouPayPensionId.of(20),
          HowMuchPartnerPayPensionId.of(20),
          HowMuchBothPayPensionId.of(HowMuchBothPayPension(20, 20))
        )

        val result = cascadeUpsert(WhoPaysIntoPensionId, YouPartnerBoth.Both, originalCacheMap)

        result.data mustBe Map(
          WhoPaysIntoPensionId.of(YouPartnerBoth.Both),
          HowMuchBothPayPensionId.of(HowMuchBothPayPension(20, 20))
        )
      }
    }

  }

}
