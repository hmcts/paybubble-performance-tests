package uk.gov.hmcts.paybubble.scenario

import java.io.{BufferedWriter, FileWriter}

import io.gatling.core.Predef._
import io.gatling.http.Predef._

object Ways2Pay {

  val AddOrder = exec(http("Ways2Pay_010_AddOrder")
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

  //This POSTs a new service Request
  val ServiceRequest = exec(http("Ways2Pay_030_ServiceRequestPOST")
    .post("/service-request")
    .header("Authorization", "${accessToken}")
    .header("ServiceAuthorization", "${s2sToken}")
    .header("Content-Type", "application/json")
    .header("accept", "*/*")
    .body(ElFileBody("ServiceRequest.json"))
    .check(jsonPath("$..service_request_reference").saveAs("service_request_reference"))
    .check(status is 201)
  )

  //On the PayNow Page this does a Get payments for a PBA account.  Currently we only have PBAFUNC12345 setup
  val W2PPBAPaymentsGET = exec(http("Ways2Pay_040_W2PPBAPaymentsGET")
    .get("/pba-accounts/PBAFUNC12345/payments")
    .header("Authorization", "${accessToken}")
    .header("ServiceAuthorization", "${s2sToken}")
    .header("Content-Type", "application/json")
    .header("accept", "*/*")
    .header("return-url", "https://localhost")
    .check(status is 200)
  )

  //
  val getPaymentGroupReferenceByCase = exec(http("Ways2Pay_050_PaymentGroupReferenceByCaseGET")
    .get("/cases/${caseid}/paymentgroups")
    .header("Authorization", " ${accessToken}")
    .header("ServiceAuthorization", "${s2sToken}")
    .header("Content-Type", "application/json")
    .header("accept", "*/*")
    .check(status is 200))

  val W2PCreditcardPayment = exec(http("Ways2Pay_060_W2PCreditCardPaymentPOST")
   // .post("/service-request/2022-1644834768896/card-payments") //serviceRequestRef
    .post("/service-request/${service_request_reference}/card-payments")
    .header("Authorization", "${accessToken}")
    .header("ServiceAuthorization", "${s2sToken}")
    .header("Content-Type", "application/json")
    .header("accept", "*/*")
    .header("return-url", "https://localhost")
    .body(ElFileBody("WaystoPayCCPayment.json"))
    .check(status is 201)
  )


/*
  val getPaymentGroupReferenceByCase = exec(http("Ways2Pay_050_PaymentGroupReferenceByCaseGET")
    .get("/cases/${caseid}/paymentgroups")
    .header("Authorization", " ${accessToken}")
    .header("ServiceAuthorization", "${s2sToken}")
    .header("Content-Type", "application/json")
//    .check(status is 200)
 // )*/

  val W2PPBAPaymentsPOST = exec(http("Ways2Pay_060_W2PPBAPaymentsPOST")
   // .post("/service-request/2022-1644853755337/pba-payments") //serviceRequestRef
    .post("/service-request/${service_request_reference}/pba-payments")
    .header("Authorization", "${accessToken}")
    .header("ServiceAuthorization", "${s2sToken}")
    .header("idempotency_key", "${UUID}")
    .header("Content-Type", "application/json")
    .header("accept", "*/*")
    .header("return-url", "https://localhost")
    .body(ElFileBody("WaystoPayPBAPayment.json"))
    .check(status is 201)
  )



  //Get card payment status by Internal Reference
  val W2PCardPaymentStatusGET = exec(http("Ways2Pay_070_W2PCardPaymentStatusGET")
    .get("/card-payments/${InternalRef}/status")//Internal Ref
    .header("Authorization", "${accessToken}")
    .header("ServiceAuthorization", "${s2sToken}")
    .header("Content-Type", "application/json")
    .header("accept", "*/*")
    .header("return-url", "https://localhost")
    .check(status is 200)
  )
}
