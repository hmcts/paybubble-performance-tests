package uk.gov.hmcts.paybubble.scenario

import java.io.{BufferedWriter, FileWriter}

import io.gatling.core.Predef._
import io.gatling.http.Predef._

object Ways2Pay {

  val AddOrder = exec(http("PaymentAPI${service}_010_AddOrder")
    .post("/order")
    .header("Authorization", "${accessToken}")
    .header("ServiceAuthorization", "${s2sToken}")
    .header("Content-Type", "application/json")
    .header("accept", "*/*")
    .body(StringBody(
      "{\n  \"ccd_case_number\": \"${case_number}\",\n  \"case_reference\": \"string\",\n  \"case_type\": \"FinancialRemedyMVP2\",\n  \"fees\": [\n    {\n      \"calculated_amount\": 50.25,\n      \"code\": \"FEE0226\",\n      \"version\": \"1\",\n      \"volume\": 1\n    }\n  ]\n}"
    ) //${case_number}
    ).asJson
    .check(status is 201)
    .check(jsonPath("$..order_reference").saveAs("order_reference"))
     .check(jsonPath("$..order_reference").saveAs("orderRef"))
     )
     .exec {session =>
       val fw = new BufferedWriter(new FileWriter("src/gatling/resources/order_references.csv", true))
       try {
         fw.write(session("orderRef").as[String]+"\r\n")
       }
       finally fw.close()
       session
     }
    .pause(10)


  val ServiceRequest = exec(http("PaymentAPI${service}_010_ServiceRequest")
    .post("/service-request")
    .header("Authorization", "${accessToken}")
    .header("ServiceAuthorization", "${s2sToken}")
    .header("Content-Type", "application/json")
    .header("accept", "*/*")
    .body(ElFileBody("ServiceRequest.json"))
    .check(status is 201)
  )




}
