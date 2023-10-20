package utils

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils._

object  S2SHelper {

  val config: Config = ConfigFactory.load()

  // val getOTP =
  // exec(
  //   session => {
  //     val otp: String = String.valueOf(new GoogleAuthenticator().getTotpPassword(FUNCTIONAL_TEST_CLIENT_S2S_TOKEN))
  //     session.set("OTP", otp)

  //   })

  // val otpp="#{OTP}"

  val S2SAuthToken =

    exec(http("020_GetServiceToken")
      .post(Environment.S2S_BASE_URL + "/lease")
      .header("Content-Type", "application/json")
      .body(StringBody(
        """{
       "microservice": "ccpay_bubble"
        }"""
      )).asJson
      .check(bodyString.saveAs("s2sToken"))
      .check(bodyString.saveAs("responseBody")))
    .pause(10)
      /*.exec( session => {
        println("the code of id is "+session("s2sToken").as[String])
        session
      })*/

  val S2SPaymentsAuthToken =

    exec(http("020_GetServiceToken")
      .post(Environment.S2S_BASE_URL + "/lease")
      .header("Content-Type", "application/json")
      .body(StringBody(
        """{
       "microservice": "divorce_frontend"
        }"""
      )).asJson
      .check(bodyString.saveAs("s2sToken")))

  val RefundsS2SAuthToken =

    exec(http("PaymentAPIToken_020_GetServiceToken")
      .post(Environment.S2S_BASE_URL + "/lease")
      .header("Content-Type", "application/json")
      .body(StringBody(
        """{
       "microservice": "payment_app"
        }"""
      )).asJson
      .check(bodyString.saveAs("s2sTokenRefund"))
      )

  val CCDS2SToken = 

    exec(http("GetS2SToken")
      .post(Environment.S2S_BASE_URL + "/lease")
      .header("Content-Type", "application/json")
      .body(StringBody("""{"microservice":"ccd_data"}"""))
      .check(bodyString.saveAs("ccdS2SToken")))
      .exitHereIfFailed


}