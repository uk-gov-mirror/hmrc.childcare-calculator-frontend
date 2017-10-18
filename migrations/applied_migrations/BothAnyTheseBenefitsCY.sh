#!/bin/bash

echo "Applying migration BothAnyTheseBenefitsCY"

echo "Adding routes to conf/app.routes"

echo "" >> ../conf/app.routes
echo "GET        /bothAnyTheseBenefitsCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothAnyTheseBenefitsCYController.onPageLoad(mode: Mode = NormalMode)" >> ../conf/app.routes
echo "POST       /bothAnyTheseBenefitsCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothAnyTheseBenefitsCYController.onSubmit(mode: Mode = NormalMode)" >> ../conf/app.routes

echo "GET        /changeBothAnyTheseBenefitsCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothAnyTheseBenefitsCYController.onPageLoad(mode: Mode = CheckMode)" >> ../conf/app.routes
echo "POST       /changeBothAnyTheseBenefitsCY                       uk.gov.hmrc.childcarecalculatorfrontend.controllers.BothAnyTheseBenefitsCYController.onSubmit(mode: Mode = CheckMode)" >> ../conf/app.routes

echo "Adding messages to conf.messages"
echo "" >> ../conf/messages.en
echo "bothAnyTheseBenefitsCY.title = bothAnyTheseBenefitsCY" >> ../conf/messages.en
echo "bothAnyTheseBenefitsCY.heading = bothAnyTheseBenefitsCY" >> ../conf/messages.en
echo "bothAnyTheseBenefitsCY.checkYourAnswersLabel = bothAnyTheseBenefitsCY" >> ../conf/messages.en

echo "Adding helper line into UserAnswers"
awk '/class/ {\
     print;\
     print "  def bothAnyTheseBenefitsCY: Option[Boolean] = cacheMap.getEntry[Boolean](BothAnyTheseBenefitsCYId.toString)";\
     print "";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/UserAnswers.scala

echo "Adding helper method to CheckYourAnswersHelper"
awk '/class/ {\
     print;\
     print "";\
     print "  def bothAnyTheseBenefitsCY: Option[AnswerRow] = userAnswers.bothAnyTheseBenefitsCY map {";\
     print "    x => AnswerRow(\"bothAnyTheseBenefitsCY.checkYourAnswersLabel\", if(x) \"site.yes\" else \"site.no\", true, routes.BothAnyTheseBenefitsCYController.onPageLoad(CheckMode).url)"; print "  }";\
     next }1' ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala > tmp && mv tmp ../app/uk/gov/hmrc/childcarecalculatorfrontend/utils/CheckYourAnswersHelper.scala

echo "Moving test files from generated-test/ to test/"
rsync -avm --include='*.scala' -f 'hide,! */' ../generated-test/ ../test/
rm -rf ../generated-test/

echo "Migration BothAnyTheseBenefitsCY complete"
