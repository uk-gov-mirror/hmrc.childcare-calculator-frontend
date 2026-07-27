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
import uk.gov.hmrc.childcarecalculatorfrontend.models.*
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.{Location, YouPartnerBoth}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CacheMap

class IncomeCascadeUpsertSpec extends SpecBase with CascadeUpsertBase {

  "Parent Paid Work CY" when {
    "save the data" must {

      "save the page data when user accesses the page first time and selects yes" in {
        val originalCacheMap = CacheMap.of(LocationId.of(Location.NorthernIreland))

        val result = cascadeUpsert(ParentPaidWorkCYId, true, originalCacheMap)

        result.data mustBe Map(
          ParentPaidWorkCYId.of(true),
          LocationId.of(Location.NorthernIreland)
        )
      }

      "save the data and remove PartnerEmploymentIncomeCY, BothPaidPensionCY, WhoPaysIntoPension  page data when user selects yes" in {
        val originalCacheMap = CacheMap.of(
            EmploymentIncomeCYId.of(EmploymentIncomeCY(20, 20)),
            PartnerEmploymentIncomeCYId.of(1200),
            BothPaidPensionCYId.of(true),
            ParentPaidWorkCYId.of(false),
            WhoPaysIntoPensionId.of(YouPartnerBoth.You)
          )

        val result = cascadeUpsert(ParentPaidWorkCYId, true, originalCacheMap)

        result.data mustBe Map(
          ParentPaidWorkCYId.of(true),
          EmploymentIncomeCYId.of(EmploymentIncomeCY(20, 20))
        )
      }

      "save the page data when user accesses the page first time and select when user selects no " in {
        val originalCacheMap = CacheMap.of(LocationId.of(Location.NorthernIreland))

        val result = cascadeUpsert(ParentPaidWorkCYId, false, originalCacheMap)

        result.data mustBe Map(
          ParentPaidWorkCYId.of(false),
          LocationId.of(Location.NorthernIreland)
        )
      }

      "clear EmploymentIncomeCY, PartnerPaidPensionCY, HowMuchPartnerPayPension, HowMuchYouPayPensionId, HowMuchBothPayPensionId" +
        " page data when user change the selection from yes to no" in {
          val originalCacheMap = CacheMap.of(
              EmploymentIncomeCYId.of(EmploymentIncomeCY(20, 20)),
              ParentPaidWorkCYId.of(true),
              HowMuchYouPayPensionId.of(2300),
              HowMuchBothPayPensionId.of(HowMuchBothPayPension(23, 23))
            )

          val result = cascadeUpsert(ParentPaidWorkCYId, false, originalCacheMap)

          result.data mustBe Map(ParentPaidWorkCYId.of(false))
        }
    }
  }

  "Partner Paid Work CY" when {
    "save the data" must {

      "save the page data when user accesses the page first time and selects yes" in {
        val originalCacheMap = CacheMap.of(LocationId.of(Location.NorthernIreland))

        val result = cascadeUpsert(PartnerPaidWorkCYId, true, originalCacheMap)

        result.data mustBe Map(
          PartnerPaidWorkCYId.of(true),
          LocationId.of(Location.NorthernIreland)
        )
      }

      "save the data and remove ParentEmploymentIncomeCY, EmploymentIncomeCY, YouPaidPensionCYId page data when user changes" +
        "the selection from no to yes" in {
          val originalCacheMap = CacheMap.of(
              ParentEmploymentIncomeCYId.of(1200),
              YouPaidPensionCYId.of(true),
              PartnerPaidWorkCYId.of(false)
            )

          val result = cascadeUpsert(PartnerPaidWorkCYId, true, originalCacheMap)

          result.data mustBe Map(PartnerPaidWorkCYId.of(true))
        }

      "save the page data when user accesses the page first time and select when user selects no " in {
        val originalCacheMap = CacheMap.of(LocationId.of(Location.NorthernIreland))

        val result = cascadeUpsert(PartnerPaidWorkCYId, false, originalCacheMap)

        result.data mustBe Map(
          PartnerPaidWorkCYId.of(false),
          LocationId.of(Location.NorthernIreland)
        )
      }

      "clear EmploymentIncomeCY,BothPaidPensionCY, WhoPaysIntoPension, HowMuchPartnerPayPension, HowMuchBothPayPension" +
        " page data when user changes the selection from yes to no " in {
          val originalCacheMap = CacheMap.of(
              EmploymentIncomeCYId.of(EmploymentIncomeCY(20, 20)),
              BothPaidPensionCYId.of(true),
              WhoPaysIntoPensionId.of(YouPartnerBoth.Both),
              HowMuchPartnerPayPensionId.of(230),
              HowMuchBothPayPensionId.of(HowMuchBothPayPension(230, 230)),
              PartnerPaidWorkCYId.of(true)
            )

          val result = cascadeUpsert(PartnerPaidWorkCYId, false, originalCacheMap)

          result.data mustBe Map(PartnerPaidWorkCYId.of(false))
        }
    }
  }

  "Other Income CY" when {
    "Save YourOtherIncomeThisYear data " must {
      "remove yourOtherIncomeAmountCY page data when user selects no option" in {
        val originalCacheMap = CacheMap.of(YourOtherIncomeAmountCYId.of(20))

        val result = cascadeUpsert(YourOtherIncomeThisYearId, false, originalCacheMap)

        result.data mustBe Map(YourOtherIncomeThisYearId.of(false))
      }

      "return original cache map when user selects yes option" in {
        val originalCacheMap = CacheMap.of(YourOtherIncomeAmountCYId.of(20))

        val result = cascadeUpsert(YourOtherIncomeThisYearId, true, originalCacheMap)

        result.data mustBe Map(
          YourOtherIncomeThisYearId.of(true),
          YourOtherIncomeAmountCYId.of(20)
        )
      }
    }

    "Save BothOtherIncomeThisYear data " must {
      "remove whoGetsOtherIncomeCY, yourOtherIncomeAmountCY, partnerOtherIncomeAmountCY and otherIncomeAmountCY pages data" +
        " when user selects no option" in {
          val originalCacheMap = CacheMap.of(
              YourOtherIncomeAmountCYId.of(20),
              PartnerOtherIncomeAmountCYId.of(20),
              OtherIncomeAmountCYId.of(OtherIncomeAmountCY(20, 20)),
              WhoGetsOtherIncomeCYId.of(YouPartnerBoth.You)
            )

          val result = cascadeUpsert(BothOtherIncomeThisYearId, false, originalCacheMap)

          result.data mustBe Map(BothOtherIncomeThisYearId.of(false))
        }

      "return original cache map when user selects yes option" in {
        val originalCacheMap = CacheMap.of(
            WhoGetsOtherIncomeCYId.of(YouPartnerBoth.You),
            YourOtherIncomeAmountCYId.of(20)
          )

        val result = cascadeUpsert(BothOtherIncomeThisYearId, true, originalCacheMap)

        result.data mustBe Map(
          BothOtherIncomeThisYearId.of(true),
          WhoGetsOtherIncomeCYId.of(YouPartnerBoth.You),
          YourOtherIncomeAmountCYId.of(20)
        )
      }
    }

    "Save WhoGetsOtherIncomeCY data " must {
      "remove PartnerOtherIncomeAmountCY and OtherIncomeAmountCY page data when user selects you option" in {
        val originalCacheMap = CacheMap.of(
            YourOtherIncomeAmountCYId.of(20),
            PartnerOtherIncomeAmountCYId.of(20),
            OtherIncomeAmountCYId.of(OtherIncomeAmountCY(20, 20))
          )

        val result = cascadeUpsert(WhoGetsOtherIncomeCYId, YouPartnerBoth.You.toString, originalCacheMap)

        result.data mustBe Map(
          WhoGetsOtherIncomeCYId.of(YouPartnerBoth.You),
          YourOtherIncomeAmountCYId.of(20)
        )
      }

      "remove YourOtherIncomeAmountCY and OtherIncomeAmountCY page data when user selects partner option" in {
        val originalCacheMap = CacheMap.of(
            YourOtherIncomeAmountCYId.of(20),
            PartnerOtherIncomeAmountCYId.of(20),
            OtherIncomeAmountCYId.of(OtherIncomeAmountCY(20, 20))
          )

        val result = cascadeUpsert(WhoGetsOtherIncomeCYId, YouPartnerBoth.Partner.toString, originalCacheMap)

        result.data mustBe Map(
          WhoGetsOtherIncomeCYId.of(YouPartnerBoth.Partner),
          PartnerOtherIncomeAmountCYId.of(20)
        )
      }

      "remove PartnerOtherIncomeAmountCY and YourOtherIncomeAmountCY page data when user selects both option" in {
        val originalCacheMap = CacheMap.of(
            YourOtherIncomeAmountCYId.of(20),
            PartnerOtherIncomeAmountCYId.of(20),
            OtherIncomeAmountCYId.of(OtherIncomeAmountCY(20, 20))
          )

        val result = cascadeUpsert(WhoGetsOtherIncomeCYId, YouPartnerBoth.Both.toString, originalCacheMap)

        result.data mustBe Map(
          WhoGetsOtherIncomeCYId.of(YouPartnerBoth.Both),
          OtherIncomeAmountCYId.of(OtherIncomeAmountCY(20, 20))
        )
      }

      "return original cache map when there is any invalid value for the input" in {
        val originalCacheMap = CacheMap.of(
            YourOtherIncomeAmountCYId.of(20),
            PartnerOtherIncomeAmountCYId.of(20),
            OtherIncomeAmountCYId.of(OtherIncomeAmountCY(20, 20))
          )

        val result = cascadeUpsert(WhoGetsOtherIncomeCYId, "invalidvalue", originalCacheMap)

        result.data mustBe Map(
          WhoGetsOtherIncomeCYId.toString -> "invalidvalue",
          YourOtherIncomeAmountCYId.of(20),
          PartnerOtherIncomeAmountCYId.of(20),
          OtherIncomeAmountCYId.of(OtherIncomeAmountCY(20, 20))
        )
      }
    }

  }

}
