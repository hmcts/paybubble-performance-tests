package uk.gov.hmcts.paybubble.scenario

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.paybubble.scenario.check.CsrfCheck
import uk.gov.hmcts.paybubble.scenario.check.CsrfCheck.{csrfParameter, csrfTemplate}
import uk.gov.hmcts.paybubble.util.{CommonHeader, Environment}
import java.lang._


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

  exec(flushCookieJar)
    .exec(flushHttpCache)

    .group("PaymentAPI${service}_010_Homepage"){
    exec(http("PaymentAPI${service}_010_Homepage")
           .get("/")
        .headers(CommonHeader.headers_homepage)
             .check(CsrfCheck.save)
        .check(status.is(200))
        //.check(css("authorizeCommand", "action").saveAs("loginurl"))
          /* .check(css(".form-group>input[name='client_id']", "value").saveAs("clientId"))
           .check(css(".form-group>input[name='state']", "value").saveAs("state"))
           .check(css(".form-group>input[name='redirect_uri']", "value").saveAs("redirectUri"))*/
           /*.check(regex("""state="(.+?)"&amp;client_id="""").find(0).saveAs("stateid")))*/
       .check(regex("""class="form" action="(.+?)" method="post"""").find(0).transform(str => str.replace("&amp;", "&")).saveAs("loginurl"))
      )

 //.replace(")", ""))
       // .check(css("#additional-evidence-form", "action").saveAs("uploadurl"))

   // .check(css("a:contains('forgotpassword')", "href").saveAs("computerURL")))


    .pause( MinThinkTime, MaxThinkTime )}

   .exec( session => {
          println("csrf value "+session("csrf").as[String])
     println("login url  "+session("loginurl").as[String])
//     session.set("activationLink", (pattern findFirstMatchIn session("loginurl").get).mkString.trim.replace("amp;", ""))
          session
        })



  //==================================================================================
  //Business process : Enter the login details and submit
  //below requests are main login and relevant sub requests as part of the login submission
  //==================================================================================

  val login =
  group("PaymentAPI${service}_020_Login1"){
  exec(http("PaymentAPI${service}_020_Login1")
        .post(idamUrl + "${loginurl}")
       .headers(CommonHeader.headers_login)
    .formParam(csrfParameter, csrfTemplate)
        .formParam("username", "ccdloadtest1@gmail.com")
        .formParam("password", "Password12")
        .formParam("save", "Sign in")
        .formParam("selfRegistrationEnabled", "false")
        .formParam("_csrf", "${csrf}")
    .check(status.is(200))
    .check(regex("""<meta name="csrf-token" content="(.*)"><title>""").saveAs("csrf")))
  }

    .group("PaymentAPI${service}_030_Login2"){
    exec(http("PaymentAPI${service}_030_Login2")
      .get("/api/payment-history/LD-feature?flag=apportion-feature")
    .headers(CommonHeader.headers_bulkscanfeature))}

    .group("PaymentAPI${service}_040_Login3"){
      exec(http("PaymentAPI${service}_040_Login3")
    .get("/api/payment-history/LD-feature?flag=FE-pcipal-old-feature")
        .headers(CommonHeader.headers_bulkscanfeature))}

    .group("PaymentAPI${service}_050_Login4"){
      exec(http("PaymentAPI${service}_050_Login4")
    .get("/api/payment-history/LD-feature?flag=FE-pcipal-antenna-feature")
    .headers(CommonHeader.headers_bulkscanfeature))}

    .group("PaymentAPI${service}_060_Login5"){
      exec(http("PaymentAPI${service}_060_Login5")
    .get("/api/payment-history/LD-feature?flag=bspayments-strategic")
    .headers(CommonHeader.headers_bulkscanfeature))}

    .group("PaymentAPI${service}_070_Login6"){
      exec(http("PaymentAPI${service}_070_Login6")
  .get("/api/payment-history/bulk-scan-feature")
  .headers(CommonHeader.headers_bulkscanfeature))}

    .group("PaymentAPI${service}_080_Login7"){
      exec(http("PaymentAPI${service}_080_Login7")
  .get("/api/payment-history/bulk-scan-feature")
  .headers(CommonHeader.headers_bulkscanfeature)
      //.check(headerRegex("Set-Cookie","__auth-token=(.*)").saveAs("authToken"))
    )}

    .pause(MinThinkTime , MaxThinkTime)


  val logout =
  group("PaymentAPI${service}_${SignoutNumber}_Logout"){
    exec(http("PaymentAPI${service}_${SignoutNumber}_Logout")
        .get("/logout")
        .headers(CommonHeader.headers_logout)
        .check(status.is(404)))}


}