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

package navigation

import base.SpecBase
import models.NormalMode
import pages.heating.HeatingOccupationListPage

class HeatingNavigatorSpec extends SpecBase {

  val navigator = new HeatingNavigator


  "Heating Navigator" when {
    "in Normal mode" must {

      "from Heating" must {

        "go to EmployerContribution when 'Yes' is selected" in {
          val answers = emptyUserAnswers.set(HeatingOccupationListPage, true).success.value

          navigator.nextPage(HeatingOccupationListPage, NormalMode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(NormalMode)
        }

        "go to EmployerContribution when 'No' is selected" in {
          val answers = emptyUserAnswers.set(HeatingOccupationListPage, false).success.value

          navigator.nextPage(HeatingOccupationListPage, NormalMode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(NormalMode)

        }
      }

    }
  }

}