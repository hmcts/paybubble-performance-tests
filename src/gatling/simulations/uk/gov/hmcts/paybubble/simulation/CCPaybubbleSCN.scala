package uk.gov.hmcts.paybubble.simulation

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.paybubble.scenario.{DCNGenerator, OnlineTelephonyScenario, PayBubbleLogin, PaymentTransactionAPI}
import uk.gov.hmcts.paybubble.util.{Environment, IDAMHelper, S2SHelper}
import scala.concurrent.duration.DurationInt
import scala.util.Random

class CCPaybubbleSCN extends Simulation {

	val idamUrl = Environment.idamURL
	val baseURL=Environment.baseURL
	val bulkScanUrl=Environment.bulkScanURL
	val paymentAPIURL=Environment.paymentAPIURL
	val feeder =jsonFile("datagenforbulkscan.json").circular
	val feederonline =jsonFile("dataonlinepayment.json").circular
	val feederbulkscan =jsonFile("databulkscanpayments.json").circular
	val feedertelephone =jsonFile("datatelephonepayments.json").circular
	val feederpba =jsonFile("dataPBA.json").circular
	val feederpbaiac =csv("IAC_case_ids.csv").circular
	val feederViewCCDPayment =jsonFile("dataccdviewpayment.json").circular
	val onlineTelephonyFeeder = jsonFile("onlinetelephony.json").circular
	val usersFeeder = csv("users.csv").circular
	val caseNumber = Iterator.continually(Map("case_number" -> (1000000000L * (Random.nextInt(9000000) + 1000000) + Random.nextInt(1000000000))))

	val rampUpDurationMins = 2
	val rampDownDurationMins = 2
	val testDurationMins = 60
	val HourlyTarget:Double = 50
	val RatePerSec = HourlyTarget / 3600

	val httpProtocol = http
		.baseUrl(paymentAPIURL)
		//.proxy(Proxy("proxyout.reform.hmcts.net", 8080))

	val bulkscanhttpProtocol = http
		.baseUrl(bulkScanUrl)
		//.proxy(Proxy("proxyout.reform.hmcts.net", 8080))

	val baseProtocol = http
		.baseUrl(baseURL)
		.inferHtmlResources()
		.silentResources

	val createS2S_Scn = scenario("Create Bundling For IAC ")
		.exec(S2SHelper.getOTP)
		.exec(S2SHelper.S2SAuthToken)

	val datagendcn_Scn = scenario("Data Gen DCN ")
		.feed(feeder).feed(Feeders.DataGenBulkScanFeeder)
	  	.repeat(1) {
			exec(S2SHelper.S2SAuthToken)
			.exec(DCNGenerator.generateDCN)
			}

	val telephony_Scn = scenario("Offline Telephony Payments Scenario ")
  		.feed(feedertelephone).feed(Feeders.TelephoneFeeder)
	  	.repeat(1) {//40
			exec(IDAMHelper.getIdamToken).exec(S2SHelper.S2SAuthToken).exec(PaymentTransactionAPI.getPaymentGroupReference).exec(PaymentTransactionAPI.telephony)
			.pause(10)
			}

	val onlineTelephony_Scn = scenario("Online Telephony Payments Scenario").repeat(1)
	{ feed(onlineTelephonyFeeder)
		.feed(usersFeeder)
		.feed(caseNumber)
		.feed(Feeders.OnlineTelephonyFeeder)
		.exec(PayBubbleLogin.homePage)
		.exec(PayBubbleLogin.login)
		.exec(OnlineTelephonyScenario.onlineTelephonyScenario)
		.exec(PayBubbleLogin.logout)
	}

	val bulkscan_Scn = scenario("Offline Bulkscan Payments Scenario ")
  		.feed(feederbulkscan).feed(Feeders.BulkscanFeeder)
	  	.repeat(1) {//74
			exec(IDAMHelper.getIdamToken)
			.exec(S2SHelper.S2SAuthToken)
			.exec(PaymentTransactionAPI.getPaymentGroupReference)
			.exec(PaymentTransactionAPI.bulkscan)
			.exec(PaymentTransactionAPI.paymentAllocations)
			}

	val onlinePayment_Scn = scenario("Online Payments Scenario ")
  		.feed(feederonline).feed(Feeders.OnlinePaymentFeeder)
	  	.repeat(1) {//200
			exec(IDAMHelper.getIdamToken)
			.exec(S2SHelper.S2SAuthToken)
			.exec(PaymentTransactionAPI.onlinePayment)
			}

	val PBA_Scn = scenario("Pay By Account Scenario ")
  		.feed(feederpba).feed(feederpbaiac).feed(Feeders.PBAFeeder)
	  	.repeat(1) {//25
			//exec(IDAMHelper.getIdamToken)
			exec(S2SHelper.S2SAuthToken)
			//.exec(PaymentTransactionAPI.PBA)
			//.exec(PaymentTransactionAPI.PBA_IAC)
			.exec(PaymentTransactionAPI.reconciliationPayments)

			}

	val CCDViewPayment_Scn = scenario("View Payments Scenario ")
  		.feed(feederViewCCDPayment).feed(Feeders.ViewPaymentsFeeder)
	  	.repeat(1) {//271
			exec(IDAMHelper.getIdamToken)
			.exec(S2SHelper.S2SAuthToken)
			.exec(PaymentTransactionAPI.getPaymentReferenceByCase)
			.exec(PaymentTransactionAPI.ccdViewPayment)
			}

	val processpaymentmatch_Scn = scenario("Payment Transaction ")
		.feed(feeder)
  		.exec(PayBubbleLogin.homePage)
  		.exec(PayBubbleLogin.login)
		/*.exec(PaymentByDCN.searchByDCN)
		.exec(PaymentByDCN.searchAndAddFee)
		.exec(PaymentByDCN.paymentProcess)
		.exec(PaymentByDCN.PaymentProcessed)*/

	/*setUp(datagendcn_Scn.inject(nothingFor(15),rampUsers(1199) during (1800))).protocols(bulkscanhttpProtocol)*/
	/*setUp(telephony_Scn.inject(atOnceUsers(1))).protocols(httpProtocol)*/
	//setUp(bulkscan_Scn.inject(atOnceUsers(1))).protocols(httpProtocol)
	//setUp(onlinePayment_Scn.inject(atOnceUsers(1))).protocols(httpProtocol)
	//setUp(PBA_Scn.inject(atOnceUsers(1))).protocols(httpProtocol)
	//setUp(CCDViewPayment_Scn.inject(atOnceUsers(1))).protocols(httpProtocol)
	//setUp(processpaymentmatch_Scn.inject(atOnceUsers(1))).protocols(httpProtocol)

	/*setUp(
		telephony_Scn.inject(nothingFor(15),rampUsers(100) during (600)),
		//bulkscan_Scn.inject(nothingFor(15),rampUsers(500) during (1800))
		//onlinePayment_Scn.inject(nothingFor(15),rampUsers(3000) during (2400)
	PBA_Scn.inject(nothingFor(15),rampUsers(199) during (600))
	//	CCDViewPayment_Scn.inject(atOnceUsers(1))
	).protocols(httpProtocol)*/

  	/*setUp(
    telephony_Scn.inject(nothingFor(1),rampUsers(1) during (1))
    /*onlinePayment_Scn.inject(nothingFor(25),rampUsers(130) during (3500)),
    bulkscan_Scn.inject(nothingFor(35),rampUsers(20) during (3500)),
    PBA_Scn.inject(nothingFor(45),rampUsers(18) during (3500)),
    telephony_Scn.inject(nothingFor(55),rampUsers(3) during (3500))*/
  	).protocols(httpProtocol)*/

	/*setUp(
	CCDViewPayment_Scn.inject(nothingFor(15),rampUsers(4200) during (3500)),
	onlinePayment_Scn.inject(nothingFor(25),rampUsers(130) during (3500)),
		bulkscan_Scn.inject(nothingFor(35),rampUsers(20) during (3500)),
		PBA_Scn.inject(nothingFor(45),rampUsers(18) during (3500)),
		telephony_Scn.inject(nothingFor(55),rampUsers(3) during (3500))
	).protocols(httpProtocol)*/

  	/*setUp(
	CCDViewPayment_Scn.inject(nothingFor(15),rampUsers(13000) during (3500)),
	onlinePayment_Scn.inject(nothingFor(25),rampUsers(100) during (3500)),
	bulkscan_Scn.inject(nothingFor(35),rampUsers(1099) during (3500)),
	PBA_Scn.inject(nothingFor(45),rampUsers(79) during (3500)),
	telephony_Scn.inject(nothingFor(55),rampUsers(100) during (3500))
	).protocols(httpProtocol)*/

	setUp(PBA_Scn.inject(rampUsers(1) during(10))).protocols(httpProtocol)

	/*setUp(PBA_Scn.inject(
		rampUsersPerSec(0.00) to (RatePerSec) during (rampUpDurationMins minutes),
		constantUsersPerSec(RatePerSec) during (testDurationMins minutes),
		rampUsersPerSec(RatePerSec) to (0.00) during (rampDownDurationMins minutes)
	)).protocols(httpProtocol)*/
}
