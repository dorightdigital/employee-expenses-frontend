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
import models.{CheckMode, NormalMode}
import pages.textiles.TextilesOccupationList1Page

class TextilesNavigatorSpec extends SpecBase {
  private val modes = Seq(NormalMode, CheckMode)
  val navigator = new TextilesNavigator

  "TextilesNavigator" when {
    for (mode <- modes) {
      s"in $mode" must {
        "go to EmployerContribution from Textiles when 'Yes' is selected" in {
          val answers = emptyUserAnswers.set(TextilesOccupationList1Page, true).success.value

          navigator.nextPage(TextilesOccupationList1Page, mode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(mode)
        }

        "go to EmployerContribution from Textiles when 'No' is selected" in {
          val answers = emptyUserAnswers.set(TextilesOccupationList1Page, false).success.value

          navigator.nextPage(TextilesOccupationList1Page, mode)(answers) mustBe
            controllers.routes.EmployerContributionController.onPageLoad(mode)
        }
      }
    }
  }
}