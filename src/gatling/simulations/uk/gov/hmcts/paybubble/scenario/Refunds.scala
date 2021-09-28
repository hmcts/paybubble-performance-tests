package uk.gov.hmcts.paybubble.scenario

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.paybubble.util._

import java.io.{BufferedWriter, FileWriter}

object Refunds {

  val submitRefund = exec(http("SubmitRefund")
    .post(Environment.refundsUrl + "/refund")
    .header("Authorization", "Bearer ${accessTokenRefund}")
    .header("ServiceAuthorization", "${s2sTokenRefund}")
    .header("Content-Type", "application/json")
    .body(ElFileBody("SubmitRefundRequest.json")))

  val getRefunds = exec(http("GetRefunds")
    .get(Environment.refundsUrl + "/refund?status=sent%20for%20approval")
    .header("Authorization", "Bearer ${accessTokenRefund}")
    .header("ServiceAuthorization", "${s2sTokenRefund}")
    .header("Content-Type", "application/json")
    .formParam("excludeCurrentUser", "false")
    .formParam("status", "sent for approval")
    //.body(ElFileBody("SubmitRefundRequest.json"))
    )

    .pause(5)

}