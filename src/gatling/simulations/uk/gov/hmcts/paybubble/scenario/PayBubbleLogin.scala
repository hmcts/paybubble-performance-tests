package uk.gov.hmcts.paybubble.scenario

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.paybubble.scenario.check.CsrfCheck
import uk.gov.hmcts.paybubble.scenario.check.CsrfCheck.{csrfParameter, csrfTemplate}
import uk.gov.hmcts.paybubble.util.{CommonHeader, Environment}

object PayBubbleLogin {

  val idamUrl = Environment.idamURL
  val baseURL=Environment.baseURL
  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  //====================================================================================
  //Business process : Access Home Page by hitting the URL and relevant sub requests
  //below requests are Homepage and relevant sub requests as part of the login submission
  //=====================================================================================

  val homePage =
  exec(flushCookieJar)
  .exec(flushHttpCache)

  .group("PaymentAPI${service}_010_Homepage"){
    exec(http("PaymentAPI${service}_010_010_Homepage")
    .get("/")
      .headers(CommonHeader.headers_homepage)
      .check(CsrfCheck.save)
      .check(status.is(200))
      .check(regex("""class="form" action="(.+?)" method="post"""").find(0).transform(str => str.replace("&amp;", "&")).saveAs("loginurl")))
  }

  .pause(MinThinkTime, MaxThinkTime)

  //==================================================================================
  //Business process : Enter the login details and submit
  //below requests are main login and relevant sub requests as part of the login submission
  //==================================================================================

  val login =
  group("PaymentAPI${service}_020_Login"){
    exec(http("PaymentAPI${service}_020_010_Login1")
    .post(idamUrl + "${loginurl}")
      .headers(CommonHeader.headers_login)
      .formParam(csrfParameter, csrfTemplate)
      .formParam("username", "${username}")
      .formParam("password", "${password}")
      .formParam("save", "Sign in")
      .formParam("selfRegistrationEnabled", "false")
      .formParam("_csrf", "${csrf}")
      .check(status.is(200))
      .check(regex("""<meta name="csrf-token" content="(.*)"><title>""").saveAs("csrf")))

    .exec(http("PaymentAPI${service}_020_020_Login2")
    .get("/api/payment-history/bulk-scan-feature")
      .headers(CommonHeader.headers_bulkscanfeature))
  }

    .exitHereIfFailed
    .pause(MinThinkTime , MaxThinkTime)


  //==================================================================================
  //Business process : Log out of Paybubble
  //==================================================================================

  val logout =
  group("PaymentAPI${service}_${SignoutNumber}_Logout"){
    exec(http("PaymentAPI${service}_${SignoutNumber}_010_Logout")
    .get("/logout")
      .headers(CommonHeader.headers_logout)
      .check(regex("This page cannot be found"))
      .check(status.is(404)))
  }

}