package uk.gov.hmcts.paybubble.scenario

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.paybubble.util.Environment._

object OnlineTelephonyScenario extends Simulation {

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

	val onlineTelephonyScenario = scenario("Online Telephony Payments Scenario")

		// Enter case number
		.group("PaymentAPI${service}_090_100_110"){
		exec(http("PaymentAPI${service}_090_Cases")
			.get("/api/cases/${case_number}")
			.headers(headers_11)
		  .check(status in (200,304)))

			.exec(http("PaymentAPI${service}_100_PaymentHistory1")
			.get("/api/payment-history/cases/${case_number}/paymentgroups")
			.headers(headers_13)
			.check(status.is(404)))

			.exec(http("PaymentAPI${service}_110_BulkScanCases")
			.get("/api/bulk-scan/cases/${case_number}")
			.headers(headers_13)
			.check(status in (200,304)))}
		  .pause(thinkTime)

		// Click on 'take telephony payment'
		.group("PaymentAPI${service}_120_130"){
		exec(http("PaymentAPI${service}_120_PaymentHistory2")
			.get("/api/payment-history/bulk-scan-feature")
			.headers(headers_11)
			.check(status in (200,304)))

		.exec(http("PaymentAPI${service}_130_Fees")
			.get("/api/fees")
			.headers(headers_13)
			.check(status in (200,304)))}
		  .pause(thinkTime)

		// Search for '550'
		.group("PaymentAPI${service}_140_150"){
		exec(http("PaymentAPI${service}_140_FeesJurisdictions1")
			.get("/api/fees-jurisdictions/1")
			.headers(headers_13)
			.check(status in (200,304)))

		.exec(http("PaymentAPI${service}_150_FeesJurisdictions2")
			.get("/api/fees-jurisdictions/2")
			.headers(headers_13)
			.check(status in (200,304)))}
		  .pause(thinkTime)

		// Select a fee
		.group("PaymentAPI${service}_160_170_180"){
		exec(http("PaymentAPI${service}_160_PaymentGroups1")
			.post("/api/payment-groups")
			.headers(headers_40)
			.body(StringBody("""{"fees":[{"code":"${code}","version":"${version}","calculated_amount":"550","memo_line":"${memo_line}","natural_account_code":"${natural_account_code}","ccd_case_number":"${case_number}","jurisdiction1":"${jurisdiction1}","jurisdiction2":"${jurisdiction2}","description":"${description}","volume":1,"fee_amount":"550"}]}"""))
			.check(jsonPath("$..payment_group_reference").saveAs("paymentGroup"))
			.check(status in (200,304)))

		.exec(http("PaymentAPI${service}_170_PaymentHistory3")
			.get("/api/payment-history/bulk-scan-feature")
			.headers(headers_11)
			.check(status in (200,304)))

		.exec(http("PaymentAPI${service}_180_PaymentHistory4")
			.get("/api/payment-history/payment-groups/${paymentGroup}")
			.headers(headers_13)
			.check(status in (200,304)))}
		  .pause(thinkTime)

		//Select 'Divorce' for 'What service is this fee for?' and 'Antenna' for 'Which system are you using to take the payment?'
		.group("PaymentAPI${service}_190_200_210"){
		exec(http("PaymentAPI${service}_190_PaymentHistory5")
			.post("/api/payment-history/payment-groups/${paymentGroup}/telephony-card-payments")
			.headers(headers_40)
			.body(StringBody("""{"currency":"GBP","ccd_case_number":"${case_number}","amount":"550.00","service":"DIVORCE","site_id":"AA07"}"""))
			.check(headerRegex("Set-Cookie", """__pcipal-info=j%3A%7B%22url%22%3A%22https%3A%2F%2Feuwest1.pcipalstaging.cloud%2Fsession%2F303%2Fview%2F(.*)%2Fframed""").saveAs("sessionId"))
			.check(headerRegex("Set-Cookie", """%2Fframed%22%2C%22auth%22%3A%22(.*)%22%2C""").saveAs("authToken"))
			.check(status in (200,304)))

		.exec(http("PaymentAPI${service}_200_PCIPALThirdCall")
			.get("/pcipalThirdCall")
			.headers(headers_63)
			.check(regex("""<input type='hidden' name='X-REFRESH-TOKEN' value='(.*)'>Loading""").saveAs("refreshToken"))
			.check(status in (200,304)))

		.exec(http("PaymentAPI${service}_210_PCIPALStaging1")
			.post(PCIPALURL + "/session/303/view/${sessionId}/framed")
			.headers(headers_64)
			.formParam("X-BEARER-TOKEN", "${authToken}")
			.formParam("X-REFRESH-TOKEN", "${refreshToken}")
			.check(headerRegex("Set-Cookie", """pcipal-xsrf-token=(.*); domain=.pcipalstaging.cloud;""").transform(str => str.replace("%2F", "/")).transform(str => str.replace("%2B", "+")).transform(str => str.replace("%3D", "=")).saveAs("XSRFToken"))
			.check(status in (200,304)))}
		  .pause(thinkTime)

		// Enter 'Mr John Smith' for 'Cardholder Name'
		.group("PaymentAPI${service}_220"){
		exec(http("PaymentAPI${service}_220_PCIPALStaging2")
			.post(PCIPALURL + "/api/v1/session/303/event/${sessionId}/3/update")
			.headers(headers_86)
			.body(StringBody("""{"Value":"Mr John Smith"}"""))
			.check(status in (200,304)))}
		  .pause(thinkTime)

		// Click on 'Continue'
		.group("PaymentAPI${service}_230"){
		exec(http("PaymentAPI${service}_230_PCIPALStaging3")
			.post(PCIPALURL + "/api/v1/session/303/event/${sessionId}/42/click")
			.headers(headers_86)
			.check(status in (200,304)))}
		  .pause(thinkTime)

}