package uk.gov.hmcts.paybubble.simulation

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.paybubble.scenario.{DCNGenerator, PayBubbleLogin, PaymentTransactionAPI}
import uk.gov.hmcts.paybubble.util.{Environment, IDAMHelper, S2SHelper}

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
	val feederViewCCDPayment =jsonFile("dataccdviewpayment.json").circular
	val httpProtocol = http
		.baseUrl(paymentAPIURL)
		.proxy(Proxy("proxyout.reform.hmcts.net", 8080))

	val bulkscanhttpProtocol = http
										 .baseUrl(bulkScanUrl)
										 .proxy(Proxy("proxyout.reform.hmcts.net", 8080))


  val createS2S_Scn = scenario("Create Bundling For IAC ")
		.exec(S2SHelper.getOTP)
		.exec(S2SHelper.S2SAuthToken)

	val datagendcn_Scn = scenario("Data Gen DCN ")
	  	.repeat(25) {
				feed(feeder).feed(Feeders.DataGenBulkScanFeeder)
				//.exec(IDAMHelper.getIdamToken)
			  	.exec(S2SHelper.S2SAuthToken)
					.exec(DCNGenerator.generateDCN)
			}

	val telephony_Scn = scenario("Offline Telephony Payments Scenario ")
	  	.repeat(3) {//40
				feed(feedertelephone).feed(Feeders.TelephoneFeeder).exec(IDAMHelper.getIdamToken).exec(S2SHelper.S2SAuthToken).exec(PaymentTransactionAPI.getPaymentGroupReference).exec(PaymentTransactionAPI.telephony)
			  	.pause(1000)
			}

	val bulkscan_Scn = scenario("Offline Bulkscan Payments Scenario ")
	  	.repeat(20) {//74
				feed(feederbulkscan).feed(Feeders.BulkscanFeeder).exec(IDAMHelper.getIdamToken).exec(S2SHelper.S2SAuthToken).exec(PaymentTransactionAPI.getPaymentGroupReference).exec(PaymentTransactionAPI.bulkscan).exec(PaymentTransactionAPI.paymentAllocations)
			}

	val onlinePayment_Scn = scenario("Online Payments Scenario ")
	  	.repeat(130) {//200
				feed(feederonline).feed(Feeders.OnlinePaymentFeeder).exec(IDAMHelper.getIdamToken).exec(S2SHelper.S2SAuthToken).exec(PaymentTransactionAPI.onlinePayment)

			}

	val PBA_Scn = scenario("Pay By Account Scenario ")
	  	.repeat(18) {//25
				feed(feederpba).feed(Feeders.PBAFeeder).exec(IDAMHelper.getIdamToken).exec(S2SHelper.S2SAuthToken).exec(PaymentTransactionAPI.PBA)

			}

	val CCDViewPayment_Scn = scenario("Pay By Account Scenario ")
	  	.repeat(271) {//271
				feed(feederViewCCDPayment).feed(Feeders.ViewPaymentsFeeder).exec(IDAMHelper.getIdamToken).exec(S2SHelper.S2SAuthToken).exec(PaymentTransactionAPI.getPaymentReferenceByCase).exec(PaymentTransactionAPI.ccdViewPayment)
			}

	val processpaymentmatch_Scn = scenario("Payment Transaction ")
		.feed(feeder)
  	.exec(PayBubbleLogin.homePage)
  	.exec(PayBubbleLogin.login)
  	/*.exec(PaymentByDCN.searchByDCN)
  	.exec(PaymentByDCN.searchAndAddFee)
  	.exec(PaymentByDCN.paymentProcess)
  	.exec(PaymentByDCN.PaymentProcessed)*/

	//setUp(datagendcn_Scn.inject(atOnceUsers(1))).protocols(bulkscanhttpProtocol)
	/*setUp(telephony_Scn.inject(atOnceUsers(1))).protocols(httpProtocol)*/
	//setUp(bulkscan_Scn.inject(atOnceUsers(1))).protocols(httpProtocol)
	//setUp(onlinePayment_Scn.inject(atOnceUsers(1))).protocols(httpProtocol)
	//setUp(PBA_Scn.inject(atOnceUsers(1))).protocols(httpProtocol)
	//setUp(CCDViewPayment_Scn.inject(atOnceUsers(1))).protocols(httpProtocol)
	//setUp(processpaymentmatch_Scn.inject(atOnceUsers(1))).protocols(httpProtocol)

	setUp(
		telephony_Scn.inject(atOnceUsers(1)),
		bulkscan_Scn.inject(atOnceUsers(1)),
		onlinePayment_Scn.inject(atOnceUsers(1)),
		PBA_Scn.inject(atOnceUsers(1)),
		CCDViewPayment_Scn.inject(atOnceUsers(1))
	).protocols(httpProtocol)
}
