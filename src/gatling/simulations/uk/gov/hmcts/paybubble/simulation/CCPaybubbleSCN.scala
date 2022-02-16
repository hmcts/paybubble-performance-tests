package uk.gov.hmcts.paybubble.simulation

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.core.scenario._
import uk.gov.hmcts.paybubble.scenario._
import uk.gov.hmcts.paybubble.util._
import scala.concurrent.duration.DurationInt
import scala.language.postfixOps
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
	val dcnnumberFeeder = csv("dcn_numbers.csv").circular
	val CPOCaseIdFeeder =csv("CPO_case_ids.csv").circular
	val orderReferencesFeeder =csv("order_references.csv").circular
  val refundsUsersFeeder = csv("RefundUsers.csv").circular
  val refundIDsFeeder = csv("RefundData.csv").queue
	val refundIDsFeeder = csv("InternalRef.csv").circular
	val caseNumber = Iterator.continually(Map("case_number" -> (1000000000L * (Random.nextInt(9000000) + 1000000) + Random.nextInt(1000000000))))
	val UUID = Iterator.continually(Map("UUID" -> java.util.UUID.randomUUID.toString))

	val rampUpDurationMins = 2
	val rampDownDurationMins = 2
	val testDurationMins = 60

	val CCDViewPaymentHourlyTarget:Double = 459
	val CCDViewPaymentRatePerSec = CCDViewPaymentHourlyTarget / 3600

	val onlinePaymentHourlyTarget:Double = 220
	val onlinePaymentRatePerSec = onlinePaymentHourlyTarget / 3600

	val bulkscanHourlyTarget:Double = 79
	val bulkscanRatePerSec = bulkscanHourlyTarget / 3600

	val PBAHourlyTarget:Double = 186
	val PBARatePerSec = PBAHourlyTarget / 3600

	val telephonyHourlyTarget:Double = 14
	val telephonyRatePerSec = telephonyHourlyTarget / 3600

	val addOrderHourlyTarget:Double = 531
	val addOrderRatePerSec = addOrderHourlyTarget / 3600

	val createPaymentHourlyTarget:Double = 238
	val createPaymentRatePerSec = createPaymentHourlyTarget / 3600

	val getOrderHourlyTarget:Double = 65
	val getOrderRatePerSec = getOrderHourlyTarget / 3600

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
		.feed(feeder)
    .feed(Feeders.DataGenBulkScanFeeder)
	  	.repeat(1) {
			exec(S2SHelper.S2SAuthToken)
			.exec(DCNGenerator.generateDCN)
			}

	val telephony_Scn = scenario("Offline Telephony Payments Scenario ")
  		.feed(feedertelephone).feed(Feeders.TelephoneFeeder)
	  	.repeat(1) {//40
			exec(IDAMHelper.getIdamToken)
      .exec(S2SHelper.S2SAuthToken)
      .exec(PaymentTransactionAPI.getPaymentGroupReference)
      .exec(PaymentTransactionAPI.telephony)
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
  		.feed(feederbulkscan).feed(dcnnumberFeeder).feed(Feeders.BulkscanFeeder)
	  	.repeat(1) {//74
			exec(IDAMHelper.getIdamToken)
			.exec(S2SHelper.S2SAuthToken)
			//.exec(DCNGenerator.generateDCN)
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
			exec(IDAMHelper.getIdamToken)
			.exec(S2SHelper.S2SAuthToken)
			.exec(PaymentTransactionAPI.PBA)
			//.exec(PaymentTransactionAPI.PBA_IAC)
			//.exec(PaymentTransactionAPI.reconciliationPayments)

			}

	val CCDViewPayment_Scn = scenario("View Payments Scenario ")
  		.feed(feederViewCCDPayment).feed(Feeders.ViewPaymentsFeeder)
	  	.repeat(1) {//271
			exec(IDAMHelper.getIdamToken)
			.exec(S2SHelper.S2SAuthToken)
			.exec(PaymentTransactionAPI.getPaymentReferenceByCase)
			.exec(PaymentTransactionAPI.ccdViewPayment)
			.exec(PaymentTransactionAPI.getPaymentGroupReferenceByCase)
			.exec(PaymentTransactionAPI.getPaymentByCase)
			}

	val processpaymentmatch_Scn = scenario("Payment Transaction ")
		.feed(feeder)
  		.exec(PayBubbleLogin.homePage)
  		.exec(PayBubbleLogin.login)
		/*.exec(PaymentByDCN.searchByDCN)
		.exec(PaymentByDCN.searchAndAddFee)
		.exec(PaymentByDCN.paymentProcess)
		.exec(PaymentByDCN.PaymentProcessed)*/

	//Orders scenarios

	val addOrder_Scn = scenario("Add Order Scenario")
		.feed(caseNumber)
		.feed(Feeders.OrdersFeeder)
		.exec(IDAMHelper.getIdamToken)
		.exec(S2SHelper.S2SAuthToken)
		.exec(OrdersScenario.AddOrder)

	val createPayment_Scn = scenario("Create Payment Scenario")
		.feed(UUID)
		.feed(orderReferencesFeeder)
		.feed(Feeders.OrdersFeeder)
		.exec(IDAMHelper.getIdamToken)
		.exec(S2SHelper.S2SAuthToken)
		.exec(OrdersScenario.CreatePayment)

	val getOrder_Scn = scenario("Get Order Scenario")
		.feed(CPOCaseIdFeeder)
		.feed(Feeders.OrdersFeeder)
		.exec(IDAMHelper.getIdamToken)
		.exec(S2SHelper.S2SAuthToken)
		.exec(OrdersScenario.GetOrder)

  val createPaymentAndRefund = scenario("Create Payment and Refund E2E Scenario")
    .repeat(5) {
      feed(UUID)
      .feed(caseNumber)
      .feed(orderReferencesFeeder)
      .feed(Feeders.OrdersFeeder)
      .exec(IDAMHelper.getIdamToken)
      .exec(S2SHelper.S2SAuthToken)
      .exec(OrdersScenario.AddOrder)
      .exec(PaymentTransactionAPI.getPaymentByReference)
      .exec(OrdersScenario.CreatePayment)
      .feed(refundsUsersFeeder)
      .exec(IDAMHelper.refundsGetIdamToken)
      .exec(S2SHelper.RefundsS2SAuthToken)
      .exec(Refunds.submitRefund)
    }

  val approveRefund = scenario("Approve a Refund")
    .feed(refundIDsFeeder)
    .feed(refundsUsersFeeder)
    .exec(IDAMHelper.refundsGetIdamToken)
		.exec(S2SHelper.RefundsS2SAuthToken)
    .exec(Refunds.approveRefund)

	val rejectRefund = scenario("Reject a Refund")
		.feed(refundIDsFeeder)
		.feed(refundsUsersFeeder)
		.exec(IDAMHelper.refundsGetIdamToken)
		.exec(S2SHelper.RefundsS2SAuthToken)
		.exec(Refunds.rejectRefund)

  val getRefunds = scenario("Get All Refunds Scenario")
    .feed(refundsUsersFeeder)
    .exec(IDAMHelper.refundsGetIdamToken)
		.exec(S2SHelper.RefundsS2SAuthToken)
    .repeat(10) {
      exec(Refunds.getRefunds)
    }


	val Ways2PayCC_Scn = scenario("Way2Pay Credit Card Scenario ")
		.feed(feederViewCCDPayment).feed(Feeders.ViewPaymentsFeeder)
		.repeat(1) {//271
			exec(IDAMHelper.getIdamToken)
				.exec(S2SHelper.S2SAuthToken)
				.exec(Ways2Pay.ServiceRequest)
				.exec(Ways2Pay.W2PPBAPaymentsGET)
  			.exec(Ways2Pay.getPaymentGroupReferenceByCase)
				.exec(Ways2Pay.W2PCreditcardPayment)
				.exec(Ways2Pay.W2PCardPaymentStatusGET)
		}

	val Ways2PayPBA_Scn = scenario("Way2Pay Pay By Account Scenario ")
		.feed(feederViewCCDPayment).feed(Feeders.ViewPaymentsFeeder)
		.feed(UUID)
		.repeat(1) {//271
			exec(IDAMHelper.getIdamToken)
				.exec(S2SHelper.S2SAuthToken)
				.exec(Ways2Pay.ServiceRequest)
				.exec(Ways2Pay.W2PPBAPaymentsGET)
				.exec(Ways2Pay.getPaymentGroupReferenceByCase)
				.exec(Ways2Pay.W2PPBAPaymentsPOST)

		}
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

	/*setUp(
		CCDViewPayment_Scn.inject(rampUsers(1) during (10)),
		/*onlinePayment_Scn.inject(rampUsers(1) during (10)),
		bulkscan_Scn.inject(rampUsers(1) during (10)),
		PBA_Scn.inject(rampUsers(1) during (10)),
		telephony_Scn.inject(rampUsers(1) during (10))*/
		//onlineTelephony_Scn.inject(rampUsers(1) during (10))
	).protocols(httpProtocol)*/

	/*setUp(addOrder_Scn.inject(rampUsers(1) during (10)),
				createPayment_Scn.inject(rampUsers(1) during (10)),
				getOrder_Scn.inject(rampUsers(1) during (10))
			 ).protocols(httpProtocol)*/

	//setUp(datagendcn_Scn.inject(rampUsers(1) during (10))).protocols(bulkscanhttpProtocol)

	/*setUp(CCDViewPayment_Scn.inject(
		rampUsersPerSec(0.00) to (CCDViewPaymentRatePerSec) during (rampUpDurationMins minutes),
		constantUsersPerSec(CCDViewPaymentRatePerSec) during (testDurationMins minutes),
		rampUsersPerSec(CCDViewPaymentRatePerSec) to (0.00) during (rampDownDurationMins minutes)),

		onlinePayment_Scn.inject(rampUsersPerSec(0.00) to (onlinePaymentRatePerSec) during (rampUpDurationMins minutes),
			constantUsersPerSec(onlinePaymentRatePerSec) during (testDurationMins minutes),
			rampUsersPerSec(onlinePaymentRatePerSec) to (0.00) during (rampDownDurationMins minutes)),

		bulkscan_Scn.inject(rampUsersPerSec(0.00) to (bulkscanRatePerSec) during (rampUpDurationMins minutes),
			constantUsersPerSec(bulkscanRatePerSec) during (testDurationMins minutes),
			rampUsersPerSec(bulkscanRatePerSec) to (0.00) during (rampDownDurationMins minutes)),

		PBA_Scn.inject(rampUsersPerSec(0.00) to (PBARatePerSec) during (rampUpDurationMins minutes),
			constantUsersPerSec(PBARatePerSec) during (testDurationMins minutes),
			rampUsersPerSec(PBARatePerSec) to (0.00) during (rampDownDurationMins minutes)),

		telephony_Scn.inject(rampUsersPerSec(0.00) to (telephonyRatePerSec) during (rampUpDurationMins minutes),
			constantUsersPerSec(telephonyRatePerSec) during (testDurationMins minutes),
			rampUsersPerSec(telephonyRatePerSec) to (0.00) during (rampDownDurationMins minutes))
	)
		.protocols(httpProtocol)*/

  //************** Main Scenario for perf tests **************//

	/*setUp(
    addOrder_Scn.inject(
      rampUsersPerSec(0.00) to (addOrderRatePerSec) during (rampUpDurationMins minutes),
      constantUsersPerSec(addOrderRatePerSec) during (testDurationMins minutes),
      rampUsersPerSec(addOrderRatePerSec) to (0.00) during (rampDownDurationMins minutes)),

		createPayment_Scn.inject(rampUsersPerSec(0.00) to (createPaymentRatePerSec) during (rampUpDurationMins minutes),
			constantUsersPerSec(createPaymentRatePerSec) during (testDurationMins minutes),
			rampUsersPerSec(createPaymentRatePerSec) to (0.00) during (rampDownDurationMins minutes)),

		getOrder_Scn.inject(rampUsersPerSec(0.00) to (getOrderRatePerSec) during (rampUpDurationMins minutes),
			constantUsersPerSec(getOrderRatePerSec) during (testDurationMins minutes),
			rampUsersPerSec(getOrderRatePerSec) to (0.00) during (rampDownDurationMins minutes))

    createPaymentAndRefund.inject(rampUsersPerSec(0.00) to (getOrderRatePerSec) during (rampUpDurationMins minutes),
			constantUsersPerSec(getOrderRatePerSec) during (testDurationMins minutes),
			rampUsersPerSec(getOrderRatePerSec) to (0.00) during (rampDownDurationMins minutes))
	)
		.protocols(httpProtocol)
*/

 // setUp(addOrder_Scn.inject(rampUsers(10) during (1 minutes))).protocols(httpProtocol)
	//Get Payment History - to be created and added
	//create payment needs to be updated the new calls PBA and Credit Card
	//setUp(createPayment_Scn .inject(rampUsers(2) during (1 minutes))).protocols(httpProtocol)
	//		setUp(getOrder_Scn.inject(rampUsers(1) during (1 minutes))).protocols(httpProtocol)

	//Ways to Pay
//	setUp(Ways2PayCC_Scn.inject(rampUsers(1) during (1 minutes))).protocols(httpProtocol)
	setUp(Ways2PayPBA_Scn.inject(rampUsers(1) during (1 minutes))).protocols(httpProtocol)

}
