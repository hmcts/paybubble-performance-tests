package uk.gov.hmcts.paybubble.util

import io.gatling.core.Predef._
import io.gatling.http.Predef._

object IDAMHelper {

 /* private val USERNAME = "testytesttest@test.net"
  private val PASSWORD = "4590fgvhbfgbDdffm3lk4j"*/
// private val USERNAME = "james@swansea.gov.uk"
//  private val PASSWORD = "Password123"
  private val USERNAME = "ccdloadtest1@gmail.com"
  private val PASSWORD = "Password12"
// private val USERNAME = "emshowcase@hmcts.net"
 // private val PASSWORD = "4590fgvhbfgbDdffm3lk4j"
  // below are for aat
/*private val USERNAME = "bundle-tester--518511189@gmail.com"
  private val PASSWORD = "4590fgvhbfgbDdffm3lk4j"*/


  val thinktime = Environment.thinkTime



  val getIdamToken = exec(http("PaymentAPI${service}_010_005_GetAuthCookie")
                       .post(Env.getIdamUrl + "/authenticate")
                       .header("Content-Type", "application/x-www-form-urlencoded")
                        .formParam("originIp", "0:0:0:0:0:0:0:1")
                        .formParam("redirectUri", Env.getOAuthRedirect())
                       .formParam("username", USERNAME)
                       .formParam("password", PASSWORD)
                       .check(status is 200)
                       .check(headerRegex("Set-Cookie", "Idam.Session=(.*)").saveAs("authCookie")))

                  .doIf(session => session.contains("authCookie")) {
                    exec(http("PaymentAPI${service}_010_010_GetServiceAuthToken")
                         .post(Env.getIdamUrl  + "/o/authorize?response_type=code&client_id="+Env.getOAuthClient()+"&redirect_uri="+Env.getOAuthRedirect()+"&scope=openid").disableFollowRedirect
                         .header("Content-Type", "application/x-www-form-urlencoded")
                         .header("Cookie", "Idam.Session=${authCookie}")
                         .header("Content-Length", "0")
                         .check(status is 302)
                         .check(headerRegex("Location", "(?<=code=)(.*)(?=&client_id)").saveAs("serviceauthcode")))
                      .pause(10)
                  }

    .doIf(session => session.contains("serviceauthcode")) {
    exec(http("PaymentAPI${service}_010_015_GetAuthToken")
         .post(Env.getIdamUrl  + "/o/token?grant_type=authorization_code&code=${serviceauthcode}&client_id="+Env.getOAuthClient()+"&redirect_uri="+Env.getOAuthRedirect()+"&client_secret="+Env.getOAuthSecret())
         .header("Content-Type", "application/x-www-form-urlencoded")
         .header("Content-Length", "0")
         .check(status is 200)
         .check(jsonPath("$.access_token").saveAs("accessToken")))
  }
    .pause(10)

  //following is a tested method

  val getIdamTokenLatest=
    exec(http("PaymentAPI${service}_010_015_GetAuthToken")
         .post(Env.getIdamUrl  + "/o/token?client_id="+Env.getOAuthClient()+"&client_secret="+Env.getOAuthSecret()+"grant_type=password&scope=search-user&username=befta.caseworker.2.solicitor.2@gmail.com&password=PesZvqrb78")
         .header("Content-Type", "application/x-www-form-urlencoded")
         .header("Content-Length", "0")
         .check(status is 200)
         .check(jsonPath("$.access_token").saveAs("accessToken")))



}
