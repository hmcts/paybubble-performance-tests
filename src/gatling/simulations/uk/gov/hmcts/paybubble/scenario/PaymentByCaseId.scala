package uk.gov.hmcts.paybubble.scenario

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.paybubble.util.Environment

object PaymentByCaseId {

  val headers_39 = Map(
    "accept" -> "application/json, text/plain, */*",
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-US,en;q=0.9",
    "csrf-token" -> "3E6jANZr-RH9MsxOq_l9p-erjlxVAXnFYKdQ",
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin",
    "x-requested-with" -> "XMLHttpRequest")

  val headers_43 = Map(
    "accept" -> "application/json, text/plain, */*",
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-US,en;q=0.9",
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin")

  val headers_49 = Map(
    "accept" -> "application/json, text/plain, */*",
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-US,en;q=0.9",
    "content-type" -> "application/json",
    "csrf-token" -> "3E6jANZr-RH9MsxOq_l9p-erjlxVAXnFYKdQ",
    "origin" -> "https://paybubble.perftest.platform.hmcts.net",
    "sec-fetch-dest" -> "empty",
    "sec-fetch-mode" -> "cors",
    "sec-fetch-site" -> "same-origin",
    "x-requested-with" -> "XMLHttpRequest")

  val headers_58 = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Origin" -> "https://paybubble.perftest.platform.hmcts.net",
    "Sec-Fetch-Dest" -> "document",
    "Sec-Fetch-Mode" -> "navigate",
    "Sec-Fetch-Site" -> "cross-site",
    "Sec-Fetch-User" -> "?1",
    "Upgrade-Insecure-Requests" -> "1")

  val headers_59 = Map("Origin" -> "https://ip3cloud.com")

  val headers_61 = Map(
    "Accept" -> "image/webp,image/apng,image/*,*/*;q=0.8",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Sec-Fetch-Dest" -> "image",
    "Sec-Fetch-Mode" -> "no-cors",
    "Sec-Fetch-Site" -> "same-origin")

  val headers_62 = Map(
    "Accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Sec-Fetch-Dest" -> "iframe",
    "Sec-Fetch-Mode" -> "navigate",
    "Sec-Fetch-Site" -> "cross-site",
    "Upgrade-Insecure-Requests" -> "1")

  val headers_63 = Map(
    "Accept" -> "text/css,*/*;q=0.1",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Sec-Fetch-Dest" -> "style",
    "Sec-Fetch-Mode" -> "no-cors",
    "Sec-Fetch-Site" -> "cross-site")

  val headers_64 = Map(
    "Accept" -> "text/css,*/*;q=0.1",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Sec-Fetch-Dest" -> "style",
    "Sec-Fetch-Mode" -> "no-cors",
    "Sec-Fetch-Site" -> "same-origin")

  val headers_67 = Map(
    "Accept" -> "*/*",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Sec-Fetch-Dest" -> "script",
    "Sec-Fetch-Mode" -> "no-cors",
    "Sec-Fetch-Site" -> "same-origin")

  val headers_108 = Map(
    "Accept" -> "*/*",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Sec-Fetch-Dest" -> "empty",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "same-origin")

  val headers_114 = Map(
    "Accept" -> "application/json, text/javascript, */*; q=0.01",
    "Accept-Encoding" -> "gzip, deflate, br",
    "Accept-Language" -> "en-US,en;q=0.9",
    "Content-Type" -> "application/json; charset=utf-8",
    "Sec-Fetch-Dest" -> "empty",
    "Sec-Fetch-Mode" -> "cors",
    "Sec-Fetch-Site" -> "same-origin",
    "X-Requested-With" -> "XMLHttpRequest")


  //val BaseURL = Environment.baseURL
  val IdamUrl = Environment.idamURL
  val baseURL=Environment.baseURL
  //val loginFeeder = csv("OrgId.csv").circular

  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime



  //after login, search by caseid following requests

  //https://paybubble.perftest.platform.hmcts.net/api/cases/1591283474149643

  val searchbycase=
    exec(http("request_28")
  .get( "/api/cases/1591283454735452")
      .headers(headers_39))

    .exec(http("request_28")
        .get( "/api/bulk-scan/cases/1591283472573323")
        .headers(headers_43))

    .exec(http("request_25")
        .get("/api/cases/1591283472573323")
        .headers(headers_43))
    .exec(http("request_27")
  .get( "/api/payment-history/cases/1591283472573323/paymentgroups")
  .headers(headers_43)
  .check(status.is(404)))

  .pause(3)

    val searchAndAddFee=

  //step2- click on add a new fee

    exec(http("request_28")
    .get( "/api/fees")
    .headers(headers_43))
  .pause(2)


    //step3- search for a fee, verify the parameter 2020-1591957263060
  .exec(http("request_31")
        .get( "/api/fees-jurisdictions/1")
        .headers(headers_43))

         .exec(http("request_32")
                   .get("/api/fees-jurisdictions/2")
                   .headers(headers_43))
  .pause(2)

  //step4-select the displayed fee from one of the fees
  .exec(http("request_33")
        .post("/api/payment-groups")
        .headers(headers_49)
        .body(RawFileBody("RecordedSimulationpaybubbletele_0049_request.txt")))

    .exec(http("request_35")
  .get("/api/payment-history/bulk-scan-feature")
  .headers(headers_39))

    .exec(http("request_36")
  .get("/api/payment-history/payment-groups/2020-1596027090043")
  .headers(headers_43))
  .pause(3)


  //step5- select either divorce or probate
val cardPayments=
  exec(http("request_53")
        .post("/api/payment-history/payment-groups/2020-1596027090043/card-payments")
        .headers(headers_49)
        .body(RawFileBody("RecordedSimulationPBByCase_0053_request.txt"))
       )
  .pause(12)

    .exec(http("request_58")
          .post("/clients/hmcts/payments/launch.php")
          .headers(headers_58)
          .formParam("orderReference", "RC-1596-0270-9670-2924")
          .formParam("orderAmount", "215.00")
          .formParam("cardHolderName", "Vijay Vykuntam")
          .formParam("billingAddress1", "4, Hibernia Gardens")
          .formParam("billingTown", "Hounslow")
          .formParam("billingPostcode", "TW3 3SD")
          .formParam("ppAccountID", "1209")
          .formParam("renderMethod", "HTML")
          .formParam("apiKey", "3ecfe793e7153715345a0ec4fe536699")
          .formParam("callbackURL", "https://ip3cloud.com/clients/hmcts/payments/data/callback.php")
          .formParam("callbackURLTwo", "https://core-api-mgmt-perftest.azure-api.net/telephony-api/telephony/callback")
          .formParam("redirectURL", "")
          .formParam("hosted", "true")
          .formParam("transactionType", "SAL")
    )

  //https://paybubble.perftest.platform.hmcts.net/api/payment-history/payment-groups/2020-1591957263060/card-payments
  //pause

  //step6- enter the address and pay
  //





}
