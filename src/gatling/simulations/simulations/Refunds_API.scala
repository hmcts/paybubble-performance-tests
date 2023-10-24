package simulations

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

class Refunds_API extends Simulation {

  val dmBaseURL = Environment.refundsUrl

  /* TEST TYPE DEFINITION */
  /* pipeline = nightly pipeline against the AAT environment (see the Jenkins_nightly file) */
  /* perftest (default) = performance test against the perftest environment */
  val testType = scala.util.Properties.envOrElse("TEST_TYPE", "perftest")

  //set the environment based on the test type
  val environment = testType match {
    case "perftest" => "perftest"
    //case "pipeline" => "perftest" //updated pipeline to run against perftest - change to aat to run against AAT
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

  /*Hourly Volumes for Get Refund*/
  val GetRefundHourlyTarget: Double = 100
  val GetNotificationHourlyTarget: Double = 100
  val ReprocessNotificationsHourlyTarget: Double = 2

  /*Rate Per Second Volume for Share Case Requests */
  val GetRefundRatePerSec = GetRefundHourlyTarget / 3600
  val GetNotificationRatePerSec = GetNotificationHourlyTarget / 3600
  val ReprocessNotificationsRatePerSec = ReprocessNotificationsHourlyTarget / 3600

  /* PIPELINE CONFIGURATION */
  val numberOfPipelineUsers = 1

  /* SIMULATION FEEDER FILES */
  val refundUsers = csv("RefundUsers.csv").circular
  val paymentsForRefunds = csv("RefundV2Data/refundData.csv")
  val refundAdminUsers = csv("RefundV2Data/refundAdminUsers.csv").circular
  val notificationData = csv("RefundV2Data/notificationData.csv")
  val notificationPostcodeData = csv("RefundV2Data/notificationPostcodes.csv").random
  //val NOCAPIFeeder = csv("noticeOfChangeAPI.csv")

  //If running in debug mode, disable pauses between steps
  val pauseOption: PauseType = debugMode match {
    case "off" => constantPauses
    case _ => disabledPauses
  }
  /* ******************************** */
  //  /* PIPELINE CONFIGURATION */
  //  val numberOfPipelineUsersSole:Double = 5
  //  val numberOfPipelineUsersJoint:Double = 5
  /* ******************************** */

  val httpProtocol = Environment.HttpProtocol
    .baseUrl(dmBaseURL)
    .doNotTrackHeader("1")
    .inferHtmlResources()
    .silentResources

  before {
    println(s"Test Type: ${testType}")
    println(s"Test Environment: ${env}")
    println(s"Debug Mode: ${debugMode}")
  }

  //defines the Gatling simulation model, based on the inputs
  def simulationProfile(simulationType: String, userPerSecRate: Double, numberOfPipelineUsers: Double): Seq[OpenInjectionStep] = {
    simulationType match {
      case "perftest" =>
        if (debugMode == "off") {
          Seq(
            rampUsersPerSec(0.00) to (userPerSecRate) during (rampUpDurationMins minutes),
            constantUsersPerSec(userPerSecRate) during (testDurationMins minutes),
            rampUsersPerSec(userPerSecRate) to (0.00) during (rampDownDurationMins minutes)
          )
        }
        else {
          Seq(atOnceUsers(1))
        }
//      case "pipeline" =>
//        Seq(rampUsers(numberOfPipelineUsers.toInt) during (2 minutes))
      case _ =>
        Seq(nothingFor(0))
    }
  }

  //defines the test assertions, based on the test type
  def assertions(simulationType: String): Seq[Assertion] = {
    simulationType match {
      case "perftest" =>
        Seq(global.successfulRequests.percent.gte(95))
//      case "pipeline" =>
//        Seq(global.successfulRequests.percent.gte(95))
      case _ =>
        Seq()
    }
  }

  val ScnRefunds = scenario("Refunds")
    .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
        .feed(paymentsForRefunds)
        .feed(refundUsers)
        .feed(refundAdminUsers)
        //login as caseworker
        .exec(S2SHelper.RefundsS2SAuthToken)
        .exec(IDAMHelper.refundsGetIdamToken)
        .exec(RefundsV2.submitRefund)
        .pause(5)
//        //login as approver
        .exec(S2SHelper.RefundsS2SAuthToken)
        .exec(IDAMHelper.refundsGetAdminIdamToken)
        .exec(RefundsV2.reviewerActionRefund)
        .pause(5)
//        //login as caseworker
        .exec(S2SHelper.RefundsS2SAuthToken)
        .exec(IDAMHelper.refundsGetIdamToken)
        .exec(RefundsV2.resubmitRefund)
        .pause(5)
//        //login as approver
        .exec(S2SHelper.RefundsS2SAuthToken)
        .exec(IDAMHelper.refundsGetAdminIdamToken)
        .exec(RefundsV2.approveRefund)
        .pause(5)
        //login as caseworker
        .exec(S2SHelper.RefundsS2SAuthToken)
        .exec(IDAMHelper.refundsGetIdamToken)
        .exec(RefundsV2.getRefund)
        .exec(RefundsV2.getRefundStatusHistory)
        .exec(RefundsV2.getRefunds)
        .exec(RefundsV2.getRefundActions)
        .exec(RefundsV2.getRefundReasons)
        .exec(RefundsV2.getRefundRejectionReasons)
        .exec(RefundsV2.resendNotifications)
        .exec(RefundsV2.cancelRefund)
        .exec(RefundsV2.deleteRefund)
    }

  /* this scenario will test the notifications apis. */

  val ScnNotifications = scenario("Notifications")
    .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
        .feed(refundUsers)
        .feed(notificationData)
        .feed(notificationPostcodeData)
        .exec(S2SHelper.RefundsS2SAuthToken)
        .exec(IDAMHelper.refundsGetIdamToken)
        .exec(RefundsV2.submitRefund)
        .exec(Notifications.notificationPostcode)
        .exec(Notifications.notificationsDocPreview)
        .exec(Notifications.notificationsEmail)
        .exec(Notifications.notificationsLetter)
        .exec(Notifications.getNotifications)
        .exec(RefundsV2.cancelRefund)
        .exec(RefundsV2.deleteRefund)
    }

  /* this scenario will test the reprocessing of any failed notifications.
     the endpoint will get hit every 30 minutes and is effectively a batch job */

  val ScnReprocessFailedNotifications = scenario("ReprocessFailedNotifications")
    .exitBlockOnFail {
      exec(_.set("env", s"${env}"))
        .feed(refundUsers)
        .exec(S2SHelper.RefundsS2SAuthToken)
        .exec(IDAMHelper.refundsGetIdamToken)
        .exec(RefundsV2.reprocessFailedNotifications)
    }

  /*Refund and Notification Simulations */
  setUp(
    ScnRefunds.inject(simulationProfile(testType, GetRefundRatePerSec, numberOfPipelineUsers)).pauses(pauseOption), //Needs a working user, returns a 409 for first request
    ScnNotifications.inject(simulationProfile(testType, GetNotificationRatePerSec, numberOfPipelineUsers)).pauses(pauseOption), //Needs a working user, returns a 409 for first request
    ScnReprocessFailedNotifications.inject(simulationProfile(testType, ReprocessNotificationsRatePerSec, numberOfPipelineUsers)).pauses(pauseOption) //****Working****
  ).protocols(httpProtocol)
    .assertions(assertions(testType))
}