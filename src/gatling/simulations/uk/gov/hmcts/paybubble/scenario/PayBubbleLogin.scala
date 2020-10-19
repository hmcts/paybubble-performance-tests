package uk.gov.hmcts.paybubble.scenario

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.paybubble.scenario.check.CsrfCheck
import uk.gov.hmcts.paybubble.scenario.check.CsrfCheck.{csrfParameter, csrfTemplate}
import uk.gov.hmcts.paybubble.scenario.util._

object PayBubbleLogin {

  //val BaseURL = Environment.baseURL
  val idamUrl = Environment.idamURL
  val baseURL=Environment.baseURL
  //val loginFeeder = csv("OrgId.csv").circular
  val host="paybubble.perftest.platform.hmcts.net"


  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  //====================================================================================
  //Business process : Access Home Page by hitting the URL and relavant sub requests
  //below requests are Homepage and relavant sub requests as part of the login submission
  //=====================================================================================

  val homePage =


      exec(http("PayBubble_010_005_Homepage")
           .get("/")
        .headers(CommonHeader.headers_homepage)
             .check(CsrfCheck.save)
        .check(status.is(200))
        //.check(css("authorizeCommand", "action").saveAs("loginurl"))
          /* .check(css(".form-group>input[name='client_id']", "value").saveAs("clientId"))
           .check(css(".form-group>input[name='state']", "value").saveAs("state"))
           .check(css(".form-group>input[name='redirect_uri']", "value").saveAs("redirectUri"))*/
           /*.check(regex("""state="(.+?)"&amp;client_id="""").find(0).saveAs("stateid")))*/
       .check(regex("""class="form" action="(.+?)" method="post"""").find(0).saveAs("loginurl"))
      )

 //.replace(")", ""))
       // .check(css("#additional-evidence-form", "action").saveAs("uploadurl"))

   // .check(css("a:contains('forgotpassword')", "href").saveAs("computerURL")))


    .pause( MinThinkTime, MaxThinkTime )

   .exec( session => {
          println("csrf value "+session("csrf").as[String])
     println("login url  "+session("loginurl").as[String])
//     session.set("activationLink", (pattern findFirstMatchIn session("loginurl").get).mkString.trim.replace("amp;", ""))
          session
        })



  //==================================================================================
  //Business process : Enter the login details and submit
  //below requests are main login and relavant sub requests as part of the login submission
  //==================================================================================

  val login =
  exec(http("Login")
        .post(idamUrl + "${loginurl1}")
       .headers(CommonHeader.headers_login)
    .formParam(csrfParameter, csrfTemplate)
        .formParam("username", "ccdloadtest1@gmail.com")
        .formParam("password", "Password12")
        .formParam("save", "Sign in")
        .formParam("selfRegistrationEnabled", "false")

    .check(status.is(200))
    .check(regex("""<meta name="csrf-token" content=(.*?)","><title>""").find(0).saveAs("csrf1"))
  )


    .exec(http("request_37")
  .get("/api/payment-history/bulk-scan-feature")
  .headers(CommonHeader.headers_bulkscanfeature))

    .exec(http("request_38")
  .get("/api/payment-history/bulk-scan-feature")
  .headers(CommonHeader.headers_bulkscanfeature))

    .pause(MinThinkTime , MaxThinkTime)


  val logout =
  exec(http("request_106")
        .get("/logout")
        .headers(CommonHeader.headers_logout)
        .check(status.is(404)))


}