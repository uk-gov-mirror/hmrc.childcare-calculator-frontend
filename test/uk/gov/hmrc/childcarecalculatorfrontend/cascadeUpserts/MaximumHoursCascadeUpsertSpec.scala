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
import uk.gov.hmrc.childcarecalculatorfrontend.models.enums.*
import uk.gov.hmrc.childcarecalculatorfrontend.utils.CacheMap

class MaximumHoursCascadeUpsertSpec extends SpecBase with CascadeUpsertBase {

  "saving the doYouLiveWithPartner" when {

    "doYouLiveWithPartner is false" must {

      "remove data related to both parents in employment" in {
        val originalCacheMap = CacheMap.of(
          WhoIsInPaidEmploymentId.of(YouPartnerBothNeither.Both),
          WhatIsYourPartnersTaxCodeId.of("1100L"),
          WhatIsYourTaxCodeId.of("1100L"),
          WhoGetsVouchersId.of(YouPartnerBothNeitherNotSure.You),
          PartnerChildcareVouchersId.of(true),
          YourChildcareVouchersId.of(true),
          DoYouGetAnyBenefitsId.of(Set(ParentsBenefit.IncapacityBenefit)),
          DoesYourPartnerGetAnyBenefitsId.of(Set(ParentsBenefit.IncapacityBenefit)),
          YourPartnersAgeId.of(Age.UnderEighteen),
          YourAgeId.of(Age.UnderEighteen),
          PartnerMinimumEarningsId.of(true),
          YourMinimumEarningsId.of(false),
          AreYouSelfEmployedOrApprenticeId.of(EmploymentStatus.SelfEmployed),
          PartnerMaximumEarningsId.of(true)
        )

        val result = cascadeUpsert(DoYouLiveWithPartnerId, false, originalCacheMap)
        result.data mustBe Map(
          DoYouLiveWithPartnerId.of(false),
          WhatIsYourTaxCodeId.of("1100L"),
          YourChildcareVouchersId.of(true),
          YourAgeId.of(Age.UnderEighteen),
          YourMinimumEarningsId.of(false),
          AreYouSelfEmployedOrApprenticeId.of(EmploymentStatus.SelfEmployed)
        )
      }
    }

    "doYouLiveWithPartner is true" must {
      "remove an existing paid employment and who is in paid employment" in {
        val originalCacheMap = CacheMap.of(
          AreYouInPaidWorkId.of(true),
          DoYouGetAnyBenefitsId.of(Set.empty)
        )

        val result = cascadeUpsert(DoYouLiveWithPartnerId, true, originalCacheMap)
        result.data mustBe Map(DoYouLiveWithPartnerId.of(true))
      }
    }
  }

  "saving the areYouInPaidWork" must {
    "remove all the relevant data for you pages when are you in paid work is no" in {
      val originalCacheMap = CacheMap.of(
        WhatIsYourTaxCodeId.of("1100L"),
        YourChildcareVouchersId.of(true),
        DoYouGetAnyBenefitsId.of(Set.empty),
        YourAgeId.of(Age.UnderEighteen),
        YourMinimumEarningsId.of(true),
        YourMaximumEarningsId.of(true),
        UniversalCreditId.of(true),
        PartnerPaidWorkCYId.of(true),
        ParentEmploymentIncomeCYId.of(20),
        YouPaidPensionCYId.of(true),
        HowMuchYouPayPensionId.of(20),
        YourOtherIncomeThisYearId.of(true),
        YouAnyTheseBenefitsCYId.of(true),
        YouBenefitsIncomeCYId.of(20)
      )

      val result = cascadeUpsert(AreYouInPaidWorkId, false, originalCacheMap)
      result.data mustBe Map(AreYouInPaidWorkId.of(false))
    }
  }

  "saving the whoIsInPaidEmployment" must {

    "Do data clearance for Neither" in {
      val originalCacheMap1 = CacheMap.of(
        WhatIsYourTaxCodeId.of("1100L"),
        WhatIsYourPartnersTaxCodeId.of("1100L"),
        WhoGetsVouchersId.of(YouPartnerBothNeitherNotSure.Both),
        YourChildcareVouchersId.of(true),
        PartnerChildcareVouchersId.of(true),
        DoYouGetAnyBenefitsId.of(Set.empty),
        YourAgeId.of(Age.UnderEighteen),
        YourPartnersAgeId.of(Age.UnderEighteen),
        YourMinimumEarningsId.of(true),
        PartnerMinimumEarningsId.of(true),
        EitherOfYouMaximumEarningsId.of(true),
        UniversalCreditId.of(true),
        EmploymentIncomeCYId.of(EmploymentIncomeCY(20, 20)),
        BothPaidPensionCYId.of(true),
        WhoPaysIntoPensionId.of(YouPartnerBoth.Both),
        HowMuchBothPayPensionId.of(HowMuchBothPayPension(10, 10)),
        BothOtherIncomeThisYearId.of(true),
        WhoGetsOtherIncomeCYId.of(YouPartnerBoth.Both),
        OtherIncomeAmountCYId.of(OtherIncomeAmountCY(20, 20)),
        BothAnyTheseBenefitsCYId.of(true),
        WhosHadBenefitsId.of(YouPartnerBoth.Both),
        BenefitsIncomeCYId.of(BenefitsIncomeCY(20, 20))
      )

      // Partner In Paid Employment
      val originalCacheMap2 = CacheMap.of(
        WhatIsYourPartnersTaxCodeId.of("1100L"),
        PartnerChildcareVouchersId.of(true),
        DoYouGetAnyBenefitsId.of(Set.empty),
        YourPartnersAgeId.of(Age.UnderEighteen),
        PartnerMinimumEarningsId.of(false),
        PartnerSelfEmployedOrApprenticeId.of(EmploymentStatus.SelfEmployed),
        UniversalCreditId.of(true),
        ParentPaidWorkCYId.of(true),
        PartnerEmploymentIncomeCYId.of(20),
        PartnerPaidPensionCYId.of(true),
        HowMuchPartnerPayPensionId.of(20),
        PartnerBenefitsIncomeCYId.of(20)
      )

      // You In Paid Employment
      val originalCacheMap3 = CacheMap.of(
        WhatIsYourTaxCodeId.of("1100L"),
        YourChildcareVouchersId.of(true),
        DoYouGetAnyBenefitsId.of(Set.empty),
        YourAgeId.of(Age.UnderEighteen),
        YourMinimumEarningsId.of(false),
        AreYouSelfEmployedOrApprenticeId.of(EmploymentStatus.SelfEmployed),
        YourMaximumEarningsId.of(true),
        UniversalCreditId.of(true),
        PartnerPaidWorkCYId.of(true),
        ParentEmploymentIncomeCYId.of(20),
        YouPaidPensionCYId.of(true),
        HowMuchYouPayPensionId.of(20),
        YourOtherIncomeThisYearId.of(true),
        YouAnyTheseBenefitsCYId.of(true),
        YouBenefitsIncomeCYId.of(20)
      )

      val result1 =
        cascadeUpsert(WhoIsInPaidEmploymentId, YouPartnerBothNeither.Neither, originalCacheMap1)
      result1.data mustBe Map(WhoIsInPaidEmploymentId.of(YouPartnerBothNeither.Neither))

      val result2 =
        cascadeUpsert(WhoIsInPaidEmploymentId, YouPartnerBothNeither.Neither, originalCacheMap2)
      result2.data mustBe Map(WhoIsInPaidEmploymentId.of(YouPartnerBothNeither.Neither))

      val result3 =
        cascadeUpsert(WhoIsInPaidEmploymentId, YouPartnerBothNeither.Neither, originalCacheMap3)
      result3.data mustBe Map(WhoIsInPaidEmploymentId.of(YouPartnerBothNeither.Neither))
    }

    "remove an existing partner work hours, partner min and max earnings, employment," +
      " pension, benefits CY when whoIsInPaidEmployment is you" in {

        // Partner earning less than minimum earnings
        val originalCacheMap = CacheMap.of(
          YourPartnersAgeId.of(Age.UnderEighteen),
          PartnerMinimumEarningsId.of(false),
          PartnerSelfEmployedOrApprenticeId.of(EmploymentStatus.SelfEmployed),
          UniversalCreditId.of(true),
          ParentPaidWorkCYId.of(true),
          PartnerEmploymentIncomeCYId.of(20),
          PartnerPaidPensionCYId.of(true),
          HowMuchPartnerPayPensionId.of(20),
          PartnerBenefitsIncomeCYId.of(20)
        ) // TODO Add in Statutory Data

        val result = cascadeUpsert(WhoIsInPaidEmploymentId, YouPartnerBothNeither.You, originalCacheMap)
        result.data mustBe Map(
          WhoIsInPaidEmploymentId.of(YouPartnerBothNeither.You),
          UniversalCreditId.of(true)
        )
      }

    "remove an existing partner work hours, partner vouchers partner and both min and max earnings, " +
      "both employment, both pension, both benefits CY when whoIsInPaidEmployment is you" in {

        // Parent earning more than minimum earnings and Partner earning less than minimum earnings
        val originalCacheMap1 = CacheMap.of(
          WhoGetsVouchersId.of(YouPartnerBothNeitherNotSure.You),
          YourAgeId.of(Age.UnderEighteen),
          YourPartnersAgeId.of(Age.UnderEighteen),
          YourMinimumEarningsId.of(true),
          PartnerMinimumEarningsId.of(false),
          PartnerSelfEmployedOrApprenticeId.of(EmploymentStatus.SelfEmployed),
          YourMaximumEarningsId.of(true),
          EmploymentIncomeCYId.of(EmploymentIncomeCY(20, 20)),
          BothPaidPensionCYId.of(true),
          WhoPaysIntoPensionId.of(YouPartnerBoth.Both),
          HowMuchBothPayPensionId.of(HowMuchBothPayPension(20, 20)),
          BothOtherIncomeThisYearId.of(true),
          WhoGetsOtherIncomeCYId.of(YouPartnerBoth.Both),
          OtherIncomeAmountCYId.of(OtherIncomeAmountCY(20, 20)),
          BothAnyTheseBenefitsCYId.of(true),
          WhosHadBenefitsId.of(YouPartnerBoth.Both),
          BenefitsIncomeCYId.of(BenefitsIncomeCY(20, 20))
        )

        // Parent and Partner earning more than minimum earnings
        val originalCacheMap2 = CacheMap.of(
          WhoGetsVouchersId.of(YouPartnerBothNeitherNotSure.You),
          YourAgeId.of(Age.UnderEighteen),
          YourPartnersAgeId.of(Age.UnderEighteen),
          YourMinimumEarningsId.of(true),
          PartnerMinimumEarningsId.of(true),
          EitherOfYouMaximumEarningsId.of(true),
          EmploymentIncomeCYId.of(EmploymentIncomeCY(20, 20)),
          BothPaidPensionCYId.of(true),
          WhoPaysIntoPensionId.of(YouPartnerBoth.Both),
          HowMuchBothPayPensionId.of(HowMuchBothPayPension(20, 20)),
          BothOtherIncomeThisYearId.of(true),
          WhoGetsOtherIncomeCYId.of(YouPartnerBoth.Both),
          OtherIncomeAmountCYId.of(OtherIncomeAmountCY(20, 20)),
          BothAnyTheseBenefitsCYId.of(true),
          WhosHadBenefitsId.of(YouPartnerBoth.Both),
          BenefitsIncomeCYId.of(BenefitsIncomeCY(20, 20))
        )

        // Partner earning more than minimum earnings and Parent earning less than minimum earnings
        val originalCacheMap3 = CacheMap.of(
          WhoGetsVouchersId.of(YouPartnerBothNeitherNotSure.You),
          YourAgeId.of(Age.UnderEighteen),
          YourPartnersAgeId.of(Age.UnderEighteen),
          YourMinimumEarningsId.of(false),
          PartnerMinimumEarningsId.of(true),
          AreYouSelfEmployedOrApprenticeId.of(EmploymentStatus.SelfEmployed),
          PartnerMaximumEarningsId.of(true),
          EmploymentIncomeCYId.of(EmploymentIncomeCY(20, 20)),
          BothPaidPensionCYId.of(true),
          WhoPaysIntoPensionId.of(YouPartnerBoth.Both),
          HowMuchBothPayPensionId.of(HowMuchBothPayPension(20, 20)),
          BothOtherIncomeThisYearId.of(true),
          WhoGetsOtherIncomeCYId.of(YouPartnerBoth.Both),
          OtherIncomeAmountCYId.of(OtherIncomeAmountCY(20, 20)),
          BothAnyTheseBenefitsCYId.of(true),
          WhosHadBenefitsId.of(YouPartnerBoth.Both),
          BenefitsIncomeCYId.of(BenefitsIncomeCY(20, 20))
        )

        val result1 = cascadeUpsert(WhoIsInPaidEmploymentId, YouPartnerBothNeither.You, originalCacheMap1)
        result1.data mustBe Map(
          WhoIsInPaidEmploymentId.of(YouPartnerBothNeither.You),
          YourAgeId.of(Age.UnderEighteen),
          YourMinimumEarningsId.of(true),
          YourMaximumEarningsId.of(true)
        )

        val result2 = cascadeUpsert(WhoIsInPaidEmploymentId, YouPartnerBothNeither.You, originalCacheMap2)
        result2.data mustBe Map(
          WhoIsInPaidEmploymentId.of(YouPartnerBothNeither.You),
          YourAgeId.of(Age.UnderEighteen),
          YourMinimumEarningsId.of(true)
        )

        val result3 = cascadeUpsert(WhoIsInPaidEmploymentId, YouPartnerBothNeither.You, originalCacheMap3)
        result3.data mustBe Map(
          WhoIsInPaidEmploymentId.of(YouPartnerBothNeither.You),
          YourAgeId.of(Age.UnderEighteen),
          AreYouSelfEmployedOrApprenticeId.of(EmploymentStatus.SelfEmployed),
          YourMinimumEarningsId.of(false)
        )
      }

    "remove an existing your work hours, your min and max earnings, employment," +
      " pension, benefits CY when whoIsInPaidEmployment is partner" in {

        // Parent earning less than minimum earnings
        val originalCacheMap = CacheMap.of(
          WhatIsYourTaxCodeId.of("1100L"),
          YourChildcareVouchersId.of(true),
          YourAgeId.of(Age.UnderEighteen),
          YourMinimumEarningsId.of(false),
          AreYouSelfEmployedOrApprenticeId.of(EmploymentStatus.SelfEmployed),
          PartnerPaidWorkCYId.of(true),
          ParentEmploymentIncomeCYId.of(20),
          YouPaidPensionCYId.of(true),
          HowMuchYouPayPensionId.of(20),
          YourOtherIncomeThisYearId.of(true),
          YouAnyTheseBenefitsCYId.of(true),
          YouBenefitsIncomeCYId.of(20)
        )

        val result = cascadeUpsert(WhoIsInPaidEmploymentId, YouPartnerBothNeither.Partner, originalCacheMap)
        result.data mustBe Map(WhoIsInPaidEmploymentId.of(YouPartnerBothNeither.Partner))
      }

    "remove an existing your work hours,  your vouchers your and both min and max earnings, " +
      "both employment,both pension,both benefits CY when whoIsInPaidEmployment is partner" in {

        // Partner earning less than minimum earnings and Parent earning more than minimum earnings
        val originalCacheMap1 = CacheMap.of(
          WhatIsYourTaxCodeId.of("1100L"),
          WhatIsYourPartnersTaxCodeId.of("1100L"),
          WhoGetsVouchersId.of(YouPartnerBothNeitherNotSure.You),
          YourAgeId.of(Age.UnderEighteen),
          YourPartnersAgeId.of(Age.UnderEighteen),
          YourMinimumEarningsId.of(true),
          PartnerMinimumEarningsId.of(false),
          PartnerSelfEmployedOrApprenticeId.of(EmploymentStatus.SelfEmployed),
          YourMaximumEarningsId.of(true),
          EmploymentIncomeCYId.of(EmploymentIncomeCY(20, 20)),
          BothPaidPensionCYId.of(true),
          WhoPaysIntoPensionId.of(YouPartnerBoth.Both),
          HowMuchBothPayPensionId.of(HowMuchBothPayPension(20, 20)),
          BothOtherIncomeThisYearId.of(true),
          WhoGetsOtherIncomeCYId.of(YouPartnerBoth.Both),
          OtherIncomeAmountCYId.of(OtherIncomeAmountCY(20, 20)),
          BothAnyTheseBenefitsCYId.of(true),
          WhosHadBenefitsId.of(YouPartnerBoth.Both),
          BenefitsIncomeCYId.of(BenefitsIncomeCY(20, 20))
        )

        // Partner and Parent earning more than minimum earnings
        val originalCacheMap2 = CacheMap.of(
          WhatIsYourTaxCodeId.of("1100L"),
          WhatIsYourPartnersTaxCodeId.of("1100L"),
          WhoGetsVouchersId.of(YouPartnerBothNeitherNotSure.You),
          YourAgeId.of(Age.UnderEighteen),
          YourPartnersAgeId.of(Age.UnderEighteen),
          YourMinimumEarningsId.of(true),
          PartnerMinimumEarningsId.of(true),
          EitherOfYouMaximumEarningsId.of(true),
          EmploymentIncomeCYId.of(EmploymentIncomeCY(20, 20)),
          BothPaidPensionCYId.of(true),
          WhoPaysIntoPensionId.of(YouPartnerBoth.Both),
          HowMuchBothPayPensionId.of(HowMuchBothPayPension(20, 20)),
          BothOtherIncomeThisYearId.of(true),
          WhoGetsOtherIncomeCYId.of(YouPartnerBoth.Both),
          OtherIncomeAmountCYId.of(OtherIncomeAmountCY(20, 20)),
          BothAnyTheseBenefitsCYId.of(true),
          WhosHadBenefitsId.of(YouPartnerBoth.Both),
          BenefitsIncomeCYId.of(BenefitsIncomeCY(20, 20))
        )

        // Partner earning more than minimum earnings and Parent earning less than minimum earnings
        val originalCacheMap3 = CacheMap.of(
          WhatIsYourTaxCodeId.of("1100L"),
          WhatIsYourPartnersTaxCodeId.of("1100L"),
          WhoGetsVouchersId.of(YouPartnerBothNeitherNotSure.You),
          YourAgeId.of(Age.UnderEighteen),
          YourPartnersAgeId.of(Age.UnderEighteen),
          YourMinimumEarningsId.of(false),
          PartnerMinimumEarningsId.of(true),
          AreYouSelfEmployedOrApprenticeId.of(EmploymentStatus.SelfEmployed),
          PartnerMaximumEarningsId.of(true),
          EmploymentIncomeCYId.of(EmploymentIncomeCY(20, 20)),
          BothPaidPensionCYId.of(true),
          WhoPaysIntoPensionId.of(YouPartnerBoth.Both),
          HowMuchBothPayPensionId.of(HowMuchBothPayPension(20, 20)),
          BothOtherIncomeThisYearId.of(true),
          WhoGetsOtherIncomeCYId.of(YouPartnerBoth.Both),
          OtherIncomeAmountCYId.of(OtherIncomeAmountCY(20, 20)),
          BothAnyTheseBenefitsCYId.of(true),
          WhosHadBenefitsId.of(YouPartnerBoth.Both),
          BenefitsIncomeCYId.of(BenefitsIncomeCY(20, 20))
        )

        val result1 = cascadeUpsert(WhoIsInPaidEmploymentId, YouPartnerBothNeither.Partner, originalCacheMap1)
        result1.data mustBe Map(
          WhoIsInPaidEmploymentId.of(YouPartnerBothNeither.Partner),
          YourPartnersAgeId.of(Age.UnderEighteen),
          WhatIsYourPartnersTaxCodeId.of("1100L"),
          PartnerMinimumEarningsId.of(false),
          PartnerSelfEmployedOrApprenticeId.of(EmploymentStatus.SelfEmployed)
        )

        val result2 = cascadeUpsert(WhoIsInPaidEmploymentId, YouPartnerBothNeither.Partner, originalCacheMap2)
        result2.data mustBe Map(
          WhoIsInPaidEmploymentId.of(YouPartnerBothNeither.Partner),
          YourPartnersAgeId.of(Age.UnderEighteen),
          WhatIsYourPartnersTaxCodeId.of("1100L"),
          PartnerMinimumEarningsId.of(true)
        )

        val result3 = cascadeUpsert(WhoIsInPaidEmploymentId, YouPartnerBothNeither.Partner, originalCacheMap3)
        result3.data mustBe Map(
          WhoIsInPaidEmploymentId.of(YouPartnerBothNeither.Partner),
          YourPartnersAgeId.of(Age.UnderEighteen),
          WhatIsYourPartnersTaxCodeId.of("1100L"),
          PartnerMinimumEarningsId.of(true),
          PartnerMaximumEarningsId.of(true)
        )
      }

    "remove parent childcare vouchers when whoIsInPaidEmployment is both" in {
      val originalCacheMap = CacheMap.of(
        YourChildcareVouchersId.of(true),
        PartnerPaidWorkCYId.of(true),
        ParentEmploymentIncomeCYId.of(20),
        YouPaidPensionCYId.of(true),
        HowMuchYouPayPensionId.of(20),
        YourOtherIncomeThisYearId.of(true),
        YouAnyTheseBenefitsCYId.of(true),
        YouBenefitsIncomeCYId.of(20)
      )

      val result = cascadeUpsert(WhoIsInPaidEmploymentId, YouPartnerBothNeither.Both, originalCacheMap)
      result.data mustBe Map(WhoIsInPaidEmploymentId.of(YouPartnerBothNeither.Both)) // TODO Add in Statutory Data
    }

    "remove partner childcare vouchers when whoIsInPaidEmployment is both" in {
      val originalCacheMap = CacheMap.of(
        PartnerChildcareVouchersId.of(true),
        ParentPaidWorkCYId.of(true),
        PartnerEmploymentIncomeCYId.of(20),
        PartnerPaidPensionCYId.of(true),
        HowMuchPartnerPayPensionId.of(20),
        PartnerBenefitsIncomeCYId.of(20)
      )

      val result = cascadeUpsert(WhoIsInPaidEmploymentId, YouPartnerBothNeither.Both, originalCacheMap)
      result.data mustBe Map(WhoIsInPaidEmploymentId.of(YouPartnerBothNeither.Both)) // TODO Add in Statutory Data
    }
  }

  "saving the your age" must {
    "removing an existing yourMinimumEarnings when user change the selection to age under18" in {
      val originalCacheMap = CacheMap.of(YourAgeId.of(Age.EighteenToTwenty), YourMinimumEarningsId.of(true))

      val result = cascadeUpsert(YourAgeId, Age.UnderEighteen, originalCacheMap)
      result.data mustBe Map(YourAgeId.of(Age.UnderEighteen))
    }

    "removing an existing yourMinimumEarnings and areYouSelfEmployedOrApprentice when user change the selection to age 18-20" in {
      val originalCacheMap = CacheMap.of(
        YourAgeId.of(Age.UnderEighteen),
        YourMinimumEarningsId.of(false),
        AreYouSelfEmployedOrApprenticeId.of(EmploymentStatus.Neither)
      )

      val result = cascadeUpsert(YourAgeId, Age.EighteenToTwenty, originalCacheMap)
      result.data mustBe Map(YourAgeId.of(Age.EighteenToTwenty))
    }

    "removing an existing yourMinimumEarnings areYouSelfEmployedOrApprentice and yourSelfEmployed when user change the selection to age 20-24" in {
      val originalCacheMap = CacheMap.of(
        YourAgeId.of(Age.UnderEighteen),
        YourMinimumEarningsId.of(false),
        AreYouSelfEmployedOrApprenticeId.of(EmploymentStatus.SelfEmployed),
        YourSelfEmployedId.of(true)
      )

      val result = cascadeUpsert(YourAgeId, Age.TwentyOneOrOver, originalCacheMap)
      result.data mustBe Map(YourAgeId.of(Age.TwentyOneOrOver))
    }

    "removing an existing yourMinimumEarnings  when user change the selection to age over 25" in {
      val originalCacheMap = CacheMap.of(YourAgeId.of(Age.UnderEighteen), YourMinimumEarningsId.of(true))

      val result = cascadeUpsert(YourAgeId, Age.TwentyOneOrOver, originalCacheMap)
      result.data mustBe Map(YourAgeId.of(Age.TwentyOneOrOver))
    }

    " not removing an existing your minimumEarnings  when user change the selection to age 18-20 again" in {
      val originalCacheMap = CacheMap.of(YourAgeId.of(Age.EighteenToTwenty), YourMinimumEarningsId.of(true))

      val result = cascadeUpsert(YourAgeId, Age.EighteenToTwenty, originalCacheMap)
      result.data mustBe Map(
        YourAgeId.of(Age.EighteenToTwenty),
        YourMinimumEarningsId.of(true)
      )
    }
  }

  "saving the partner age" must {
    "removing an existing partnerMinimumEarnings when user change the selection to age under18" in {
      val originalCacheMap = CacheMap.of(
        YourPartnersAgeId.of(Age.EighteenToTwenty),
        PartnerMinimumEarningsId.of(true)
      )

      val result = cascadeUpsert(YourPartnersAgeId, Age.UnderEighteen, originalCacheMap)
      result.data mustBe Map(YourPartnersAgeId.of(Age.UnderEighteen))
    }

    "removing an existing yourMinimumEarnings ,selfEmployedOrApprentice when user change the selection to age 18-20" in {
      val originalCacheMap = CacheMap.of(
        YourPartnersAgeId.of(Age.UnderEighteen),
        PartnerMinimumEarningsId.of(false),
        PartnerSelfEmployedOrApprenticeId.of(EmploymentStatus.Neither)
      )

      val result = cascadeUpsert(YourPartnersAgeId, Age.EighteenToTwenty, originalCacheMap)
      result.data mustBe Map(YourPartnersAgeId.of(Age.EighteenToTwenty))
    }

    "removing an existing yourMinimumEarnings selfEmployedOrApprentice and SelfEmployed when user change the selection to age 20-24" in {
      val originalCacheMap = CacheMap.of(
        YourPartnersAgeId.of(Age.UnderEighteen),
        PartnerMinimumEarningsId.of(false),
        PartnerSelfEmployedOrApprenticeId.of(EmploymentStatus.SelfEmployed),
        PartnerSelfEmployedId.of(true)
      )

      val result = cascadeUpsert(YourPartnersAgeId, Age.TwentyOneOrOver, originalCacheMap)
      result.data mustBe Map(YourPartnersAgeId.of(Age.TwentyOneOrOver))
    }

    "removing an existing yourMinimumEarnings, maximumEarnings when user change the selection to age over 25" in {
      val originalCacheMap = CacheMap.of(YourPartnersAgeId.of(Age.UnderEighteen), PartnerMinimumEarningsId.of(true))

      val result = cascadeUpsert(YourPartnersAgeId, Age.TwentyOneOrOver, originalCacheMap)
      result.data mustBe Map(YourPartnersAgeId.of(Age.TwentyOneOrOver))
    }

    "not removing an existing yourMinimumEarnings maximum earnings when user change the selection to age under18 again" in {
      val originalCacheMap = CacheMap.of(YourPartnersAgeId.of(Age.UnderEighteen), PartnerMinimumEarningsId.of(true))

      val result = cascadeUpsert(YourPartnersAgeId, Age.UnderEighteen, originalCacheMap)
      result.data mustBe Map(
        YourPartnersAgeId.of(Age.UnderEighteen),
        PartnerMinimumEarningsId.of(true)
      )
    }
  }

  "saving the your minimumEarnings" must {
    "remove your maximum earnings and either of you max earnings whenparent in paid employment and your minimum earnings is no" in {
      val originalCacheMap = CacheMap.of(YourMaximumEarningsId.of(false))

      val result = cascadeUpsert(YourMinimumEarningsId, false, originalCacheMap)
      result.data mustBe Map(YourMinimumEarningsId.of(false))
    }

    "remove you self employed or apprentice and you self employed less than 12 months when minimum earnings is yes" in {
      val originalCacheMap = CacheMap.of(
        AreYouSelfEmployedOrApprenticeId.of(EmploymentStatus.SelfEmployed),
        YourSelfEmployedId.of(true)
      )

      val result = cascadeUpsert(YourMinimumEarningsId, true, originalCacheMap)
      result.data mustBe Map(YourMinimumEarningsId.of(true))
    }
  }

  "saving the your partners minimumEarnings" must {
    "remove partners and either of you maximum earnings when partners minimum earnings is no" in {
      val originalCacheMap = CacheMap.of(PartnerMaximumEarningsId.of(false))

      val result = cascadeUpsert(PartnerMinimumEarningsId, false, originalCacheMap)
      result.data mustBe Map(PartnerMinimumEarningsId.of(false))
    }

    "remove your either of you max earnings when both in paid employment  and your minimum earnings is no" in {
      val originalCacheMap = CacheMap.of(EitherOfYouMaximumEarningsId.of(true))

      val result = cascadeUpsert(PartnerMinimumEarningsId, false, originalCacheMap)
      result.data mustBe Map(PartnerMinimumEarningsId.of(false))
    }

    "remove your partners self employed or apprentice and partners self employed less than 12 months when partners minimum earnings is yes" in {
      val originalCacheMap = CacheMap.of(
        PartnerSelfEmployedOrApprenticeId.of(EmploymentStatus.SelfEmployed),
        PartnerSelfEmployedId.of(true)
      )

      val result = cascadeUpsert(PartnerMinimumEarningsId, true, originalCacheMap)
      result.data mustBe Map(PartnerMinimumEarningsId.of(true))
    }
  }

  "saving are you self employed or apprentice" must {
    "remove your self employed selection when parent select apprentice" in {
      val originalCacheMap = CacheMap.of(YourSelfEmployedId.of(false))

      val result = cascadeUpsert(
        AreYouSelfEmployedOrApprenticeId,
        EmploymentStatus.Apprentice,
        originalCacheMap
      )
      result.data mustBe Map(
        AreYouSelfEmployedOrApprenticeId.of(EmploymentStatus.Apprentice)
      )
    }

    "remove your self employed selection when parent select neither" in {
      val originalCacheMap = CacheMap.of(YourSelfEmployedId.of(false))

      val result = cascadeUpsert(
        AreYouSelfEmployedOrApprenticeId,
        EmploymentStatus.Neither,
        originalCacheMap
      )
      result.data mustBe Map(
        AreYouSelfEmployedOrApprenticeId.of(EmploymentStatus.Neither)
      )
    }
  }

  "saving partner self employed or apprentice" must {
    "remove partner self employed selection when partner select apprentice" in {
      val originalCacheMap = CacheMap.of(PartnerSelfEmployedId.of(false))

      val result = cascadeUpsert(
        PartnerSelfEmployedOrApprenticeId,
        EmploymentStatus.Apprentice,
        originalCacheMap
      )
      result.data mustBe Map(
        PartnerSelfEmployedOrApprenticeId.of(EmploymentStatus.Apprentice)
      )
    }

    "remove partner self employed selection when partner select neither" in {
      val originalCacheMap = CacheMap.of(PartnerSelfEmployedId.of(false))

      val result = cascadeUpsert(
        PartnerSelfEmployedOrApprenticeId,
        EmploymentStatus.Neither,
        originalCacheMap
      )
      result.data mustBe Map(
        PartnerSelfEmployedOrApprenticeId.of(EmploymentStatus.Neither)
      )
    }
  }

  // Need to work on clearence for maximum earnings 'no' to clear noOfChildren data and further

  "session management" must {
    "clear all the cache Map data" in {

      val originalCacheMap = CacheMap.of(
        LocationId.of(Location.England),
        PartnerSelfEmployedId.of(false)
      )
      val result = cascadeUpsert(SessionDataClearId, "sessionData", originalCacheMap)

      result.data mustBe Map(SessionDataClearId.of("sessionData"))

    }
  }

}
