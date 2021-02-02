package uk.gov.hmcts.paybubble.scenario

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.paybubble.util.Environment._

object OnlineTelephonyScenario extends Simulation {

	val common_header_1 = Map(
		"accept" -> "application/json, text/plain, */*",
		"sec-ch-ua" -> """Google Chrome";v="87", " Not;A Brand";v="99", "Chromium";v="87""",
		"sec-ch-ua-mobile" -> "?0",
		"sec-fetch-dest" -> "empty",
		"sec-fetch-mode" -> "cors",
		"sec-fetch-site" -> "same-origin")

	val common_header_2 = Map(
		"accept" -> "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9",
		"sec-ch-ua" -> """Google Chrome";v="87", " Not;A Brand";v="99", "Chromium";v="87""",
		"sec-ch-ua-mobile" -> "?0",
		"sec-fetch-dest" -> "document",
		"sec-fetch-mode" -> "navigate",
		"upgrade-insecure-requests" -> "1")

	val csrf_header = Map(
		"csrf-token" -> "${csrf}",
		"x-requested-with" -> "XMLHttpRequest")

	// Note: String interpolation is used so that the baseURL variable can be used directly without needing to add it to the session first
	val content_type_header = Map(
		"content-type" -> "application/json",
		"origin" -> s"${baseURL}")

	val pcipal_header_1 = Map(
		"sec-fetch-site" -> "same-origin",
		"sec-fetch-user" -> "?1")

	val pcipal_header_2 = Map(
		"cache-control" -> "max-age=0",
		"origin" -> "null",
		"sec-fetch-site" -> "cross-site")

	// Note: String interpolation is used so that the PCIPALURL variable can be used directly without needing to add it to the session first
	val pcipal_header_3 = Map(
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

		// Submit case number
		.group("PaymentAPI${service}_030_SubmitCaseNumber"){
			exec(http("PaymentAPI${service}_030_010_Cases")
			.get("/api/cases/${case_number}")
				.headers(common_header_1)
				.headers(csrf_header)
				.check(substring("OK"))
				.check(status in (200,304)))

			.exec(http("PaymentAPI${service}_030_020_PaymentHistory1")
			.get("/api/payment-history/cases/${case_number}/paymentgroups")
				.headers(common_header_1)
				.check(substring("404 - undefined"))
				.check(status.is(404)))

				.exec(http("PaymentAPI${service}_030_030_BulkScanCases")
			.get("/api/bulk-scan/cases/${case_number}")
				.headers(common_header_1)
				.check(jsonPath("$..success").is("true"))
				.check(status in (200,304)))
		}

		  .pause(thinkTime)

		// Click on 'take telephony payment'
		.group("PaymentAPI${service}_040_ClickOnTakeTelephonyPayment"){
			exec(http("PaymentAPI${service}_040_010_PaymentHistory2")
			.get("/api/payment-history/bulk-scan-feature")
				.headers(common_header_1)
				.headers(csrf_header)
				.check(jsonPath("$..enable").is("true"))
				.check(status in (200,304)))

			.exec(http("PaymentAPI${service}_040_020_Fees")
			.get("/api/fees")
				.headers(common_header_1)
				.check(jsonPath("$..unspecified_claim_amount").is("true"))
				.check(status in (200,304)))
		}

		.pause(thinkTime)

		// Search for '550'
		.group("PaymentAPI${service}_050_SearchFor550"){
			exec(http("PaymentAPI${service}_050_010_FeesJurisdictions1")
			.get("/api/fees-jurisdictions/1")
				.headers(common_header_1)
				.check(jsonPath("$..name").is("civil"))
				.check(status in (200,304)))

			.exec(http("PaymentAPI${service}_050_020_FeesJurisdictions2")
			.get("/api/fees-jurisdictions/2")
				.headers(common_header_1)
				.check(jsonPath("$..name").is("county court"))
				.check(status in (200,304)))
		}

		.pause(thinkTime)

		// Select a fee
		.group("PaymentAPI${service}_060_SelectAFee"){
			exec(http("PaymentAPI${service}_060_010_PaymentGroups1")
			.post("/api/payment-groups")
				.headers(common_header_1)
				.headers(csrf_header)
				.headers(content_type_header)
				.body(StringBody("""{"fees":[{"code":"${code}","version":"${version}","calculated_amount":"550","memo_line":"${memo_line}","natural_account_code":"${natural_account_code}","ccd_case_number":"${case_number}","jurisdiction1":"${jurisdiction1}","jurisdiction2":"${jurisdiction2}","description":"${description}","volume":1,"fee_amount":"550"}]}"""))
				.check(jsonPath("$..payment_group_reference").saveAs("paymentGroup"))
				.check(status in (200,304)))

			.exec(http("PaymentAPI${service}_060_020_PaymentHistory3")
			.get("/api/payment-history/bulk-scan-feature")
				.headers(common_header_1)
				.headers(csrf_header)
				.check(jsonPath("$..enable").is("true"))
				.check(status in (200,304)))

			.exec(http("PaymentAPI${service}_060_030_PaymentHistory4")
			.get("/api/payment-history/payment-groups/${paymentGroup}")
				.headers(common_header_1)
				.check(jsonPath("$..payment_group_reference").is("${paymentGroup}"))
				.check(status in (200,304)))
		}

		.pause(thinkTime)

		//Select 'Divorce' for 'What service is this fee for?' and 'Antenna' for 'Which system are you using to take the payment?'
		.group("PaymentAPI${service}_070_SelectDivorceAndAntenna"){
			exec(http("PaymentAPI${service}_070_010_PaymentHistory5")
			.post("/api/payment-history/payment-groups/${paymentGroup}/telephony-card-payments")
				.headers(common_header_1)
				.headers(csrf_header)
				.headers(content_type_header)
				.body(StringBody("""{"currency":"GBP","ccd_case_number":"${case_number}","amount":"550.00","service":"DIVORCE","site_id":"AA07"}"""))
				.check(headerRegex("Set-Cookie", """%2Fsession%2F303%2Fview%2F(.*)%2Fframed""").saveAs("sessionId"))
				.check(headerRegex("Set-Cookie", """%2Fframed%22%2C%22auth%22%3A%22(.*)%22%2C""").saveAs("authToken"))
				.check(status in (200,304)))

			.exec(http("PaymentAPI${service}_070_020_PCIPALThirdCall")
			.get("/pcipalThirdCall")
				.headers(common_header_2)
				.headers(pcipal_header_1)
				.check(regex("""<input type='hidden' name='X-REFRESH-TOKEN' value='(.*)'>Loading""").saveAs("refreshToken"))
				.check(status in (200,304)))

			.exec(http("PaymentAPI${service}_070_030_PCIPALStaging1")
			.post(PCIPALURL + "/session/303/view/${sessionId}/framed")
				.headers(common_header_2)
				.headers(pcipal_header_2)
				.formParam("X-BEARER-TOKEN", "${authToken}")
				.formParam("X-REFRESH-TOKEN", "${refreshToken}")
				.check(headerRegex("Set-Cookie", """pcipal-xsrf-token=(.*);""").transform(str => str.replace("%2F", "/")).transform(str => str.replace("%2B", "+")).transform(str => str.replace("%3D", "=")).saveAs("XSRFToken"))
				.check(status in (200,304)))
		}

		.pause(thinkTime)

		// Enter 'Mr John Smith' for 'Cardholder Name'
		.group("PaymentAPI${service}_080_EnterCardholderName"){
			exec(http("PaymentAPI${service}_080_010_PCIPALStaging2")
			.post(PCIPALURL + "/api/v1/session/303/event/${sessionId}/3/update")
				.headers(pcipal_header_3)
				.body(StringBody("""{"Value":"Mr John Smith"}"""))
				.check(headerRegex("Cache-control", "no-cache, no-store, must-revalidate"))
				.check(status in (200,304)))
		}

		.pause(thinkTime)

		// Click on 'Continue'
		.group("PaymentAPI${service}_090_ClickOnContinue"){
			exec(http("PaymentAPI${service}_090_010_PCIPALStaging3")
			.post(PCIPALURL + "/api/v1/session/303/event/${sessionId}/42/click")
				.headers(pcipal_header_3)
				.check(headerRegex("Cache-control", "no-cache, no-store, must-revalidate"))
				.check(status in (200,304)))
		}

		.pause(thinkTime)

}