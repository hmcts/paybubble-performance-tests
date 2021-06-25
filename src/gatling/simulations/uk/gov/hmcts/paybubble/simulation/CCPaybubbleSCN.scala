package uk.gov.hmcts.paybubble.simulation

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.paybubble.scenario.{DCNGenerator, OnlineTelephonyScenario, PayBubbleLogin, PaymentTransactionAPI}
import uk.gov.hmcts.paybubble.scenario.util._
import com.typesafe.config.{Config, ConfigFactory}

class CCPaybubbleSCN extends Simulation {

	val config: Config = ConfigFactory.load()

	val idamUrl = Environment.idamURL
	val baseURL=Environment.baseURL
	val bulkScanUrl=Environment.bulkScanURL
	val paymentAPIURL=Environment.paymentAPIURL
	val feeder =jsonFile("datagenforbulkscan.json").circular
	val feederonline =jsonFile("dataonlinepayment.json").circular
	val feederbulkscan =jsonFile("databulkscanpayments.json").circular
	val feedertelephone =jsonFile("datatelephonepayments.json").circular
	val feederpba =jsonFile("dataPBA.json").circular
	val feederViewCCDPayment =jsonFile("dataccdviewpayment.json").circular
	val onlineTelephonyFeeder = jsonFile("onlinetelephony.json").circular
	val onlineTelephonyCaseFeeder = csv("onlinetelephonycaseids.csv").circular
	val usersFeeder = csv("users.csv").circular

	val httpProtocol = http
		.baseUrl(paymentAPIURL)
		.inferHtmlResources()
		.silentResources
		//.proxy(Proxy("proxyout.reform.hmcts.net", 8080))

	val bulkscanhttpProtocol = http
		.baseUrl(bulkScanUrl)
		//.proxy(Proxy("proxyout.reform.hmcts.net", 8080))

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
			exec(IDAMHelper.getIdamTokenLatest).exec(S2SHelper.S2SAuthToken).exec(PaymentTransactionAPI.getPaymentGroupReference).exec(PaymentTransactionAPI.telephony)
			.pause(10)
			}

	val onlineTelephony_Scn = scenario("Online Telephony Payments Scenario").repeat(1)
	{ feed(onlineTelephonyFeeder)
		.feed(usersFeeder)
		.feed(onlineTelephonyCaseFeeder)
		.feed(Feeders.OnlineTelephonyFeeder)
		.exec(PayBubbleLogin.homePage)
		.exec(PayBubbleLogin.login)
		.exec(OnlineTelephonyScenario.onlineTelephonyScenario)
		.exec(PayBubbleLogin.logout)
	}

	val bulkscan_Scn = scenario("Offline Bulkscan Payments Scenario ")
  		.feed(feederbulkscan).feed(Feeders.BulkscanFeeder)
	  	.repeat(1) {//74
			exec(IDAMHelper.getIdamTokenLatest)
			.exec(S2SHelper.S2SAuthToken)
			.exec(PaymentTransactionAPI.getPaymentGroupReference)
			.exec(PaymentTransactionAPI.bulkscan)
			.exec(PaymentTransactionAPI.paymentAllocations)
			}

	val onlinePayment_Scn = scenario("Online Payments Scenario ")
  		.feed(feederonline).feed(Feeders.OnlinePaymentFeeder)
	  	.repeat(1) {//200
			exec(IDAMHelper.getIdamTokenLatest)
			.exec(S2SHelper.S2SAuthToken)
			.exec(PaymentTransactionAPI.onlinePayment)
			}

	val PBA_Scn = scenario("Pay By Account Scenario ")
  		.feed(feederpba).feed(Feeders.PBAFeeder)
	  	.repeat(1) {//25
			exec(IDAMHelper.getIdamTokenLatest)
			.exec(S2SHelper.S2SAuthToken)
			.exec(PaymentTransactionAPI.PBA)
			}

	val CCDViewPayment_Scn = scenario("View Payments Scenario ")
  		.feed(feederViewCCDPayment).feed(Feeders.ViewPaymentsFeeder)
	  	.repeat(1) {//271
			exec(IDAMHelper.getIdamTokenLatest)
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

  	setUp(
	CCDViewPayment_Scn.inject(rampUsers(10) during (300)),
	onlinePayment_Scn.inject(rampUsers(10) during (300)),
	bulkscan_Scn.inject(rampUsers(10) during (300)),
	PBA_Scn.inject(rampUsers(10) during (300)),
	telephony_Scn.inject(rampUsers(10) during (300)),
	onlineTelephony_Scn.inject(rampUsers(10) during (300))
 ).protocols(httpProtocol)
	.assertions(global.successfulRequests.percent.gte(95))
	.assertions(forAll.successfulRequests.percent.gte(90))

}
