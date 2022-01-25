package uk.gov.hmcts.paybubble.scenario

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.paybubble.util.Environment
import uk.gov.hmcts.paybubble.util.Environment.current_date

object PaymentTransactionAPI {

val paymentAPIURL=Environment.paymentAPIURL

  val getPaymentGroupReference=
    exec(http("PaymentAPI${service}_010_GetPaymentGrpRef")
         .post("/payment-groups")
         .header("Authorization", " ${accessToken}")
         .header("ServiceAuthorization", "${s2sToken}")
         .header("Content-Type", "application/json")
         .header("accept", "*/*")
         .body(StringBody(
           "{\n    \"fees\": [\n      {\n        \"calculated_amount\": 250,\n        \"code\": \"FEE3232\",\n        \"reference\": \"testRef\",\n        \"version\": \"1\",\n        \"volume\": 2\n      }\n    ]\n  }"
         )
         ).asJson
         .check(status is 201)
      .check(jsonPath("$.payment_group_reference").saveAs("paymentgroupref"))

    )
    .pause(10)

  val PBA = exec(http("PaymentAPI${service}_010_PayByAccounts")
    .post("/credit-account-payments")
    .header("Authorization", "${accessToken}")
    .header("ServiceAuthorization", "${s2sToken}")
    .header("Content-Type", "application/json")
    .header("accept", "*/*")
    .body(StringBody(
      "{\n  \"account_number\": \"PBA0082848\",\n  \"amount\": 218,\n  \"case_reference\": \"string\",\n  \"ccd_case_number\": \"${caseid}\",\n  \"currency\": \"GBP\",\n  \"customer_reference\": \"string\",\n  \"description\": \"string\",\n  \"case_type\":\"Divorce\",\n  \"fees\": [\n    {\n      \"calculated_amount\": 215,\n      \"code\": \"FEE0226\",\n      \"version\": \"1\",\n      \"volume\": 1\n    }\n  ],\n  \"organisation_name\": \"string\",\n  \"service\": \"Divorce\"\n}"
    )
    ).asJson
    .check(status is 201))
      .pause(10)

  val PBA_IAC = exec(http("PaymentAPI${service}_020_PayByAccountsIAC")
    .post("/credit-account-payments")
    .header("Authorization", " ${accessToken}")
    .header("ServiceAuthorization", "${s2sToken}")
    .header("Content-Type", "application/json")
    .header("accept", "*/*")
    .body(StringBody(
      "{\n  \"account_number\": \"PBA0082848\",\n  \"amount\": 2055,\n  \"case_reference\": \"string\",\n  \"ccd_case_number\": \"${case_id}\",\n  \"currency\": \"GBP\",\n  \"customer_reference\": \"string\",\n  \"description\": \"string\",\n  \"fees\": [\n    {\n       \n      \"calculated_amount\": 0,\n       \n      \"code\": \"FEE0313\",\n       \n      \"version\": 1,\n      \"volume\": 1\n    }\n  ],\n  \"organisation_name\": \"string\",\n  \"service\": \"IAC\",\n  \"site_id\": \"BFA1\"\n}"
    )
    ).asJson
    .check(status is 201))
    .pause(10)

  val reconciliationPayments = exec(http("PaymentAPI${service}_030_ReconciliationPayments")
    .get(s"/reconciliation-payments?end_date=${current_date}&start_date=${current_date}")
    .header("ServiceAuthorization", "${s2sToken}")
    .check(status is (200)))
    .pause(10)

  val onlinePayment = exec(http("PaymentAPI${service}_010_OnlinePayments")
                 .post("/card-payments")
                 .header("Authorization", "${accessToken}")
                 .header("ServiceAuthorization", "${s2sToken}")
                 .header("Content-Type", "application/json")
                  .header("accept", "*/*")
                  .header("return-url", "https://localhost")
                  //.header("service-callback-url", "http://div-cos-perftest.service.core-compute-perftest.internal/payment-update")
                //  .body(StringBody(
                //    "{\n  \"amount\": 0.01,\n  \"case_reference\": \"string\",\n  \"ccd_case_number\": \"string\",\n  \"currency\": \"GBP\",\n  \"description\": \"string\",\n  \"fees\": [\n    {\n      \"calculated_amount\": 0.01,\n      \"code\": \"string\",\n      \"version\": \"string\",\n      \"volume\": 1\n    }\n  ],\n  \"language\": \"string\",\n  \"service\": \"DIVORCE\",\n  \"site_id\": \"AA08\"\n}"
                //  )
                //  ).asJson
                .body(ElFileBody("PaymentPayload.json"))
                //  .check(status is 201)
                 )
            .pause(7)

      // .exec(session => {
      //   println("the case id is "+session("caseId").as[String])
      //   session
      // })

  val telephony = exec(http("PaymentAPI${service}_020_TelePayments")
                           .post("/payment-groups/${paymentgroupref}/telephony-card-payments")
                           .header("Authorization", "${accessToken}")
                           .header("ServiceAuthorization", "${s2sToken}")
                           .header("Content-Type", "application/json")
                            .header("accept", "*/*")
                           .header("return-url", "http://div-cos-perftest.service.core-compute-perftest.internal/payment-update")
                           .header("service-callback-url", "http://div-cos-perftest.service.core-compute-perftest.internal/payment-update")
                           .body(StringBody(
                             "{\n  \"amount\": 100,\n  \"case_type\": \"Divorce\",\n  \"ccd_case_number\": \"1600162727220636\",\n  \"currency\": \"GBP\",\n  \"return_url\": \"http://localhost\"\n}"
                           )
                           ).asJson
                           .check(status is 201))
                      .pause(10)

  val bulkscan = exec(http("PaymentAPI${service}_020_BulkScanPayments")
                      .post("/payment-groups/${paymentgroupref}/bulk-scan-payments-strategic")
                           .header("Authorization", "Bearer ${accessToken}")
                           .header("ServiceAuthorization", "${s2sToken}")
                           .header("Content-Type", "application/json")
                           .body(StringBody(
                             "{\n    \"amount\": 100,\n    \"banked_date\": \"2021-06-10T00:00:00.000+0000\",\n    \"ccd_case_number\": \"1111222233334444\",\n    \"exception_record\": null,\n    \"currency\": \"GBP\",\n    \"document_control_number\": \"${dcn_number}\",\n    \"external_provider\": \"exela\",\n    \"giro_slip_no\": \"10\",\n    \"payment_channel\": {\n        \"description\": \"\",\n        \"name\": \"bulk scan\"\n    },\n    \"payment_status\": {\n        \"description\": \"bulk scan payment completed\",\n        \"name\": \"success\"\n    },\n    \"payment_method\": \"CASH\",\n    \"case_type\": \"MoneyClaimCase\",\n    \"payment_allocation_dto\": {\n        \"reason\": \"Help with Fees (HWF) application declined\",\n        \"allocation_status\": \"Allocated\",\n        \"explanation\": \"I have put a stop on the case and contacted the applicant requesting the balance of payment\",\n        \"payment_allocation_status\": {\n            \"description\": \"\",\n            \"name\": \"Allocated\"\n        },\n        \"payment_group_reference\": \"2021-1611914066295\",\n        \"case_type\": \"MoneyClaimCase\",\n        \"user_name\": \"wwwwww\"\n    }\n}"
                           )
                           ).asJson
                           .check(status is 201)
    .check(jsonPath("$.reference").saveAs("paymentref"))
  )
                      .pause(10)


  val paymentAllocations = exec(http("PaymentAPI${service}_030_BulkScanPaymentAllocations")
                      .post("/payment-allocations")
                      .header("Authorization", " ${accessToken}")
                      .header("ServiceAuthorization", "${s2sToken}")
                      .header("Content-Type", "application/json")
                      .body(StringBody(
                        "{\n  \"payment_allocation_status\": {\n    \"description\": \"\",\n    \"name\": \"Allocated\"\n  },\n  \"payment_group_reference\": \"${paymentgroupref}\",\n  \"payment_reference\": \"${paymentref}\",\n  \"reason\": \"Incorrect payment received\",\n  \"explanation\": \"I have put a stop on the case.  The applicant needs to be contacted to request the balance of payment\",\n  \"user_name\": \"VIJAY TEST\"\n}\n"
                      )
                      ).asJson
                      .check(status is 201))
                 .pause(10)

  val getPaymentReferenceByCase = exec(http("PaymentAPI${service}_010_PayRefByCase")
                            .get("/cases/${caseid}/payments")
                            .header("Authorization", " ${accessToken}")
                            .header("ServiceAuthorization", "${s2sToken}")
                            .header("Content-Type", "application/json")
                            .header("accept", "*/*")
                            .check(status is 200)
    .check(jsonPath("$..payment_reference").saveAs("paymentreference"))

  )
                       .pause(10)

  val ccdViewPayment = exec(http("PaymentAPI${service}_020_CCDViewPayment")
                           .get("/payment-groups/fee-pay-apportion/${paymentreference}")
                           .header("Authorization", " ${accessToken}")
                           .header("ServiceAuthorization", "${s2sToken}")
                           .header("Content-Type", "application/json")
                           .header("accept", "*/*")
                           .check(status is 200))
                      .pause(10)

  val getPaymentGroupReferenceByCase = exec(http("PaymentAPI${service}_030_GetPaymentGroupReferenceByCase")
                .get("/cases/${caseid}/paymentgroups")
                .header("Authorization", " ${accessToken}")
                .header("ServiceAuthorization", "${s2sToken}")
                .header("Content-Type", "application/json")
                .header("accept", "*/*")
                .check(status is 200))
                .pause(10)

  val getPaymentByCase = exec(http("PaymentAPI${service}_040_GetPaymentByCase")
                .get("http://ccpay-bulkscanning-api-perftest.service.core-compute-perftest.internal/cases/${caseid}")
                .header("Authorization", "Bearer ${accessToken}")
                .header("ServiceAuthorization", "${s2sToken}")
                .header("Content-Type", "application/json")
                .header("accept", "*/*")
                .check(status is 200))
                .pause(10)

  val getPaymentByReference = exec(http("PaymentAPI_GetPaymentFeeDetails")
                .get("/payment-groups/${order_reference}")
                .header("Authorization", "Bearer ${accessToken}")
                .header("ServiceAuthorization", "${s2sToken}")
                .header("Content-Type", "application/json")
                .check(jsonPath("$.fees[0].id").saveAs("fee_id")))

  
}
