/*
 * Copyright 2019 HM Revenue & Customs
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

package controllers.transport

import base.SpecBase
import config.{ClaimAmounts, NavConstant}
import controllers.actions.UnAuthed
import forms.transport.AirlineJobListFormProvider
import models.{NormalMode, UserAnswers}
import navigation.{FakeNavigator, Navigator}
import org.mockito.Matchers._
import org.mockito.Mockito._
import org.scalatest.OptionValues
import org.scalatest.concurrent.{IntegrationPatience, ScalaFutures}
import org.scalatest.mockito.MockitoSugar
import pages.ClaimAmount
import pages.transport.AirlineJobListPage
import play.api.Application
import play.api.inject.bind
import play.api.mvc.Call
import play.api.test.FakeRequest
import play.api.test.Helpers._
import repositories.SessionRepository
import views.html.transport.AirlineJobListView

import scala.concurrent.Future

class AirlineJobListControllerSpec extends SpecBase with ScalaFutures with MockitoSugar with IntegrationPatience with OptionValues {

  def onwardRoute = Call("GET", "/foo")

  private val formProvider = new AirlineJobListFormProvider()
  private val form = formProvider()
  private val userAnswers = emptyUserAnswers
  private val mockSessionRepository = mock[SessionRepository]

  when(mockSessionRepository.set(any(), any())) thenReturn Future.successful(true)

  lazy val airlineJobListedRoute = routes.AirlineJobListController.onPageLoad(NormalMode).url

  "AirlineJobList Controller" must {

    "return OK and the correct view for a GET" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
        .build()

      val request = FakeRequest(GET, airlineJobListedRoute)

      val result = route(application, request).value

      val view = application.injector.instanceOf[AirlineJobListView]

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form, NormalMode)(request, messages).toString

      application.stop()
    }

    "populate the view correctly on a GET when the question has previously been answered" in {

      val userAnswers = UserAnswers(userAnswersId).set(AirlineJobListPage, true).success.value

      val application = applicationBuilder(userAnswers = Some(userAnswers)).build()

      val request = FakeRequest(GET, airlineJobListedRoute)

      val view = application.injector.instanceOf[AirlineJobListView]

      val result = route(application, request).value

      status(result) mustEqual OK

      contentAsString(result) mustEqual
        view(form.fill(true), NormalMode)(request, messages).toString

      application.stop()
    }

    "redirect to the next page when valid 'Yes' data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
          .overrides(bind[Navigator].qualifiedWith(NavConstant.transport).toInstance(new FakeNavigator(onwardRoute)))
          .build()

      val request = FakeRequest(POST, airlineJobListedRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "redirect to the next page when valid 'No' data is submitted" in {

      val application =
        applicationBuilder(userAnswers = Some(emptyUserAnswers))
          .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
          .overrides(bind[Navigator].qualifiedWith(NavConstant.transport).toInstance(new FakeNavigator(onwardRoute)))
          .build()

      val request = FakeRequest(POST, airlineJobListedRoute)
          .withFormUrlEncodedBody(("value", "false"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual onwardRoute.url

      application.stop()
    }

    "return a Bad Request and errors when invalid data is submitted" in {

      val application = applicationBuilder(userAnswers = Some(emptyUserAnswers))
        .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
        .build()

      val request = FakeRequest(POST, airlineJobListedRoute)
          .withFormUrlEncodedBody(("value", ""))

      val boundForm = form.bind(Map("value" -> ""))

      val view = application.injector.instanceOf[AirlineJobListView]

      val result = route(application, request).value

      status(result) mustEqual BAD_REQUEST

      contentAsString(result) mustEqual
        view(boundForm, NormalMode)(request, messages).toString

      application.stop()
    }

    "redirect to Session Expired for a GET if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None)
        .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
        .build()

      val request = FakeRequest(GET, airlineJobListedRoute)

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "redirect to Session Expired for a POST if no existing data is found" in {

      val application = applicationBuilder(userAnswers = None)
        .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
        .build()

      val request = FakeRequest(POST, airlineJobListedRoute)
          .withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      status(result) mustEqual SEE_OTHER

      redirectLocation(result).value mustEqual controllers.routes.SessionExpiredController.onPageLoad().url

      application.stop()
    }

    "save ClaimAmount 'PilotsFlightDeck' when true" in {

      val application: Application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
        .build()

      val request = FakeRequest(POST, airlineJobListedRoute).withFormUrlEncodedBody(("value", "true"))

      val result = route(application, request).value

      val userAnswers2 = userAnswers
        .set(ClaimAmount, ClaimAmounts.Transport.Airlines.pilotsFlightDeck).success.value
        .set(AirlineJobListPage, true).success.value

      whenReady(result){
        _ =>
          verify(mockSessionRepository, times(1)).set(UnAuthed(userAnswersId), userAnswers2)
      }

      application.stop()
    }

    "save ClaimAmount 'null amount' when false" in {

      val application: Application = applicationBuilder(userAnswers = Some(userAnswers))
        .overrides(bind[SessionRepository].toInstance(mockSessionRepository))
        .build()

      val request = FakeRequest(POST, airlineJobListedRoute).withFormUrlEncodedBody(("value", "false"))

      val result = route(application, request).value

      val userAnswers2 = userAnswers
        .set(AirlineJobListPage, false).success.value

      whenReady(result){
        _ =>
          verify(mockSessionRepository, times(1)).set(UnAuthed(userAnswersId), userAnswers2)
      }

      application.stop()
    }
  }
}
