package uk.gov.hmcts.paybubble.scenario.util

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.paybubble.scenario.util._

object IDAMHelper {

  //private val USERNAME = "ccdloadtest1@gmail.com"
  //private val PASSWORD = "Password12"
  // private val USERNAME = "emshowcase@hmcts.net"
  // private val PASSWORD = "4590fgvhbfgbDdffm3lk4j"
  // below are for aat
  private val USERNAME = "bundle-tester--518511189@gmail.com"
  private val PASSWORD = "4590fgvhbfgbDdffm3lk4j"

  val thinktime = Environment.thinkTime

  val getIdamToken = 
    exec(http("PaymentAPI${service}_010_005_GetAuthCookie")
      .post(Environment.IDAM_API_BASE_URI + "/authenticate")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .formParam("originIp", "0:0:0:0:0:0:0:1")
      .formParam("redirectUri", Environment.IDAM_AUTH_REDIRECT)
      .formParam("username", USERNAME)
      .formParam("password", PASSWORD)
      .check(status is 200)
      .check(headerRegex("Set-Cookie", "Idam.Session=(.*)").saveAs("authCookie")))

    .doIf(session => session.contains("authCookie")) {
      exec(http("PaymentAPI${service}_010_010_GetServiceAuthToken")
        .post(Environment.IDAM_API_BASE_URI  + "/o/authorize?response_type=code&client_id="+Environment.OAUTH_CLIENT+"&redirect_uri="+Environment.IDAM_AUTH_REDIRECT+"&scope=openid").disableFollowRedirect
        .header("Content-Type", "application/x-www-form-urlencoded")
        .header("Cookie", "Idam.Session=${authCookie}")
        .header("Content-Length", "0")
        .check(status is 302)
        .check(headerRegex("Location", "(?<=code=)(.*)(?=&client_id)").saveAs("serviceauthcode")))
        .pause(thinktime)
    }

    .doIf(session => session.contains("serviceauthcode")) {
      exec(http("PaymentAPI${service}_010_015_GetAuthToken")
        .post(Environment.IDAM_API_BASE_URI  + "/o/token?grant_type=authorization_code&code=${serviceauthcode}&client_id="+Environment.OAUTH_CLIENT+"&redirect_uri="+Environment.IDAM_AUTH_REDIRECT+"&client_secret="+Environment.IDAM_OAUTH_SECRET)
        .header("Content-Type", "application/x-www-form-urlencoded")
        .header("Content-Length", "0")
        .check(status is 200)
        .check(jsonPath("$.access_token").saveAs("accessToken")))
    }

  .pause(thinktime)

  //following is a tested method

  val getIdamTokenLatest=
    exec(http("PaymentAPI${service}_010_015_GetAuthToken")
      .post(Environment.IDAM_API_BASE_URI + "/o/token?client_id=" + Environment.OAUTH_CLIENT + "&client_secret=" + Environment.IDAM_OAUTH_SECRET + "&grant_type=password&scope=openid&username=kishanki@gmail.com&password=LevelAt12")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Content-Length", "0")
      .check(status is 200)
      .check(jsonPath("$.access_token").saveAs("accessToken")))
}
