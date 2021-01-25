package uk.gov.hmcts.paybubble.scenario


import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.paybubble.util.Environment

object PaymentByDCN {

  //val BaseURL = Environment.baseURL
  val IdamUrl = Environment.idamURL
  val baseURL=Environment.baseURL
  //val loginFeeder = csv("OrgId.csv").circular

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime


  val headers_0 = Map(
    "accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
    "sec-fetch-dest" -> "document",
    "sec-fetch-mode" -> "navigate",
    "sec-fetch-site" -> "none",
    "sec-fetch-user" -> "?1",
    "upgrade-insecure-requests" -> "1")

  val headers_1 = Map(
    "accept" -> "*/*",
    "origin" -> baseURL,
    "sec-fetch-dest" -> "font",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin")

  val headers_3 = Map(
    "accept" -> "image/webp,image/apng,image/*,*/*;q=0.8",
    "sec-fetch-dest" -> "image",
    "sec-fetch-mode" -> "no-cors",
    "sec-fetch-site" -> "same-origin")

  val headers_4 = Map(
    "accept" -> "*/*",
    "if-modified-since" -> "Thu, 04 Jun 2020 23:38:14 GMT",
    "sec-fetch-dest" -> "script",
    "sec-fetch-mode" -> "no-cors",
    "sec-fetch-site" -> "cross-site")

  val headers_8 = Map(
    "accept" -> "image/webp,image/apng,image/*,*/*;q=0.8",
    "sec-fetch-dest" -> "image",
    "sec-fetch-mode" -> "no-cors",
    "sec-fetch-site" -> "cross-site")

  val headers_9 = Map(
    "csrf-token" -> "${csrf}",
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin",
    "x-requested-with" -> "XMLHttpRequest")

  val headers_14 = Map(
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin")

  val headers_20 = Map(
    "content-type" -> "application/json",
    "csrf-token" -> "${csrf}",
    "origin" -> baseURL,
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin",
    "x-requested-with" -> "XMLHttpRequest")

  val headers_27 = Map(
    "content-type" -> "text/plain",
    "csrf-token" -> "${csrf}",
    "origin" -> baseURL,
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin",
    "x-requested-with" -> "XMLHttpRequest")

  val searchByDCN =

  exec(http("request_39")
        .get("/api/bulk-scan/cases?document_control_number=${dcn}")
        .headers(headers_9)
      .check(status.in(200,304))
    )

    .exec(http("request_41")
  .get("/api/payment-history/cases/${caseid}/paymentgroups")
  .headers(headers_14)
  .check(status.is(404))
  )
    .exec(http("request_42")
  .get("/api/bulk-scan/cases?document_control_number=${dcn}")
  .headers(headers_14))
  .pause(5)

  val searchAndAddFee=

  //step2- click on add a new fee

    exec(http("request_28")
         .get( "/api/fees")
         .headers(headers_14))
    .pause(2)

    //step3- search for a fee, verify the parameter 2020-1591957263060
    .exec(http("request_31")
          .get( "/api/fees-jurisdictions/1")
          .headers(headers_14))

    .exec(http("request_32")
          .get("/api/fees-jurisdictions/2")
          .headers(headers_14))
    .pause(2)

    //step4-select the displayed fee from one of the fees
    .exec(http("request_33")
          .post("/api/payment-groups")
          .headers(headers_20)
          .body(ElFileBody("RecordedSimulationPBByDCNFeeMatch_0020_request.json")).asJson
      .check(jsonPath("$..payment_group_reference").optional.saveAs("payment-group-ref"))
    )


    .exec(http("request_35")
          .get("/api/payment-history/bulk-scan-feature")
          .headers(headers_9))

      .exec(http("request_24")
            .get("/api/bulk-scan/cases/${caseid}")
            .headers(headers_14))

    .exec(http("request_36")
          .get("/api/payment-history/payment/groups/${payment-group-ref}")
          .headers(headers_14))


  .pause(3)

    //below is the payment settlement transaction for equal amount as bulkscan
      val paymentProcess=
  exec(http("request_25")
    .get("/api/bulk-scan/cases?document_control_number=${dcn}")
        .headers(headers_14))
          .exec(http("request_26")
                   .get("/api/payment-history/cases/${caseid}/paymentgroup")
                   .headers(headers_14))
   .pause(9)

val PaymentProcessed=
  exec(http("request_44")
        .patch("/api/payment-history/bulk-scan-payments/${caseid}/status/PROCESSED")
        .headers(headers_27)
        .body(ElFileBody("RecordedSimulationPBByDCNFeeMatch_0027_request.json")).asJson
  )

    .exec(http("request_44")
                   .post("/api/payment-history/payment-groups/bulk-scan-payments")
                   .headers(headers_20)
                   .body(ElFileBody("RecordedSimulationPBByDCNFeeMatch_0028_request.json")).asJson
      .check(jsonPath("$..reference").optional.saveAs("payment-ref"))

    )

            .exec(http("request_46")
          .post("/api/payment-history/payment-allocations")
          .headers(headers_20)
          .body(ElFileBody("RecordedSimulationPayBubble_0029_request.json")).asJson)

    .exec(http("request_30")
  .get("/api/bulk-scan/cases/${caseid}")
  .headers(headers_14))

    .exec(http("request_31")
  .get("/api/payment-history/cases/${caseid}/paymentgroups")
  .headers(headers_14))
  .pause(7)


  //below is for surplus amount transaction












}
