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

package uk.gov.hmrc.childcarecalculatorfrontend.helpers

import akka.util.Timeout
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach}
import org.scalatestplus.play.{OneServerPerSuite, PlaySpec}
import play.api.test.{DefaultAwaitTimeout, FutureAwaits}
import uk.gov.hmrc.mongo.MongoSpecSupport

import scala.concurrent.duration._

trait IntegrationSpecBase extends PlaySpec
  with OneServerPerSuite
  with ScalaFutures
  with IntegrationPatience
  with WiremockHelper
  with BeforeAndAfterEach
  with BeforeAndAfterAll
  with FutureAwaits
  with DefaultAwaitTimeout
  with MongoSpecSupport with FakeConfig {

  val mockPort = 11111
  val mockHost = "localhost"
  val url = s"http://$mockHost:$mockPort"

  override implicit def defaultAwaitTimeout: Timeout = 5.seconds

  override def beforeEach(): Unit = {
    super.beforeEach()
    resetWiremock()
  }

  override def beforeAll(): Unit = {
    super.beforeAll()
    startWiremock()
  }

  override def afterAll(): Unit = {
    super.afterAll()
    stopWiremock()
  }
}
