package uk.gov.hmcts.paybubble.scenario


import scala.concurrent.duration._
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import uk.gov.hmcts.paybubble.util.Environment._

object PCIPALRecording extends Simulation {

	val headers_11 = Map(
		"accept" -> "application/json, text/plain, */*",
		"csrf-token" -> "${csrf}",
		"sec-ch-ua" -> """Google Chrome";v="87", " Not;A Brand";v="99", "Chromium";v="87""",
		"sec-ch-ua-mobile" -> "?0",
		"sec-fetch-dest" -> "empty",
		"sec-fetch-mode" -> "cors",
		"sec-fetch-site" -> "same-origin",
		"x-requested-with" -> "XMLHttpRequest")

	val headers_13 = Map(
		"accept" -> "application/json, text/plain, */*",
		"sec-ch-ua" -> """Google Chrome";v="87", " Not;A Brand";v="99", "Chromium";v="87""",
		"sec-ch-ua-mobile" -> "?0",
		"sec-fetch-dest" -> "empty",
		"sec-fetch-mode" -> "cors",
		"sec-fetch-site" -> "same-origin")

	val headers_40 = Map(
		"accept" -> "application/json, text/plain, */*",
		"content-type" -> "application/json",
		"csrf-token" -> "${csrf}",
		"origin" -> s"${baseURL}",
		"sec-ch-ua" -> """Google Chrome";v="87", " Not;A Brand";v="99", "Chromium";v="87""",
		"sec-ch-ua-mobile" -> "?0",
		"sec-fetch-dest" -> "empty",
		"sec-fetch-mode" -> "cors",
		"sec-fetch-site" -> "same-origin",
		"x-requested-with" -> "XMLHttpRequest")

	val headers_63 = Map(
		"accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
		"sec-ch-ua" -> """Google Chrome";v="87", " Not;A Brand";v="99", "Chromium";v="87""",
		"sec-ch-ua-mobile" -> "?0",
		"sec-fetch-dest" -> "document",
		"sec-fetch-mode" -> "navigate",
		"sec-fetch-site" -> "same-origin",
		"sec-fetch-user" -> "?1",
		"upgrade-insecure-requests" -> "1")

	val headers_64 = Map(
		"accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
		"cache-control" -> "max-age=0",
		"origin" -> "null",
		"sec-ch-ua" -> """Google Chrome";v="87", " Not;A Brand";v="99", "Chromium";v="87""",
		"sec-ch-ua-mobile" -> "?0",
		"sec-fetch-dest" -> "document",
		"sec-fetch-mode" -> "navigate",
		"sec-fetch-site" -> "cross-site",
		"upgrade-insecure-requests" -> "1")

	val headers_86 = Map(
		"content-type" -> "application/json",
		"origin" -> s"${PCIPALURL}",
		"sec-ch-ua" -> """Google Chrome";v="87", " Not;A Brand";v="99", "Chromium";v="87""",
		"sec-ch-ua-mobile" -> "?0",
		"sec-fetch-dest" -> "empty",
		"sec-fetch-mode" -> "cors",
		"sec-fetch-site" -> "same-origin",
		"x-requested-with" -> "XMLHttpRequest",
		"x-xsrf-token" -> "${XSRFToken}")


	val telephonyOnlineScenario = scenario("Telephony Online Scenario")
		.group("PaymentAPI${service}_090_Cases"){
		exec(http("PaymentAPI${service}_090_Cases")
			.get("/api/cases/1235123512351235")
			.headers(headers_11)
		  .check(status in (200,304)))
		  .pause(thinkTime)}

		.group("PaymentAPI${service}_100_BulkScanCases"){
		exec(http("PaymentAPI${service}_100_BulkScanCases")
			.get("/api/bulk-scan/cases/1235123512351235")
			.headers(headers_13)
			.check(status in (200,304)))
		.pause(thinkTime)}

	  .group("PaymentAPI${service}_110_PaymentHistory1"){
		exec(http("PaymentAPI${service}_110_PaymentHistory1")
			.get("/api/payment-history/cases/1235123512351235/paymentgroups")
			.headers(headers_13)
			.check(status in (200,304)))
		  .pause(thinkTime)}

		.group("PaymentAPI${service}_120_PaymentHistory2"){
		exec(http("PaymentAPI${service}_120_PaymentHistory2")
			.get("/api/payment-history/bulk-scan-feature")
			.headers(headers_11)
			.check(status in (200,304)))
		  .pause(thinkTime)}

		.group("PaymentAPI${service}_130_Fees"){
		exec(http("PaymentAPI${service}_130_Fees")
			.get("/api/fees")
			.headers(headers_13)
			.check(status in (200,304)))
		  .pause(thinkTime)}

		.group("PaymentAPI${service}_140_FeesJurisdictions1"){
		exec(http("PaymentAPI${service}_140_FeesJurisdictions1")
			.get("/api/fees-jurisdictions/1")
			.headers(headers_13)
			.check(status in (200,304)))
		  .pause(thinkTime)}

		.group("PaymentAPI${service}_150_FeesJurisdictions2"){
		exec(http("PaymentAPI${service}_150_FeesJurisdictions2")
			.get("/api/fees-jurisdictions/2")
			.headers(headers_13)
			.check(status in (200,304)))
		  .pause(thinkTime)}

		.exec(getCookieValue(CookieKey("_csrf").withDomain("paybubble.perftest.platform.hmcts.net").saveAs("csrf")))

		.group("PaymentAPI${service}_160_PaymentGroups1"){
		exec(http("PaymentAPI${service}_160_PaymentGroups1")
			.post("/api/payment-groups")
			.headers(headers_40)
			.body(RawFileBody("PCIPALRecording_0040_request.txt"))
			.check(status in (200,304)))
		  .pause(thinkTime)}

		.group("PaymentAPI${service}_170_PaymentHistory3"){
		exec(http("PaymentAPI${service}_170_PaymentHistory3")
			.get("/api/payment-history/bulk-scan-feature")
			.headers(headers_11)
			.check(status in (200,304)))
		  .pause(thinkTime)}

		.group("PaymentAPI${service}_180_PaymentHistory4"){
		exec(http("PaymentAPI${service}_180_PaymentHistory4")
			.get("/api/payment-history/payment-groups/2021-1611244599731")
			.headers(headers_13)
			.check(status in (200,304)))
		  .pause(thinkTime)}

		.group("PaymentAPI${service}_190_PaymentHistory5"){
		exec(http("PaymentAPI${service}_190_PaymentHistory5")
			.post("/api/payment-history/payment-groups/2021-1611244599731/telephony-card-payments")
			.headers(headers_40)
			.body(RawFileBody("PCIPALRecording_0060_request.txt"))
			.check(status in (200,304)))
		  .pause(thinkTime)}

		.group("PaymentAPI${service}_200_PCIPALThirdCall"){
		exec(http("PaymentAPI${service}_200_PCIPALThirdCall")
			.get("/pcipalThirdCall")
			.headers(headers_63)
			.check(status in (200,304)))
		  .pause(thinkTime)}

		.exec(getCookieValue(CookieKey("pcipal-auth-refresh").withDomain(".pcipalstaging.cloud").saveAs("authRefreshToken")))

		.group("PaymentAPI${service}_210_PCIPALStaging1"){
		exec(http("PaymentAPI${service}_210_PCIPALStaging1")
			.post(PCIPALURL + "/session/303/view/5b2f88d5-f4c5-4f43-848f-2c5668b7c2ef/framed")
			.headers(headers_64)
			.formParam("X-BEARER-TOKEN", "${authToken}")
			.formParam("X-REFRESH-TOKEN", "9fa704a7b90344a8928bdc8dc136e7c3")
			.check(status in (200,304)))
		  .pause(thinkTime)}

		.exec(getCookieValue(CookieKey("pcipal-xsrf-token").withDomain(".pcipalstaging.cloud").saveAs("XSRFToken")))

		.group("PaymentAPI${service}_220_PCIPALStaging2"){
		exec(http("PaymentAPI${service}_220_PCIPALStaging2")
			.post(PCIPALURL + "/api/v1/session/303/event/5b2f88d5-f4c5-4f43-848f-2c5668b7c2ef/3/update")
			.headers(headers_86)
			.body(RawFileBody("PCIPALRecording_0086_request.txt"))
			.check(status in (200,304)))
		  .pause(thinkTime)}

		.group("PaymentAPI${service}_230_PCIPALStaging3"){
		exec(http("PaymentAPI${service}_230_PCIPALStaging3")
			.post(PCIPALURL + "/api/v1/session/303/event/5b2f88d5-f4c5-4f43-848f-2c5668b7c2ef/42/click")
			.headers(headers_86)
			.check(status in (200,304)))
		  .pause(thinkTime)}

}