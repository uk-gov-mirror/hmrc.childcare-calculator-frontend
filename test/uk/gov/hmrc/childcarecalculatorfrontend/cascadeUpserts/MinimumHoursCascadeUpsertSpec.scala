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
import uk.gov.hmrc.childcarecalculatorfrontend.models.ChildAgeGroup
import uk.gov.hmrc.childcarecalculatorfrontend.models.ChildAgeGroup.ThreeYears
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.*
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CacheMap

class MinimumHoursCascadeUpsertSpec extends SpecBase with CascadeUpsertBase {

  "MinimumHoursCascadeUpsert" when {

    "saving a location of northernIreland" must {
      "remove an existing childAgedTwo key and save the location" in {
        val originalCacheMap = CacheMap.of(ChildAgedTwoId.of(true))

        val result = cascadeUpsert(LocationId, Location.NorthernIreland, originalCacheMap)
        result.data mustBe Map(LocationId.of(Location.NorthernIreland))
      }
    }

    "saving a location of wales" must {
      "remove an existing childAgedTwo key and save the location" in {
        val originalCacheMap = CacheMap.of(ChildAgedTwoId.of(true))

        val result = cascadeUpsert(LocationId, Location.Wales, originalCacheMap)
        result.data mustBe Map(LocationId.of(Location.Wales))
      }
    }

    "saving a location of scotland" must {
      "save the location and leave an existing childAgedTwo key in place" in {
        val originalCacheMap = CacheMap.of(ChildAgedTwoId.of(true))

        val result = cascadeUpsert(LocationId, Location.Scotland, originalCacheMap)
        result.data mustBe Map(
          ChildAgedTwoId.of(true),
          LocationId.of(Location.Scotland)
        )
      }
    }

    "saving a location of england" must {
      "save the location and remove existing childAgedTwo and childAgedThreeOrFour answers" in {
        val originalCacheMap = CacheMap.of(
          ChildAgedTwoId.of(true),
          ChildAgedThreeOrFourId.of(true)
        )

        val result = cascadeUpsert(LocationId, Location.England, originalCacheMap)
        result.data mustBe Map(
          LocationId.of(Location.England)
        )
      }
    }

    "saving childcareCosts with an england location" must {

      "save the page data when user access the page first time and selects no" in {
        val originalCacheMap = CacheMap.of(
          LocationId.of(Location.England),
          ChildrenAgeGroupsId.of(Set(ThreeYears))
        )

        val result = cascadeUpsert(ChildcareCostsId, YesNoNotYet.No, originalCacheMap)
        result.data mustBe Map(
          ChildcareCostsId.of(YesNoNotYet.No),
          LocationId.of(Location.England),
          ChildrenAgeGroupsId.of(Set(ThreeYears))
        )
      }

      "remove all the data for subsequent pages when user changes the selection from yes to no" in {
        val originalCacheMap = CacheMap.of(
          LocationId.of(Location.England),
          ChildrenAgeGroupsId.of(Set(ThreeYears)),
          ChildcareCostsId.of(YesNoNotYet.Yes),
          ApprovedProviderId.of(YesNoNotSure.Yes),
          DoYouLiveWithPartnerId.of(false),
          WhoIsInPaidEmploymentId.of(YouPartnerBothNeither.Partner),
          WhatIsYourPartnersTaxCodeId.of("1100L"),
          PartnerChildcareVouchersId.of(true),
          YourPartnersAgeId.of(Age.UnderEighteen),
          PartnerMinimumEarningsId.of(true),
          PartnerSelfEmployedOrApprenticeId.of(EmploymentStatus.SelfEmployed),
          PartnerMaximumEarningsId.of(true)
        )

        val result = cascadeUpsert(ChildcareCostsId, YesNoNotYet.No, originalCacheMap)
        result.data mustBe Map(
          LocationId.of(Location.England),
          ChildrenAgeGroupsId.of(Set(ThreeYears)),
          ChildcareCostsId.of(YesNoNotYet.No)
        )
      }
    }

    "saving childcareCosts with a non england location" must {
      "save the page data when user access the page first time and selects no" in {
        val originalCacheMap = CacheMap.of(
          LocationId.of(Location.Scotland),
          ChildAgedTwoId.of(false),
          ChildAgedThreeOrFourId.of(true)
        )

        val result = cascadeUpsert(ChildcareCostsId, YesNoNotYet.No, originalCacheMap)
        result.data mustBe Map(
          ChildcareCostsId.of(YesNoNotYet.No),
          LocationId.of(Location.Scotland),
          ChildAgedTwoId.of(false),
          ChildAgedThreeOrFourId.of(true)
        )
      }

      "remove all the data for subsequent pages when user changes the selection from yes to no" in {
        val originalCacheMap = CacheMap.of(
          LocationId.of(Location.Scotland),
          ChildAgedTwoId.of(false),
          ChildAgedThreeOrFourId.of(true),
          ChildcareCostsId.of(YesNoNotYet.Yes),
          ApprovedProviderId.of(YesNoNotSure.Yes),
          DoYouLiveWithPartnerId.of(false),
          WhoIsInPaidEmploymentId.of(YouPartnerBothNeither.Partner),
          WhatIsYourPartnersTaxCodeId.of("1100L"),
          PartnerChildcareVouchersId.of(true),
          YourPartnersAgeId.of(Age.UnderEighteen),
          PartnerMinimumEarningsId.of(true),
          PartnerSelfEmployedOrApprenticeId.of(EmploymentStatus.SelfEmployed),
          PartnerMaximumEarningsId.of(true)
        )

        val result = cascadeUpsert(ChildcareCostsId, YesNoNotYet.No, originalCacheMap)
        result.data mustBe Map(
          LocationId.of(Location.Scotland),
          ChildAgedTwoId.of(false),
          ChildAgedThreeOrFourId.of(true),
          ChildcareCostsId.of(YesNoNotYet.No)
        )
      }
    }

    "saving ApprovedProvider with an england location" must {
      "save the page data when user access the page first time and selects no" in {
        val originalCacheMap = CacheMap.of(
          LocationId.of(Location.England),
          ChildrenAgeGroupsId.of(Set(ChildAgeGroup.ThreeYears)),
          ChildcareCostsId.of(YesNoNotYet.Yes)
        )

        val result = cascadeUpsert(ApprovedProviderId, YesNoNotSure.No, originalCacheMap)
        result.data mustBe Map(
          ApprovedProviderId.of(YesNoNotSure.No),
          LocationId.of(Location.England),
          ChildrenAgeGroupsId.of(Set(ChildAgeGroup.ThreeYears)),
          ChildcareCostsId.of(YesNoNotYet.Yes)
        )
      }

      "remove all the data for subsequent pages when user changes the selection from yes to no" in {
        val originalCacheMap = CacheMap.of(
          LocationId.of(Location.England),
          ChildrenAgeGroupsId.of(Set(ChildAgeGroup.ThreeYears)),
          ChildcareCostsId.of(YesNoNotYet.Yes),
          ApprovedProviderId.of(YesNoNotSure.Yes),
          DoYouLiveWithPartnerId.of(false),
          WhoIsInPaidEmploymentId.of(YouPartnerBothNeither.Partner),
          WhatIsYourPartnersTaxCodeId.of("1100L"),
          PartnerChildcareVouchersId.of(true),
          YourPartnersAgeId.of(Age.UnderEighteen),
          PartnerMinimumEarningsId.of(true),
          PartnerSelfEmployedOrApprenticeId.of(EmploymentStatus.SelfEmployed),
          PartnerMaximumEarningsId.of(true)
        )

        val result = cascadeUpsert(ApprovedProviderId, YesNoNotSure.No, originalCacheMap)
        result.data mustBe Map(
          LocationId.of(Location.England),
          ChildrenAgeGroupsId.of(Set(ChildAgeGroup.ThreeYears)),
          ApprovedProviderId.of(YesNoNotSure.No),
          ChildcareCostsId.of(YesNoNotYet.Yes)
        )
      }
    }

    "saving ApprovedProvider with a non england location" must {

      "save the page data when user access the page first time and selects no" in {
        val originalCacheMap = CacheMap.of(
          LocationId.of(Location.Scotland),
          ChildAgedTwoId.of(false),
          ChildAgedThreeOrFourId.of(true),
          ChildcareCostsId.of(YesNoNotYet.Yes)
        )

        val result = cascadeUpsert(ApprovedProviderId, YesNoNotSure.No, originalCacheMap)
        result.data mustBe Map(
          ApprovedProviderId.of(YesNoNotSure.No),
          LocationId.of(Location.Scotland),
          ChildAgedTwoId.of(false),
          ChildAgedThreeOrFourId.of(true),
          ChildcareCostsId.of(YesNoNotYet.Yes)
        )
      }

      "remove all the data for subsequent pages when user changes the selection from yes to no" in {
        val originalCacheMap = CacheMap.of(
          LocationId.of(Location.Scotland),
          ChildAgedTwoId.of(false),
          ChildAgedThreeOrFourId.of(true),
          ChildcareCostsId.of(YesNoNotYet.Yes),
          ApprovedProviderId.of(YesNoNotSure.Yes),
          DoYouLiveWithPartnerId.of(false),
          WhoIsInPaidEmploymentId.of(YouPartnerBothNeither.Partner),
          WhatIsYourPartnersTaxCodeId.of("1100L"),
          PartnerChildcareVouchersId.of(true),
          YourPartnersAgeId.of(Age.UnderEighteen),
          PartnerMinimumEarningsId.of(true),
          PartnerSelfEmployedOrApprenticeId.of(EmploymentStatus.SelfEmployed),
          PartnerMaximumEarningsId.of(true)
        )

        val result = cascadeUpsert(ApprovedProviderId, YesNoNotSure.No, originalCacheMap)
        result.data mustBe Map(
          LocationId.of(Location.Scotland),
          ChildAgedTwoId.of(false),
          ChildAgedThreeOrFourId.of(true),
          ApprovedProviderId.of(YesNoNotSure.No),
          ChildcareCostsId.of(YesNoNotYet.Yes)
        )
      }
    }
  }

}
