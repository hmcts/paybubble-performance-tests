package uk.gov.hmcts.paybubble.scenario

import java.io.{BufferedWriter, FileWriter}

import io.gatling.core.Predef._
import io.gatling.http.Predef._

object Ways2Pay {

  //This POST's a new service Request for W2P
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

  //Get payment groups for a case by CaseID
  val getPaymentGroupReferenceByCase = exec(http("Ways2Pay_050_PaymentGroupReferenceByCaseGET")
    .get("/cases/${caseid}/paymentgroups")
    .header("Authorization", " ${accessToken}")
    .header("ServiceAuthorization", "${s2sToken}")
    .header("Content-Type", "application/json")
    .header("accept", "*/*")
    .check(status is 200))


  //creates an online card payment against the Service Request which was raised in the ServiceRequest request earlier
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

  ////creates credit account payment via PBA  against the Service Request which was raised in the ServiceRequest request earlier
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



  //Get card payment status by Internal Reference for previous payment.  This uses data stored in InternalRef.csv
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
