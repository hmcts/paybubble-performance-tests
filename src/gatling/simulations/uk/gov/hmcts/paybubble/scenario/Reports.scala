package uk.gov.hmcts.paybubble.scenario

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.paybubble.util.{CommonHeader, Environment}


object Reports {

  //val BaseURL = Environment.baseURL
  val idamUrl = Environment.idamURL
  val baseURL=Environment.baseURL
  //val loginFeeder = csv("OrgId.csv").circular


  val MinThinkTime = Environment.minThinkTime
  val MaxThinkTime = Environment.maxThinkTime

  //====================================================================================
  //Business process : Access Home Page by hitting the URL and relavant sub requests
  //below requests are Homepage and relavant sub requests as part of the login submission
  //=====================================================================================

  val viewreport =


      exec(http("PayBubble_010_005_ViewReport")
           .get("/payment-history/view?view=reports")
        .headers(CommonHeader.headers_viewreports)
        .check(status.is(200)))

    .exec(http("request_48")
  .get("/api/payment-history/bulk-scan-feature")
  .headers(CommonHeader.headers_reports)
        .check(status.is(200)))
    .pause( MinThinkTime, MaxThinkTime )

  //==================================================================================
  //Business process : Enter the login details and submit
  //below requests are main login and relavant sub requests as part of the login submission
  //==================================================================================

  val report_dataloss =
    exec(http("request_37")
  .get("/api/bulk-scan/report/data?date_from=07/01/2020&date_to=07/26/2020&report_type=DATA_LOSS")
  .headers(CommonHeader.headers_reports)
      .check(status.is(200)))
      .pause( MinThinkTime, MaxThinkTime )

  val report_unprocessed =
    exec(http("request_37")
         .get("/api/bulk-scan/report/data?date_from=07/01/2020&date_to=07/26/2020&report_type=UNPROCESSED")
         .headers(CommonHeader.headers_reports)
         .check(status.is(200)))
    .pause( MinThinkTime, MaxThinkTime )

  val report_unallocated =
    exec(http("request_37")
         .get("/api/payment-history/report/data?date_from=07/01/2020&date_to=07/26/2020&report_type=PROCESSED_UNALLOCATED")
         .headers(CommonHeader.headers_reports)
         .check(status.is(200)))
    .pause( MinThinkTime, MaxThinkTime )
  val report_SurplusandShortfall =
    exec(http("request_37")
         .get("/api/payment-history/report/data?date_from=07/01/2020&date_to=07/05/2020&report_type=SURPLUS_AND_SHORTFALL")
         .headers(CommonHeader.headers_reports)
         .check(status.is(200)))
    .pause( MinThinkTime, MaxThinkTime )


}