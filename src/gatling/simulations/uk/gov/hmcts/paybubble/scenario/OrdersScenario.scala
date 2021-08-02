package uk.gov.hmcts.paybubble.scenario

import io.gatling.core.Predef._
import io.gatling.http.Predef._

import java.io.{BufferedWriter, FileWriter}

object OrdersScenario {

  val AddOrder = exec(http("PaymentAPI${service}_010_AddOrder")
    .post("/order")
    .header("Authorization", "${accessToken}")
    .header("ServiceAuthorization", "${s2sToken}")
    .header("Content-Type", "application/json")
    .header("accept", "*/*")
    .body(StringBody(
      "{\n  \"ccd_case_number\": \"${case_number}\",\n  \"case_reference\": \"string\",\n  \"case_type\": \"FinancialRemedyMVP2\",\n  \"fees\": [\n    {\n      \"calculated_amount\": 50.25,\n      \"code\": \"FEE0226\",\n      \"version\": \"1\",\n      \"volume\": 1\n    }\n  ]\n}"
    )
    ).asJson
    .check(status is 201))
    /*.check(jsonPath("$..order_reference").saveAs("orderRef")))
    .exec {session =>
      val fw = new BufferedWriter(new FileWriter("src/gatling/resources/order_references.csv", true))
      try {
        fw.write(session("orderRef").as[String]+"\r\n")
      }
      finally fw.close()
      session
    }*/
    .pause(10)

  val CreatePayment = exec(http("PaymentAPI${service}_020_CreatePayment")
    .post("/order/${order_reference}/credit-account-payment")
    .header("Authorization", " ${accessToken}")
    .header("ServiceAuthorization", "${s2sToken}")
    .header("idempotency_key", "${UUID}")
    .header("order-reference", "${order_reference}")
    .header("Content-Type", "application/json")
    .header("accept", "*/*")
    .body(StringBody(
      "{\n  \"account_number\": \"PBA0082848\",\n  \"amount\": 50.25,\n  \"currency\": \"GBP\",\n  \"customer_reference\": \"string\"\n}"
    )
    ).asJson
    .check(status is 201))
    .pause(10)

  val GetOrder = exec(http("PaymentAPI${service}_030_GetOrder")
    .get("/case-payment-orders?case_ids=${CPO_case_id}")
    .header("Authorization", "Bearer ${accessToken}")
    .header("ServiceAuthorization", "${s2sToken}")
    .header("accept", "*/*")
    .check(status is 200))
    .pause(10)

}
