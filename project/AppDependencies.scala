import sbt._

object AppDependencies {
  import play.core.PlayVersion

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "org.reactivemongo"     %% "play2-reactivemongo"            % "0.16.0-play26",
    "uk.gov.hmrc"           %% "logback-json-logger"            % "4.4.0",
    "uk.gov.hmrc"           %% "govuk-template"                 % "5.33.0-play-26",
    "uk.gov.hmrc"           %% "play-health"                    % "3.14.0-play-26",
    "uk.gov.hmrc"           %% "play-ui"                        % "7.40.0-play-26",
    "uk.gov.hmrc"           %% "play-conditional-form-mapping"  % "0.2.0",
    "uk.gov.hmrc"           %% "bootstrap-play-26"              % "0.37.0",
    "uk.gov.hmrc"           %% "play-whitelist-filter"          % "2.0.0",
    "uk.gov.hmrc"           %% "tax-year"                       % "0.5.0",
    "uk.gov.hmrc"           %% "play-partials"                  % "6.9.0-play-26",
    "com.typesafe.play"     %% "play-json-joda"                 % "2.7.1",
    "org.scalatra.scalate"  %% "play-scalate"                   % "0.5.0",
    "org.scalatra.scalate"  %% "scalate-core"                   % "1.9.1"
  )

  val test: Seq[ModuleID] = Seq(
    "org.scalatest"           %% "scalatest"            % "3.0.4",
    "org.scalatestplus.play"  %% "scalatestplus-play"   % "2.0.1",
    "org.pegdown"             %  "pegdown"              % "1.6.0",
    "org.jsoup"               %  "jsoup"                % "1.10.3",
    "com.typesafe.play"       %% "play-test"            % PlayVersion.current,
    "org.mockito"             %  "mockito-all"          % "1.10.19",
    "org.scalacheck"          %% "scalacheck"           % "1.13.4",
    "com.github.tomakehurst"  %  "wiremock-standalone"  % "2.19.0"
  ).map(_ % Test)

  def apply(): Seq[ModuleID] = compile ++ test
}
