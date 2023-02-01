package uk.gov.hmcts.paybubble.util

import com.warrenstrange.googleauth.GoogleAuthenticator
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.paybubble.util.Environment._

object  S2SHelper {

  val thinktime = 5

  val getOTP =
  exec(
    session => {
      val otp: String = String.valueOf(new GoogleAuthenticator().getTotpPassword(FUNCTIONAL_TEST_CLIENT_S2S_TOKEN))
      session.set("OTP", otp)

    })

  val otpp="${OTP}"

  val S2SAuthToken =

    exec(http("020_GetServiceToken")
      .post(S2S_BASE_URI + "/lease")
      .header("Content-Type", "application/json")
      .body(StringBody(
        s"""{
       "microservice": "${S2S_SERVICE_NAME}"
        }"""
      )).asJson
      .check(bodyString.saveAs("s2sToken"))
      .check(bodyString.saveAs("responseBody")))
    .pause(10)
      /*.exec( session => {
        println("the code of id is "+session("s2sToken").as[String])
        session
      })*/

  val RefundsS2SAuthToken =

    exec(http("PaymentAPIToken_020_GetServiceToken")
      .post(S2S_BASE_URI + "/lease")
      .header("Content-Type", "application/json")
      .body(StringBody(
        """{
       "microservice": "payment_app"
        }"""
      )).asJson
      .check(bodyString.saveAs("s2sTokenRefund"))
      // .check(bodyString.saveAs("responseBody"))
      )

    .pause(5)





}