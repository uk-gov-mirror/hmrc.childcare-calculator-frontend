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

package uk.gov.hmrc.childcarecalculatorfrontend.navigation

import javax.inject.Inject

import play.api.mvc.Call
import uk.gov.hmrc.childcarecalculatorfrontend.controllers.routes
import uk.gov.hmrc.childcarecalculatorfrontend.identifiers._
import uk.gov.hmrc.childcarecalculatorfrontend.models._
import uk.gov.hmrc.childcarecalculatorfrontend.models.schemes._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.ChildcareConstants._
import uk.gov.hmrc.childcarecalculatorfrontend.utils.{SessionExpiredRouter, UserAnswers, Utils}

class MaximumHoursNavigator @Inject() (
                                        utils: Utils,
                                        override val schemes: Schemes,
                                        maxHours: MaxFreeHours,
                                        taxCredits: TaxCredits,
                                        tfc: TaxFreeChildcare,
                                        esc: EmploymentSupportedChildcare
                                      ) extends ResultsNavigator {

  override protected lazy val resultLocation: Call = routes.ResultController.onPageLoad()



  override protected def routeMap: Map[Identifier, UserAnswers => Call] = Map(
    DoYouLiveWithPartnerId -> doYouLiveRoute,
    AreYouInPaidWorkId -> areYouInPaidWorkRoute,
    WhoIsInPaidEmploymentId -> whoIsInPaidWorkRoute,
    ParentWorkHoursId -> parentWorkHoursRoute,
    PartnerWorkHoursId -> partnerWorkHoursRoute,
    YourChildcareVouchersId -> yourChildcareVoucherRoute,
    PartnerChildcareVouchersId -> (_ => routes.DoYouOrYourPartnerGetAnyBenefitsController.onPageLoad(NormalMode)),
    WhoGetsVouchersId -> (_ => routes.DoYouOrYourPartnerGetAnyBenefitsController.onPageLoad(NormalMode)),
    DoYouGetAnyBenefitsId -> doYouGetAnyBenefitsRoute,
    WhoGetsBenefitsId -> whoGetsBenefitsRoute,
    YourAgeId -> yourAgeRoute,
    YourPartnersAgeId -> yourPartnerAgeRoute,
    YourMinimumEarningsId -> yourMinimumEarningsRoute,
    PartnerMinimumEarningsId -> partnerMinimumEarningsRoute,
    AreYouSelfEmployedOrApprenticeId -> areYouSelfEmployedOrApprenticeRoute,
    PartnerSelfEmployedOrApprenticeId -> partnerSelfEmployedOrApprenticeRoute,
    YourSelfEmployedId -> yourSelfEmployedRoute,
    PartnerSelfEmployedId -> partnerSelfEmployedRoute,
    YourMaximumEarningsId -> yourMaximumEarningsRoute,
    PartnerMaximumEarningsId ->  partnerMaximumEarningsRoute,
    EitherOfYouMaximumEarningsId -> eitherMaximumEarningsRoute,
    TaxOrUniversalCreditsId -> taxOrUniversalCreditsRoutes,
    DoYouOrYourPartnerGetAnyBenefitsId -> doYouOrYourPartnerGetAnyBenefitsRoute,
    WhichBenefitsYouGetId -> whichBenefitsYouGetRoute,
    WhichBenefitsPartnerGetId -> whichBenefitsPartnerGetRoute
  )

  val SelfEmployed: String = SelfEmployedOrApprenticeOrNeitherEnum.SELFEMPLOYED.toString

  private def doYouLiveRoute(answers: UserAnswers): Call = {
    if (answers.doYouLiveWithPartner.contains(true)) {
      routes.WhoIsInPaidEmploymentController.onPageLoad(NormalMode)
    } else {
      routes.AreYouInPaidWorkController.onPageLoad(NormalMode)
    }
  }

  private def areYouInPaidWorkRoute(answers: UserAnswers): Call = {
    if (answers.areYouInPaidWork.contains(true)) {
      routes.ParentWorkHoursController.onPageLoad(NormalMode)
    } else {
      routes.ResultController.onPageLoad()
    }
  }


  private def whoIsInPaidWorkRoute(answers: UserAnswers): Call = {
    answers.isYouPartnerOrBoth(answers.whoIsInPaidEmployment) match {
      case `you` => routes.ParentWorkHoursController.onPageLoad(NormalMode)
      case `partner` | `both` => routes.PartnerWorkHoursController.onPageLoad(NormalMode)
      case `neither` => routes.ResultController.onPageLoad()
    }
  }

  private def partnerWorkHoursRoute(answers: UserAnswers): Call = {
    if (answers.whoIsInPaidEmployment.contains(both)) {
      routes.ParentWorkHoursController.onPageLoad(NormalMode)
    } else {
      routes.PartnerChildcareVouchersController.onPageLoad(NormalMode)
    }
  }

  private def parentWorkHoursRoute(answers: UserAnswers) : Call = {
    if (answers.whoIsInPaidEmployment.contains(both)) {
      routes.WhoGetsVouchersController.onPageLoad(NormalMode)
    } else {
      routes.YourChildcareVouchersController.onPageLoad(NormalMode)
    }
  }


  private def yourChildcareVoucherRoute(answers: UserAnswers): Call =
    utils.getCall(answers.doYouLiveWithPartner) {
      case true => routes.DoYouOrYourPartnerGetAnyBenefitsController.onPageLoad(NormalMode)
      case false => routes.DoYouGetAnyBenefitsController.onPageLoad(NormalMode)
    }


  private def doYouGetAnyBenefitsRoute(answers: UserAnswers): Call = {
    answers.doYouGetAnyBenefits.map {
      youGetBenefits =>
        if (youGetBenefits) {
          routes.WhichBenefitsYouGetController.onPageLoad(NormalMode)
        } else {
          routes.YourAgeController.onPageLoad(NormalMode)
        }
    }.getOrElse(SessionExpiredRouter.route(getClass.getName,"doYouGetAnyBenefitsRoute",Some(answers)))
  }

  private def doYouOrYourPartnerGetAnyBenefitsRoute(answers: UserAnswers): Call = {

    if (answers.doYouOrYourPartnerGetAnyBenefits.contains(true)) {
      routes.WhoGetsBenefitsController.onPageLoad(NormalMode)
    } else if(isEligibleToGoToResultPage(answers)) {
      routes.ResultController.onPageLoad()
    } else if(answers.whoIsInPaidEmployment.contains(partner)){
      routes.YourPartnersAgeController.onPageLoad(NormalMode)
    } else if(answers.whoIsInPaidEmployment.contains(you)||answers.whoIsInPaidEmployment.contains(both)){
      routes.YourAgeController.onPageLoad(NormalMode)
    } else SessionExpiredRouter.route(getClass.getName,"doYouOrYourPartnerGetAnyBenefitsRoute",Some(answers))
  }

  private def isEligibleToGoToResultPage(answers: UserAnswers) = answers.doYouLiveWithPartner.getOrElse(false) && (answers.whoIsInPaidEmployment.contains(you) || answers.whoIsInPaidEmployment.contains(partner)) &&
                                         taxCredits.eligibility(answers).equals(NotEligible) && esc.eligibility(answers).equals(NotEligible)

  private def whoGetsBenefitsRoute(answers: UserAnswers): Call = {
    if (answers.isYouPartnerOrBoth(answers.whoGetsBenefits).contains(partner)) {
      routes.WhichBenefitsPartnerGetController.onPageLoad(NormalMode)
    } else {
      routes.WhichBenefitsYouGetController.onPageLoad(NormalMode)
    }
  }

  private def whichBenefitsYouGetRoute(answers: UserAnswers): Call = {
    if (answers.doYouLiveWithPartner.contains(true)) {
      if (answers.whoGetsBenefits.contains(YouPartnerBothEnum.BOTH.toString)) {
        routes.WhichBenefitsPartnerGetController.onPageLoad(NormalMode)
      } else if (answers.whoGetsBenefits.contains(YouPartnerBothEnum.YOU.toString)) {
        if (!answers.whoIsInPaidEmployment.contains(partner)) {
          routes.YourAgeController.onPageLoad(NormalMode)
        } else routes.YourPartnersAgeController.onPageLoad(NormalMode)
      } else SessionExpiredRouter.route(getClass.getName,"whichBenefitsYouGetRoute",Some(answers))
    } else if(answers.doYouLiveWithPartner.contains(false)) {
      routes.YourAgeController.onPageLoad(NormalMode)
    } else SessionExpiredRouter.route(getClass.getName,"whichBenefitsYouGetRoute",Some(answers))

  }

  private def whichBenefitsPartnerGetRoute(answers: UserAnswers): Call = {
    if(answers.whoIsInPaidEmployment.contains(partner)) {
        routes.YourPartnersAgeController.onPageLoad(NormalMode)
      } else routes.YourAgeController.onPageLoad(NormalMode)
  }


  private def yourAgeRoute(answers: UserAnswers) = {
    if(answers.isYouPartnerOrBoth(answers.whoIsInPaidEmployment).contains(you)) {
      routes.YourMinimumEarningsController.onPageLoad(NormalMode)
    } else {
      routes.YourPartnersAgeController.onPageLoad(NormalMode)
    }
  }

  private def yourPartnerAgeRoute(answers: UserAnswers): Call = {
    if(answers.isYouPartnerOrBoth(answers.whoIsInPaidEmployment).contains(partner)) {
      routes.PartnerMinimumEarningsController.onPageLoad(NormalMode)
    } else {
      routes.YourMinimumEarningsController.onPageLoad(NormalMode)
    }
  }

  private def yourMinimumEarningsRoute(answers: UserAnswers): Call = {
    if(answers.isYouPartnerOrBoth(answers.whoIsInPaidEmployment).contains(both)) {
      routes.PartnerMinimumEarningsController.onPageLoad(NormalMode)
    } else if(answers.yourMinimumEarnings.contains(true)) {
      routes.YourMaximumEarningsController.onPageLoad(NormalMode)
    } else {
      routes.AreYouSelfEmployedOrApprenticeController.onPageLoad(NormalMode)
    }
  }

  private def partnerMinimumEarningsRoute(answers: UserAnswers): Call = {
    if(answers.isYouPartnerOrBoth(answers.whoIsInPaidEmployment).contains(partner)) {
      if (answers.partnerMinimumEarnings.contains(true)) {
        routes.PartnerMaximumEarningsController.onPageLoad(NormalMode)
      } else {
        routes.PartnerSelfEmployedOrApprenticeController.onPageLoad(NormalMode)
      }
    } else if(answers.yourMinimumEarnings.contains(false)) {
        routes.AreYouSelfEmployedOrApprenticeController.onPageLoad(NormalMode)
      } else {
      if(answers.partnerMinimumEarnings.contains(true)) {
        routes.EitherOfYouMaximumEarningsController.onPageLoad(NormalMode)
      } else {
        routes.PartnerSelfEmployedOrApprenticeController.onPageLoad(NormalMode)
      }
    }
  }

  private def areYouSelfEmployedOrApprenticeRoute(answers: UserAnswers): Call = {
    if(answers.areYouSelfEmployedOrApprentice.contains(SelfEmployed)) {
      routes.YourSelfEmployedController.onPageLoad(NormalMode)
    } else if(answers.isYouPartnerOrBoth(answers.whoIsInPaidEmployment).contains(you)) {
      routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
    } else if(answers.partnerMinimumEarnings.contains(false)) {
      routes.PartnerSelfEmployedOrApprenticeController.onPageLoad(NormalMode)
    } else {
      routes.PartnerMaximumEarningsController.onPageLoad(NormalMode)
    }
  }

  private def partnerSelfEmployedOrApprenticeRoute(answers: UserAnswers): Call = {
    if(answers.partnerSelfEmployedOrApprentice.contains(SelfEmployed)) {
      routes.PartnerSelfEmployedController.onPageLoad(NormalMode)
    } else if(answers.yourMinimumEarnings.contains(true)) {
      routes.YourMaximumEarningsController.onPageLoad(NormalMode)
    } else {
      routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
    }
  }

  private def yourSelfEmployedRoute(answers: UserAnswers): Call = {
    if(answers.isYouPartnerOrBoth(answers.whoIsInPaidEmployment).contains(you)) {
      routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
    } else if(answers.partnerMinimumEarnings.contains(true)) {
      routes.PartnerMaximumEarningsController.onPageLoad(NormalMode)
    } else {
      routes.PartnerSelfEmployedOrApprenticeController.onPageLoad(NormalMode)
    }
  }

  private def partnerSelfEmployedRoute(answers: UserAnswers): Call = {
    if(answers.yourMinimumEarnings.contains(true)) {
      routes.YourMaximumEarningsController.onPageLoad(NormalMode)
    } else {
      routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
    }
  }

  private def yourMaximumEarningsRoute(answers: UserAnswers): Call = {
    val yourMaxEarnings = answers.yourMaximumEarnings.getOrElse(false)

    maximumEarningsRedirection(answers, yourMaxEarnings)
  }

  private def partnerMaximumEarningsRoute(answers: UserAnswers): Call = {
    val partnerMaxEarnings= answers.partnerMaximumEarnings.getOrElse(false)

    maximumEarningsRedirection(answers, partnerMaxEarnings)
  }

  private def eitherMaximumEarningsRoute(answers: UserAnswers): Call = {
    val eitherMaxEarnings = answers.eitherOfYouMaximumEarnings.getOrElse(false)

    maximumEarningsRedirection(answers, eitherMaxEarnings)
  }

  private def maximumEarningsRedirection(answers: UserAnswers, maxEarnings: Boolean): Call = {

    def getCallForVoucherValue(voucherValue: Boolean): Call =
      if (!voucherValue.equals(true) && maxEarnings.equals(true)) {
        routes.ResultController.onPageLoad()
      } else {
        routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode)
      }

    (answers.yourChildcareVouchers, answers.partnerChildcareVouchers) match {
      case (Some(parentVoucher), _) => getCallForVoucherValue(parentVoucher)
      case (_, Some(partnerVoucher)) => getCallForVoucherValue(partnerVoucher)
      case _ =>  answers.whoGetsVouchers.fold(
        SessionExpiredRouter.route(getClass.getName,"maximumEarningsRedirection",Some(answers)))(_ => routes.TaxOrUniversalCreditsController.onPageLoad(NormalMode))
    }
  }

  private def taxOrUniversalCreditsRoutes(answers: UserAnswers): Call = {

    if (schemes.allSchemesDetermined(answers)) {
      if (
        taxCredits.eligibility(answers) == NotEligible &&
         tfc.eligibility(answers) == NotEligible &&
          esc.eligibility(answers) == NotEligible) {

        routes.ResultController.onPageLoad()
      } else if(answers.hasApprovedCosts.contains(true)) {

        if (maxHours.eligibility(answers) == Eligible) {
          routes.MaxFreeHoursInfoController.onPageLoad()
        } else {
          routes.NoOfChildrenController.onPageLoad(NormalMode)
        }

      } else {
        routes.ResultController.onPageLoad()
      }
    } else {
      SessionExpiredRouter.route(getClass.getName,"taxOrUniversalCreditsRoutes",Some(answers))
    }
  }
}
