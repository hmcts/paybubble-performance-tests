package uk.gov.hmcts.paybubble.scenario.util

import com.warrenstrange.googleauth.GoogleAuthenticator
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.paybubble.scenario.util._

object  S2SHelper {

  val getOTP =
  exec(
    session => {
      val otp: String = String.valueOf(new GoogleAuthenticator().getTotpPassword(Environment.FUNCTIONAL_TEST_CLIENT_S2S_TOKEN))
      session.set("OTP", otp)

    })

  val otpp="${OTP}"

  val S2SAuthToken =

    exec(session => {
        session.set("microservice", Environment.S2S_SERVICE_NAME)
      })

    .exec(http("PaymentAPI${service}_020_GetServiceToken")
      .post(Environment.S2S_BASE_URI+"/lease")
      .header("Content-Type", "application/json")
      .body(StringBody("{\"microservice\":\"${microservice}\"}"))//.asJson
      .check(bodyString.saveAs("s2sToken"))
      .check(bodyString.saveAs("responseBody")))
    .pause(Environment.thinkTime)
      /*.exec( session => {
        println("the code of id is "+session("s2sToken").as[String])
        session
      })*/
}