package simulations

// import io.gatling.core.Predef._
// import io.gatling.http.Predef._
// import scenarios._
// import util._
// import com.typesafe.config.{Config, ConfigFactory}
// import io.gatling.core.controller.inject.open.OpenInjectionStep
// import io.gatling.commons.stats.assertion.Assertion
// import io.gatling.core.pause.PauseType

// import scala.concurrent.duration._
// import scala.util.Random
// import scala.io.Source

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import scenarios._
import utils._

import scala.io.Source
import io.gatling.core.controller.inject.open.OpenInjectionStep
import io.gatling.commons.stats.assertion.Assertion
import io.gatling.core.pause.PauseType

import scala.concurrent.duration._
import scala.util.Random

class CCPaybubbleSCN extends Simulation {

  /* TEST TYPE DEFINITION */
  /* pipeline = nightly pipeline against the perftest/AAT environment (configure the Jenkins_nightly file) */
  /* perftest (default) = performance test against the perftest environment */
  val testType = scala.util.Properties.envOrElse("TEST_TYPE", "perftest")

  //set the environment based on the test type
  val environment = testType match{
    case "perftest" => "perftest"
    case "pipeline" => "perftest" //define whether to run the Jenkins nightly pipeline against perftest or aat
    case _ => "**INVALID**"
  }
  /* ******************************** */

  /* ADDITIONAL COMMAND LINE ARGUMENT OPTIONS */
  val debugMode = System.getProperty("debug", "off") //runs a single user e.g. ./gradle gatlingRun -Ddebug=on (default: off)
  val env = System.getProperty("env", environment) //manually override the environment aat|perftest e.g. ./gradle gatlingRun -Denv=aat
  /* ******************************** */

  /* PERFORMANCE TEST CONFIGURATION */
  val rampUpDurationMins = 5
  val rampDownDurationMins = 5
  val testDurationMins = 60

  val viewPaymentTarget:Double = 10
  val onlinePaymentTarget:Double = 10
  val bulkscanTarget:Double = 10
  val pbaTarget:Double = 10
  val telephonyTarget:Double = 10
  val onlineTarget:Double = 10

  //If running in debug mode, disable pauses between steps
  val pauseOption:PauseType = debugMode match{
    case "off" => constantPauses
    case _ => disabledPauses
  }
  /* ******************************** */

  /* PIPELINE CONFIGURATION */
  val numberOfPipelineUsers:Double = 10
  /* ******************************** */

  val httpProtocol = http
    .baseUrl(Environment.baseURL)
    .doNotTrackHeader("1")
    .inferHtmlResources()
    .silentResources

  before{
    println(s"Test Type: ${testType}")
    println(s"Test Environment: ${env}")
    println(s"Debug Mode: ${debugMode}")
  }

	val feeder =jsonFile("datagenforbulkscan.json").circular
	val feederonline =jsonFile("dataonlinepayment.json").circular
	val feederbulkscan =jsonFile("databulkscanpayments.json").circular
	val feedertelephone =jsonFile("datatelephonepayments.json").circular
	val feederpba =jsonFile("dataPBA.json").circular
	val feederViewCCDPayment =jsonFile("dataccdviewpayment.json").circular
	val onlineTelephonyFeeder = jsonFile("onlinetelephony.json").circular
	val onlineTelephonyCaseFeeder = csv("onlinetelephonycaseids.csv").circular
	val usersFeeder = csv("users.csv").circular

	val telephonyScn = scenario("Offline Telephony Payments Scenario")
    .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
      .feed(feedertelephone)
      // .feed(Feeders.TelephoneFeeder)
      .repeat(1) {//40
        exec(IDAMHelper.getIdamTokenLatest)
        .exec(S2SHelper.S2SAuthToken)
        .exec(PaymentTransactionAPI.getPaymentGroupReference)
        .exec(PaymentTransactionAPI.telephony)
      }
    }

	val onlineTelephony_Scn = scenario("Online Telephony Payments Scenario")
	  .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
      .feed(onlineTelephonyFeeder)
      .feed(usersFeeder)
      .feed(onlineTelephonyCaseFeeder)
      // .feed(Feeders.OnlineTelephonyFeeder)
      .exec(PayBubbleLogin.homePage)
      .exec(PayBubbleLogin.login)
      .exec(OnlineTelephonyScenario.onlineTelephonyScenario)
      .exec(PayBubbleLogin.logout)
	}

	val bulkscan_Scn = scenario("Offline Bulkscan Payments Scenario")
    .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
  		.feed(feederbulkscan).feed(Feeders.BulkscanFeeder)
      .exec(IDAMHelper.getIdamTokenLatest)
      .exec(S2SHelper.S2SAuthToken)
      .exec(PaymentTransactionAPI.getPaymentGroupReference)
      .exec(PaymentTransactionAPI.bulkscan)
      .exec(PaymentTransactionAPI.paymentAllocations)
    }

	val onlinePayment_Scn = scenario("Online Payments Scenario")
    .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
  		.feed(feederonline).feed(Feeders.OnlinePaymentFeeder)
			.exec(IDAMHelper.getIdamTokenLatest)
			.exec(S2SHelper.S2SAuthToken)
			.exec(PaymentTransactionAPI.onlinePayment)
    }

	val PBA_Scn = scenario("Pay By Account Scenario")
    .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
  		.feed(feederpba).feed(Feeders.PBAFeeder)
			.exec(IDAMHelper.getIdamTokenLatest)
			.exec(S2SHelper.S2SAuthToken)
			.exec(PaymentTransactionAPI.PBA)
    }

	val CCDViewPayment_Scn = scenario("View Payments Scenario")
    .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
  		.feed(feederViewCCDPayment)
      // .feed(Feeders.ViewPaymentsFeeder)
			.exec(IDAMHelper.getIdamTokenLatest)
			.exec(S2SHelper.S2SAuthToken)
			.exec(PaymentTransactionAPI.getPaymentReferenceByCase)
			.exec(PaymentTransactionAPI.ccdViewPayment)
    }
  
  //defines the Gatling simulation model, based on the inputs
  def simulationProfile(simulationType: String, userPerSecRate: Double, numberOfPipelineUsers: Double): Seq[OpenInjectionStep] = {
    simulationType match {
      case "perftest" =>
        if (debugMode == "off") {
          Seq(
            rampUsersPerSec(0.00) to (userPerSecRate) during (rampUpDurationMins.minutes),
            constantUsersPerSec(userPerSecRate) during (testDurationMins.minutes),
            rampUsersPerSec(userPerSecRate) to (0.00) during (rampDownDurationMins.minutes)
          )
        }
        else{
          Seq(atOnceUsers(1))
        }
      case "pipeline" =>
        Seq(rampUsers(numberOfPipelineUsers.toInt) during (2.minutes))
      case _ =>
        Seq(nothingFor(0))
    }
  }

  //defines the test assertions, based on the test type
  def assertions(simulationType: String): Seq[Assertion] = {
    simulationType match {
      case "perftest" =>
        Seq(global.successfulRequests.percent.gte(95))
      case "pipeline" =>
        Seq(global.successfulRequests.percent.gte(95),
        )
      case _ =>
        Seq()
    }
  }

setUp(
    CCDViewPayment_Scn.inject(simulationProfile(testType, viewPaymentTarget, numberOfPipelineUsers)).pauses(pauseOption),
    onlinePayment_Scn.inject(simulationProfile(testType, onlinePaymentTarget, numberOfPipelineUsers)).pauses(pauseOption),
    bulkscan_Scn.inject(simulationProfile(testType, bulkscanTarget, numberOfPipelineUsers)).pauses(pauseOption),
    PBA_Scn.inject(simulationProfile(testType, pbaTarget, numberOfPipelineUsers)).pauses(pauseOption),
    telephonyScn.inject(simulationProfile(testType, telephonyTarget, numberOfPipelineUsers)).pauses(pauseOption),
    onlineTelephony_Scn.inject(simulationProfile(testType, onlineTarget, numberOfPipelineUsers)).pauses(pauseOption),
  ).protocols(httpProtocol)
  .assertions(assertions(testType))

}
