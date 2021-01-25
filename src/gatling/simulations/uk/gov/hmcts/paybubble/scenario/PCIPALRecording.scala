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
		"csrf-token" -> "eH1ftMXR-sLKkOGyew9zuFFMinvfvrH6nybc",
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
		.exec(http("PaymentAPI${service}_010_Cases")
			.get("/api/cases/1235123512351235")
			.headers(headers_11)
		  .check(status.is(200)))
		  .pause(thinkTime)

	.exec(http("PaymentAPI${service}_020_PaymentHistory1")
			.get("/api/payment-history/cases/1235123512351235/paymentgroups")
			.headers(headers_13)
			.check(status.is(200)))
		  .pause(thinkTime)

		.exec(http("PaymentAPI${service}_030_BulkScanCases")
			.get("/api/bulk-scan/cases/1235123512351235")
			.headers(headers_13)
			.check(status.is(200)))
			.pause(thinkTime)

		.exec(http("PaymentAPI${service}_040_PaymentHistory2")
			.get("/api/payment-history/bulk-scan-feature")
			.headers(headers_11)
			.check(status.is(200)))
		  .pause(thinkTime)

		.exec(http("PaymentAPI${service}_050_Fees")
			.get("/api/fees")
			.headers(headers_13)
			.check(status.is(200)))
		  .pause(thinkTime)

		.exec(http("PaymentAPI${service}_060_FeesJurisdictions1")
			.get("/api/fees-jurisdictions/1")
			.headers(headers_13)
			.check(status.is(200)))
		  .pause(thinkTime)

		.exec(http("PaymentAPI${service}_070_FeesJurisdictions2")
			.get("/api/fees-jurisdictions/2")
			.headers(headers_13)
			.check(status.is(200)))
		  .pause(thinkTime)

		.exec(http("PaymentAPI${service}_080_PaymentGroups1")
			.post("/api/payment-groups")
			.headers(headers_40)
			.body(RawFileBody("PCIPALRecording_0040_request.txt"))
			.check(status.is(200)))
		  .pause(thinkTime)

		.exec(http("PaymentAPI${service}_090_PaymentGroups2")
			.post("/api/payment-groups")
			.headers(headers_40)
			.body(RawFileBody("PCIPALRecording_0042_request.txt"))
			.check(status.is(200)))
		  .pause(thinkTime)

		.exec(http("PaymentAPI${service}_100_PaymentHistory3")
			.get("/api/payment-history/bulk-scan-feature")
			.headers(headers_11)
			.check(status.is(200)))
		  .pause(thinkTime)

		.exec(http("PaymentAPI${service}_110_PaymentHistory4")
			.get("/api/payment-history/payment-groups/2021-1611244599731")
			.headers(headers_13)
			.check(status.is(200)))
		  .pause(thinkTime)

		.exec(http("PaymentAPI${service}_120_PaymentHistory5")
			.post("/api/payment-history/payment-groups/2021-1611244599731/telephony-card-payments")
			.headers(headers_40)
			.body(RawFileBody("PCIPALRecording_0060_request.txt"))
			.check(status.is(200)))
		  .pause(thinkTime)

		.exec(http("PaymentAPI${service}_130_PCIPALThirdCall")
			.get("/pcipalThirdCall")
			.headers(headers_63)
			.check(status.is(200)))
		  .pause(thinkTime)

		.exec(http("PaymentAPI${service}_140_PCIPALStaging1")
			.post(PCIPALURL + "/session/303/view/5b2f88d5-f4c5-4f43-848f-2c5668b7c2ef/framed")
			.headers(headers_64)
			.formParam("X-BEARER-TOKEN", "${accessToken}")
			.formParam("X-REFRESH-TOKEN", "9fa704a7b90344a8928bdc8dc136e7c3")
			.check(status.is(200)))
		  .pause(thinkTime)

		.exec(getCookieValue(CookieKey("pcipal-auth-refresh").withDomain(".pcipalstaging.cloud").saveAs("authRefreshToken")))
		.exec(getCookieValue(CookieKey("pcipal-xsrf-token").withDomain(".pcipalstaging.cloud").saveAs("XSRFToken")))

		.exec(http("PaymentAPI${service}_150_PCIPALStaging2")
			.post(PCIPALURL + "/api/v1/session/303/event/5b2f88d5-f4c5-4f43-848f-2c5668b7c2ef/3/update")
			.headers(headers_86)
			.body(RawFileBody("PCIPALRecording_0086_request.txt"))
			.check(status.is(200)))
		  .pause(thinkTime)

		.exec(http("PaymentAPI${service}_160_PCIPALStaging3")
			.post(PCIPALURL + "/api/v1/session/303/event/5b2f88d5-f4c5-4f43-848f-2c5668b7c2ef/42/click")
			.headers(headers_86)
			.check(status.is(200)))
		  .pause(thinkTime)

}