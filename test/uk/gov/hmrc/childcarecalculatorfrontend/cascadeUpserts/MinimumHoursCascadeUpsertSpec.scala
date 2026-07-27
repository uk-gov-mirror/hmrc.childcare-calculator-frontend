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

import play.api.libs.json.*
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers.*
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.{EmploymentStatus, YesNoNotSure, YesNoNotYet}
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CacheMap
import uk.gov.hmrc.childcarecalculatorfrontend.SpecBase

class MinimumHoursCascadeUpsertSpec extends SpecBase with CascadeUpsertBase {

  "MinimumHoursCascadeUpsert" when {

    "saving a location of northernIreland" must {
      "remove an existing childAgedTwo key and save the location" in {
        val originalCacheMap = CacheMap.of((ChildAgedTwoId.of(true)))

        val result = cascadeUpsert(LocationId, "northern-ireland", originalCacheMap)
        result.data mustBe Map(LocationId.of("northern-ireland"))
      }
    }

    "saving a location of wales" must {
      "remove an existing childAgedTwo key and save the location" in {
        val originalCacheMap = CacheMap.of((ChildAgedTwoId.of(true)))

        val result = cascadeUpsert(LocationId, "wales", originalCacheMap)
        result.data mustBe Map(LocationId.of("wales"))
      }
    }

    "saving a location of scotland" must {
      "save the location and leave an existing childAgedTwo key in place" in {
        val originalCacheMap = CacheMap.of((ChildAgedTwoId.of(true)))

        val result = cascadeUpsert(LocationId, "scotland", originalCacheMap)
        result.data mustBe Map(
          ChildAgedTwoId.of(true),
          LocationId.of("scotland")
        )
      }
    }

    "saving a location of england" must {
      "save the location and remove existing childAgedTwo and childAgedThreeOrFour answers" in {
        val originalCacheMap = (CacheMap.of(
            ChildAgedTwoId.of(true),
            ChildAgedThreeOrFourId.of(true)
          )
        )

        val result = cascadeUpsert(LocationId, "england", originalCacheMap)
        result.data mustBe Map(
          LocationId.of("england")
        )
      }
    }

    "saving childcareCosts with an england location" must {

      "save the page data when user access the page first time and selects no" in {
        val originalCacheMap = (CacheMap.of(
            LocationId.of("england"),
            ChildrenAgeGroupsId.toString -> JsArray(Seq(JsString("threeYears")))
          )
        )

        val result = cascadeUpsert(ChildcareCostsId, YesNoNotSure.No.toString, originalCacheMap)
        result.data mustBe Map(
          ChildcareCostsId.of(YesNoNotSure.No),
          LocationId.of("england"),
          ChildrenAgeGroupsId.toString -> JsArray(Seq(JsString("threeYears")))
        )
      }

      "remove all the data for subsequent pages when user changes the selection from yes to no" in {
        val originalCacheMap = (CacheMap.of(
            LocationId.of("england"),
            ChildrenAgeGroupsId.toString -> JsArray(Seq(JsString("threeYears"))),
            ChildcareCostsId.of(YesNoNotSure.Yes),
            ApprovedProviderId.of(YesNoNotSure.Yes),
            DoYouLiveWithPartnerId.of(false),
            WhoIsInPaidEmploymentId.toString -> JsString(partner),
            HasYourPartnersTaxCodeBeenAdjustedId.of(YesNoNotSure.Yes),
            DoYouKnowYourPartnersAdjustedTaxCodeId.of(true),
            WhatIsYourPartnersTaxCodeId.of("1100L"),
            PartnerChildcareVouchersId.of("yes"),
            YourPartnersAgeId.of("under18"),
            PartnerMinimumEarningsId.of(true),
            PartnerSelfEmployedOrApprenticeId.toString -> JsString(
              EmploymentStatus.SelfEmployed.toString
            ),
            PartnerMaximumEarningsId.of(true)
          )
        )

        val result = cascadeUpsert(ChildcareCostsId, no, originalCacheMap)
        result.data mustBe Map(
          LocationId.of("england"),
          ChildrenAgeGroupsId.toString -> JsArray(Seq(JsString("threeYears"))),
          ChildcareCostsId.of(YesNoNotSure.No)
        )
      }
    }

    "saving childcareCosts with a non england location" must {
      "save the page data when user access the page first time and selects no" in {
        val originalCacheMap = (CacheMap.of(
            LocationId.of("scotland"),
            ChildAgedTwoId.of(false),
            ChildAgedThreeOrFourId.of(true)
          )
        )

        val result = cascadeUpsert(ChildcareCostsId, YesNoNotSure.No.toString, originalCacheMap)
        result.data mustBe Map(
          ChildcareCostsId.of(YesNoNotSure.No),
          LocationId.of("scotland"),
          ChildAgedTwoId.of(false),
          ChildAgedThreeOrFourId.of(true)
        )
      }

      "remove all the data for subsequent pages when user changes the selection from yes to no" in {
        val originalCacheMap = (CacheMap.of(
            LocationId.of("scotland"),
            ChildAgedTwoId.of(false),
            ChildAgedThreeOrFourId.of(true),
            ChildcareCostsId.of(YesNoNotSure.Yes),
            ApprovedProviderId.of(YesNoNotSure.Yes),
            DoYouLiveWithPartnerId.of(false),
            WhoIsInPaidEmploymentId.toString -> JsString(partner),
            HasYourPartnersTaxCodeBeenAdjustedId.of(YesNoNotSure.Yes),
            DoYouKnowYourPartnersAdjustedTaxCodeId.of(true),
            WhatIsYourPartnersTaxCodeId.of("1100L"),
            PartnerChildcareVouchersId.of("yes"),
            YourPartnersAgeId.of("under18"),
            PartnerMinimumEarningsId.of(true),
            PartnerSelfEmployedOrApprenticeId.toString -> JsString(
              EmploymentStatus.SelfEmployed.toString
            ),
            PartnerMaximumEarningsId.of(true)
          )
        )

        val result = cascadeUpsert(ChildcareCostsId, YesNoNotSure.No.toString, originalCacheMap)
        result.data mustBe Map(
          LocationId.of("scotland"),
          ChildAgedTwoId.of(false),
          ChildAgedThreeOrFourId.of(true),
          ChildcareCostsId.of(YesNoNotSure.No)
        )
      }
    }

    "saving ApprovedProvider with an england location" must {
      "save the page data when user access the page first time and selects no" in {
        val originalCacheMap = (CacheMap.of(
            LocationId.of("england"),
            ChildrenAgeGroupsId.toString -> JsArray(Seq(JsString("threeYears"))),
            ChildcareCostsId.of(YesNoNotSure.Yes)
          )
        )

        val result = cascadeUpsert(ApprovedProviderId, No, originalCacheMap)
        result.data mustBe Map(
          ApprovedProviderId.toString  -> JsString(No),
          LocationId.of("england"),
          ChildrenAgeGroupsId.toString -> JsArray(Seq(JsString("threeYears"))),
          ChildcareCostsId.of(YesNoNotSure.Yes)
        )
      }

      "remove all the data for subsequent pages when user changes the selection from yes to no" in {
        val originalCacheMap = (CacheMap.of(
            LocationId.of("england"),
            ChildrenAgeGroupsId.toString -> JsArray(Seq(JsString("threeYears"))),
            ChildcareCostsId.of(YesNoNotSure.Yes),
            ApprovedProviderId.of(YesNoNotSure.Yes),
            DoYouLiveWithPartnerId.of(false),
            WhoIsInPaidEmploymentId.toString -> JsString(partner),
            HasYourPartnersTaxCodeBeenAdjustedId.of(YesNoNotSure.Yes),
            DoYouKnowYourPartnersAdjustedTaxCodeId.of(true),
            WhatIsYourPartnersTaxCodeId.of("1100L"),
            PartnerChildcareVouchersId.of("yes"),
            YourPartnersAgeId.of("under18"),
            PartnerMinimumEarningsId.of(true),
            PartnerSelfEmployedOrApprenticeId.toString -> JsString(
              EmploymentStatus.SelfEmployed.toString
            ),
            PartnerMaximumEarningsId.of(true)
          )
        )

        val result = cascadeUpsert(ApprovedProviderId, No, originalCacheMap)
        result.data mustBe Map(
          LocationId.of("england"),
          ChildrenAgeGroupsId.toString -> JsArray(Seq(JsString("threeYears"))),
          ApprovedProviderId.toString  -> JsString(No),
          ChildcareCostsId.of(YesNoNotSure.Yes)
        )
      }
    }

    "saving ApprovedProvider with a non england location" must {

      "save the page data when user access the page first time and selects no" in {
        val originalCacheMap = (CacheMap.of(
            LocationId.of("scotland"),
            ChildAgedTwoId.of(false),
            ChildAgedThreeOrFourId.of(true),
            ChildcareCostsId.of(YesNoNotSure.Yes)
          )
        )

        val result = cascadeUpsert(ApprovedProviderId, No, originalCacheMap)
        result.data mustBe Map(
          ApprovedProviderId.toString -> JsString(No),
          LocationId.of("scotland"),
          ChildAgedTwoId.of(false),
          ChildAgedThreeOrFourId.of(true),
          ChildcareCostsId.of(YesNoNotSure.Yes)
        )
      }

      "remove all the data for subsequent pages when user changes the selection from yes to no" in {
        val originalCacheMap = (CacheMap.of(
            LocationId.of("scotland"),
            ChildAgedTwoId.of(false),
            ChildAgedThreeOrFourId.of(true),
            ChildcareCostsId.of(YesNoNotSure.Yes),
            ApprovedProviderId.of(YesNoNotSure.Yes),
            DoYouLiveWithPartnerId.of(false),
            WhoIsInPaidEmploymentId.toString -> JsString(partner),
            HasYourPartnersTaxCodeBeenAdjustedId.of(YesNoNotSure.Yes),
            DoYouKnowYourPartnersAdjustedTaxCodeId.of(true),
            WhatIsYourPartnersTaxCodeId.of("1100L"),
            PartnerChildcareVouchersId.of("yes"),
            YourPartnersAgeId.of("under18"),
            PartnerMinimumEarningsId.of(true),
            PartnerSelfEmployedOrApprenticeId.toString -> JsString(
              EmploymentStatus.SelfEmployed.toString
            ),
            PartnerMaximumEarningsId.of(true)
          )
        )

        val result = cascadeUpsert(ApprovedProviderId, No, originalCacheMap)
        result.data mustBe Map(
          LocationId.of("scotland"),
          ChildAgedTwoId.of(false),
          ChildAgedThreeOrFourId.of(true),
          ApprovedProviderId.toString -> JsString(No),
          ChildcareCostsId.of(YesNoNotSure.Yes)
        )
      }
    }
  }

}
