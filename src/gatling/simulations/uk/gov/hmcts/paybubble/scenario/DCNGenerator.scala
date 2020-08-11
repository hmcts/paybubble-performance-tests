package uk.gov.hmcts.paybubble.scenario

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.paybubble.util.Environment

object DCNGenerator {

val bulkScanUrl=Environment.bulkScanURL


  val generateDCN = exec(http("TX040_PayBubble_BulkScanPayment")
    .post("/bulk-scan-payment")
    .header("ServiceAuthorization", "${s2sToken}")
    .header("Content-Type", "application/json")
    .body(StringBody(
      "{ \"amount\": ${amount}, \"bank_giro_credit_slip_number\": ${bankslipno}, \"banked_date\": \"${date}\", \"currency\": \"GBP\", \"document_control_number\": \"${dcn}\", \"method\": \"cash\"}"
    )
    ).asJson
    .check(status is 201))
      .pause(10)

    .exec(http("TX050_PayBubble_BulkScanPayment")
      .post("/bulk-scan-payments")
      .header("ServiceAuthorization", "${s2sToken}")
      .header("Content-Type", "application/json")
      .body(StringBody(
        "{ \"ccd_case_number\": \"${caseid}\", \"document_control_numbers\": [ \"${dcn}\" ], \"is_exception_record\": false, \"site_id\": \"AA07\"}"
      )).asJson
       .check(status is 201))
  .pause(10)

   /* .exec(http("TX060_PayBubble_BulkScanPayment")
      .put("/bulk-scan-payments?exception_reference=${exceptioncaseid}")
      .header("ServiceAuthorization", "${s2sToken}")
      .header("Content-Type", "application/json")
      .body(StringBody(
        "{ \"ccd_case_number\": \"${caseid}\"}"
      )
      ).asJson
       .check(status is 200))
  .pause(10)
*/
}
