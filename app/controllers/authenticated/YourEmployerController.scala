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

package controllers.authenticated

import config.NavConstant
import controllers.actions._
import controllers.routes._
import controllers.authenticated.routes._
import forms.authenticated.YourEmployerFormProvider
import javax.inject.{Inject, Named}
import models.Mode
import navigation.Navigator
import pages.YourEmployerName
import pages.authenticated.{TaxYearSelectionPage, YourEmployerPage}
import play.api.data.Form
import play.api.i18n.{I18nSupport, MessagesApi}
import play.api.mvc.{Action, AnyContent, MessagesControllerComponents}
import repositories.SessionRepository
import service.TaiService
import uk.gov.hmrc.play.bootstrap.controller.FrontendBaseController
import views.html.authenticated.YourEmployerView

import scala.concurrent.{ExecutionContext, Future}

class YourEmployerController @Inject()(
                                        override val messagesApi: MessagesApi,
                                        sessionRepository: SessionRepository,
                                        @Named(NavConstant.authenticated) navigator: Navigator,
                                        identify: IdentifierAction,
                                        getData: DataRetrievalAction,
                                        requireData: DataRequiredAction,
                                        formProvider: YourEmployerFormProvider,
                                        val controllerComponents: MessagesControllerComponents,
                                        taiService: TaiService,
                                        view: YourEmployerView
                                      )(implicit ec: ExecutionContext) extends FrontendBaseController with I18nSupport {

  val form: Form[Boolean] = formProvider()

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val preparedForm = request.userAnswers.get(YourEmployerPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }


      request.userAnswers.get(TaxYearSelectionPage) match {
        case Some(taxYears) =>
          taiService.employments(request.nino.get, taxYears.head).map {
            employments =>
              if (employments.nonEmpty) {
                if (employments.forall(p => p.endDate.isDefined)) {
                  Ok(view(preparedForm, mode, employments.head.name))
                } else {
                  Ok(view(preparedForm, mode, employments.filter(p => p.endDate.isEmpty).head.name))
                }
              } else {
                Redirect(UpdateEmployerInformationController.onPageLoad())
              }
          }
        case _ =>
          Future.successful(Redirect(SessionExpiredController.onPageLoad()))
      }

  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      request.userAnswers.get(TaxYearSelectionPage) match {
        case Some(taxYears) =>
          taiService.employments(request.nino.get, taxYears.head).flatMap {
            employments =>
              form.bindFromRequest().fold(
                (formWithErrors: Form[_]) =>
                  Future.successful(BadRequest(view(formWithErrors, mode, employments.head.name))),

                value => {
                  for {
                    ua1 <- Future.fromTry(request.userAnswers.set(YourEmployerPage, value))
                    ua2 <- Future.fromTry(ua1.set(YourEmployerName, employments.head.name))
                    _ <- sessionRepository.set(ua2)
                  } yield Redirect(navigator.nextPage(YourEmployerPage, mode)(ua2))
                }
              )
          }
        case _ =>
          Future.successful(Redirect(SessionExpiredController.onPageLoad()))
      }

  }
}