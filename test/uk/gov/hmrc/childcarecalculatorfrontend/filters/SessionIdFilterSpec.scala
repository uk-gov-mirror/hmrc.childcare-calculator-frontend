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

package uk.gov.hmrc.childcarecalculatorfrontend.filters

import java.util.UUID

import akka.stream.Materializer
import com.google.inject.Inject
import org.scalatest.{MustMatchers, WordSpec}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.http.{DefaultHttpFilters, HttpFilters}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import play.api.mvc.{Action, Results}
import play.api.routing.Router
import play.api.test.FakeRequest
import play.api.test.Helpers._
import uk.gov.hmrc.http.{HeaderNames, SessionKeys}

import scala.concurrent.ExecutionContext

object SessionIdFilterSpec {

  val sessionId = "28836767-a008-46be-ac18-695ab140e705"

  class Filters @Inject() (sessionId: SessionIdFilter) extends DefaultHttpFilters(sessionId)

  class TestSessionIdFilter @Inject() (
                                        override val mat: Materializer,
                                        ec: ExecutionContext
                                      ) extends SessionIdFilter(mat, UUID.fromString(sessionId), ec)
}

class SessionIdFilterSpec extends WordSpec with MustMatchers with GuiceOneAppPerSuite {

  import SessionIdFilterSpec._

  val router: Router = {

    import play.api.routing.sird._

    Router.from {
      case GET(p"/test1") => Action {
        implicit request =>
          val fromHeader = request.headers.get(HeaderNames.xSessionId).getOrElse("")
          val fromSession = request.session.get(SessionKeys.sessionId).getOrElse("")
          Results.Ok(
            Json.obj(
              "fromHeader" -> fromHeader,
              "fromSession" -> fromSession
            )
          )
      }
    }
  }

  override lazy val app: Application = {

    import play.api.inject._

    new GuiceApplicationBuilder()
      .overrides(
        bind[HttpFilters].to[SessionIdFilterSpec.Filters],
        bind[SessionIdFilter].to[TestSessionIdFilter]
      )
      .configure(
        "play.filters.disabled" -> List("uk.gov.hmrc.play.bootstrap.filters.frontend.crypto.SessionCookieCryptoFilter")
      )
      .router(router)
      .build()
  }

  ".apply" must {

    "add a sessionId if one doesn't already exist" in {

      val Some(result) = route(app, FakeRequest(GET, "/test1"))

      session(result).data must contain(SessionKeys.sessionId -> s"session-$sessionId")
    }

    "not override a sessionId if one already exists" in {

      val Some(result) = route(app, FakeRequest(GET, "/test1").withSession(SessionKeys.sessionId -> "foo"))

      val body = contentAsJson(result)

      (body \ "fromHeader").as[String] mustEqual ""
      (body \ "fromSession").as[String] mustEqual "foo"
    }

    "persist the Id in the session" in {

      val Some(result) = route(app, FakeRequest(GET, "/test1"))
      session(result).data must contain(SessionKeys.sessionId -> s"session-$sessionId")
    }

    "not override other session values from the request" in {

      val Some(result) = route(app, FakeRequest(GET, "/test1").withSession("foo" -> "bar"))
      session(result).data must contain("foo" -> "bar")
    }
  }
}
