package uk.gov.hmcts.paybubble.scenario

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.paybubble.util.Environment

object PaymentTransactionAPI {

val paymentAPIURL=Environment.paymentAPIURL

  val getPaymentGroupReference=
    exec(http("PaymentAPI${service}_030_GetPaymentGrpRef")
         .post("/payment-groups")
         .header("Authorization", " ${accessToken}")
         .header("ServiceAuthorization", "${s2sToken}")
         .header("Content-Type", "application/json")
         .header("accept", "*/*")
         .body(StringBody(
           "{\n  \"fees\": [\n    {\n      \"code\": \"FEE0007\",\n      \"version\": \"1\",\n      \"calculated_amount\": \"${amount}\",\n      \"memo_line\": \"RECEIPT OF FEES - Family enforcement other\",\n      \"natural_account_code\": \"4481102168\",\n      \"ccd_case_number\": \"${caseid}\",\n      \"jurisdiction1\": \"family\",\n      \"jurisdiction2\": \"family court\",\n      \"description\": \"Application for a charging order\",\n      \"volume\": 1,\n      \"fee_amount\": \"${amount}\"\n    }\n  ]\n}"
         )
         ).asJson
         .check(status is 201)
      .check(jsonPath("$.payment_group_reference").saveAs("paymentgroupref"))

    )
    .pause(50)




  val PBA = exec(http("PaymentAPI${service}_030_PayByAccounts")
    .post("/credit-account-payments")
    .header("Authorization", " ${accessToken}")
    .header("ServiceAuthorization", "${s2sToken}")
    .header("Content-Type", "application/json")
    .header("accept", "*/*")
    .body(StringBody(
      "{\n  \"account_number\": \"PBA0082848\",\n  \"amount\": 3,\n  \"case_reference\": \"string\",\n  \"ccd_case_number\": \"${caseid}\",\n  \"currency\": \"GBP\",\n  \"customer_reference\": \"string\",\n  \"description\": \"string\",\n  \"fees\": [\n    {\n       \n      \"calculated_amount\": 3,\n       \n      \"code\": \"FEE0289\",\n       \n      \"version\": 2,\n      \"volume\": 1\n    }\n  ],\n  \"organisation_name\": \"string\",\n  \"service\": \"FPL\",\n  \"site_id\": \"ABA3\"\n}"
    )
    ).asJson
    .check(status is 201))
      .pause(190)



  val onlinePayment = exec(http("PaymentAPI${service}_030_OnlinePayments")
                 .post("/card-payments")
                 .header("Authorization", " ${accessToken}")
                 .header("ServiceAuthorization", "${s2sToken}")
                 .header("Content-Type", "application/json")
                  .header("accept", "*/*")
                  .header("return-url", "http://div-cos-perftest.service.core-compute-perftest.internal/payment-update")
                  .header("service-callback-url", "http://div-cos-perftest.service.core-compute-perftest.internal/payment-update")
                 .body(StringBody(
                   "{\n  \"amount\": ${amount},\n  \"case_reference\": \"string\",\n  \"ccd_case_number\": \"${caseid}\",\n  \"currency\": \"GBP\",\n  \"description\": \"string\",\n  \"fees\": [\n    {\n      \"calculated_amount\": ${amount},\n      \"case_reference\": \"string\",\n      \"ccd_case_number\": \"${caseid}\",\n      \"code\": \"FEE3130\",\n      \"description\": \"string\",\n      \"fee_amount\": ${amount},\n      \"id\": 0,\n      \"jurisdiction1\": \"string\",\n      \"jurisdiction2\": \"string\",\n      \"memo_line\": \"string\",\n      \"natural_account_code\": \"string\",\n      \"net_amount\": ${amount},\n      \"reference\": \"string\",\n      \"version\": \"1\",\n      \"volume\": 1\n    }\n  ],\n  \"service\": \"CMC\",\n  \"site_id\": \"AA08\"\n}"
                 )
                 ).asJson
                 .check(status is 201))
            .pause(17)

  val telephony = exec(http("PaymentAPI${service}_040_TelePayments")
                           .post("/payment-groups/${paymentgroupref}/card-payments")
                           .header("Authorization", " ${accessToken}")
                           .header("ServiceAuthorization", "${s2sToken}")
                           .header("Content-Type", "application/json")
                            .header("accept", "*/*")
                           .header("return-url", "http://div-cos-perftest.service.core-compute-perftest.internal/payment-update")
                           .header("service-callback-url", "http://div-cos-perftest.service.core-compute-perftest.internal/payment-update")
                           .body(StringBody(
                             "{\n  \"currency\": \"GBP\",\n  \"description\": \"PayBubble payment\",\n  \"channel\": \"telephony\",\n  \"provider\": \"pci pal\",\n  \"ccd_case_number\": \"${caseid}\",\n  \"amount\": \"${amount}\",\n  \"service\": \"DIVORCE\",\n  \"site_id\": \"AA07\"\n}"
                           )
                           ).asJson
                           .check(status is 201))
                      .pause(100)

  val bulkscan = exec(http("PaymentAPI${service}_040_BulkScanPayments")
                      .post("/payment-groups/${paymentgroupref}/bulk-scan-payments")
                           .header("Authorization", " ${accessToken}")
                           .header("ServiceAuthorization", "${s2sToken}")
                           .header("Content-Type", "application/json")
                           .body(StringBody(
                             "{ \"amount\": ${amount}, \"banked_date\": \"${date}\", \"ccd_case_number\": \"${caseid}\", \"currency\": \"GBP\", \"document_control_number\": \"${dcn}\", \"external_provider\": \"exela\", \"giro_slip_no\": \"${bankslipno}\", \"payer_name\": \"vijay\", \"payment_channel\": { \"description\": \"bulk scan payment completed\", \"name\": \"bulk scan\" }, \"payment_method\": \"CASH\", \"payment_status\": { \"description\": \"HI THIS IS \", \"name\": \"success\" }, \"requestor\": \"PROBATE\", \"site_id\": \"AA07\"}"
                           )
                           ).asJson
                           .check(status is 201)
    .check(jsonPath("$.reference").saveAs("paymentref"))
  )
                      .pause(60)


  val paymentAllocations = exec(http("PaymentAPI${service}_050_BulkScanPaymentAllocations")
                      .post("/payment-allocations")
                      .header("Authorization", " ${accessToken}")
                      .header("ServiceAuthorization", "${s2sToken}")
                      .header("Content-Type", "application/json")
                      .body(StringBody(
                        "{\n  \"payment_allocation_status\": {\n    \"description\": \"\",\n    \"name\": \"Allocated\"\n  },\n  \"payment_group_reference\": \"${paymentgroupref}\",\n  \"payment_reference\": \"${paymentref}\",\n  \"reason\": \"Incorrect payment received\",\n  \"explanation\": \"I have put a stop on the case.  The applicant needs to be contacted to request the balance of payment\",\n  \"user_name\": \"VIJAY TEST\"\n}\n"
                      )
                      ).asJson
                      .check(status is 201))
                 .pause(50)

  val getPaymentReferenceByCase = exec(http("PaymentAPI${service}_030_PayRefByCase")
                            .get("/cases/${caseid}/payments")
                            .header("Authorization", " ${accessToken}")
                            .header("ServiceAuthorization", "${s2sToken}")
                            .header("Content-Type", "application/json")
                            .header("accept", "*/*")
                            .check(status in (200,201))
    .check(jsonPath("$..payment_reference").saveAs("paymentreference"))

  )
                       .pause(3)

  val getPaymentGroupReferenceByCase = exec(http("PaymentAPI${service}_040_CCDViewPayment")
                                 .get("/cases/${caseid}/paymentgroups")
                                 .header("Authorization", " ${accessToken}")
                                 .header("ServiceAuthorization", "${s2sToken}")
                                 .header("Content-Type", "application/json")
                                 .header("accept", "*/*")
                                 .check(status is 201))
                            .pause(10)

  val ccdViewPayment = exec(http("PaymentAPI${service}_040_CCDViewPayment")
                           .get("/payment-groups/fee-pay-apportion/${paymentreference}")
                           .header("Authorization", " ${accessToken}")
                           .header("ServiceAuthorization", "${s2sToken}")
                           .header("Content-Type", "application/json")
                            .header("accept", "*/*")
                           .check(status in (200,201)))
                      .pause(10)
}
