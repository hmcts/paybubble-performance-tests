package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils.{Common, Environment}

object RefundsV2 {

  // submit refund

  val submitRefund =

    group("Refunds") {
      exec(http("POST_SubmitRefund")
        .post(Environment.refundsUrl + "/refund")
        .header("Authorization", "Bearer #{accessTokenRefund}")
        .header("ServiceAuthorization", "#{s2sTokenRefund}")
        .header("Content-Type", "application/json")
        .body(ElFileBody("RefundV2Data/submitRefundRequest.json"))
        .check(jsonPath("$.refund_reference").saveAs("refundId")))
    }

  //refund reviewer action - send back refund to case worker
  val reviewerActionRefund =

    group("Refunds") {
      exec(http("PATCH_ReviewerActionRefund")
        .patch(Environment.refundsUrl + "/refund/#{refundId}/action/SENDBACK")
        .header("Authorization", "Bearer #{adminAccessTokenRefund}")
        .header("ServiceAuthorization", "#{s2sTokenRefund}")
        .header("Content-Type", "application/json")
        .body(ElFileBody("RefundV2Data/sendBackRefundRequest.json")))
    }

  //resubmit the refund with a new value
  val resubmitRefund =

    group("Refunds") {
      exec(http("PATCH_ResubmitRefunds")
        .patch(Environment.refundsUrl + "/refund/resubmit/#{refundId}")
        .header("Authorization", "Bearer #{accessTokenRefund}")
        .header("ServiceAuthorization", "#{s2sTokenRefund}")
        .header("Content-Type", "application/json")
        .body(ElFileBody("RefundV2Data/resubmitRefund.json")))
    }

  //approve refund
  val approveRefund =

    group("Refunds") {
      exec(http("PATCH_ApproveRefund")
        .patch(Environment.refundsUrl + "/refund/#{refundId}/action/APPROVE")
        .header("Authorization", "Bearer #{adminAccessTokenRefund}")
        .header("ServiceAuthorization", "#{s2sTokenRefund}")
        .header("Content-Type", "application/json")
        .body(ElFileBody("RefundV2Data/sendBackRefundRequest.json")))
    }

  //reject refund
  val rejectRefund =

    group("Refunds") {
      exec(http("PATCH_RejectRefund")
        .patch(Environment.refundsUrl + "/refund/#{refundId}/action/REJECT")
        .header("Authorization", "Bearer #{accessTokenRefund}")
        .header("ServiceAuthorization", "#{s2sTokenRefund}")
        .header("Content-Type", "application/json")
        .formParam("reference", "#{refundId}")
        .formParam("reviewer-action", "REJECT")
        .body(ElFileBody("RejectRefundRequest.json")))
    }

  //cancel refund
  val cancelRefund =

    group("Refunds") {
      exec(http("PATCH_CancelRefund")
        .patch(Environment.refundsUrl + "/payment/#{reference}/action/cancel")
        .header("Authorization", "Bearer #{accessTokenRefund}")
        .header("ServiceAuthorization", "#{s2sTokenRefund}")
        .header("Content-Type", "application/json")
        .check(regex("Refund cancelled")))
    }

  //delete refund
  val deleteRefund =

    group("Refunds") {
      exec(http("DELETE_DeleteRefund")
        .delete(Environment.refundsUrl + "/refund/#{refundId}")
        .header("Authorization", "Bearer #{accessTokenRefund}")
        .header("ServiceAuthorization", "#{s2sTokenRefund}")
        .header("Content-Type", "application/json"))
    }

  val getRefund =

    group("Refunds") {
      exec(http("GET_Refund")
        .get(Environment.refundsUrl + "/refund")
        .header("Authorization", "Bearer #{accessTokenRefund}")
        .header("ServiceAuthorization", "#{s2sTokenRefund}")
        .header("Content-Type", "application/json")
        .queryParam("status", "Sent for approval")
        .queryParam("ccdCaseNumber", "#{ccd_case_number}")
        .check(jsonPath("$.refund_list[0].ccd_case_number").is("#{ccd_case_number}")))
    }

  //get refunds
  val getRefunds =

    exec(_.setAll(
      "refundStartDate" -> Common.currentDateMinus1Day(),
      "refundEndDate" -> Common.currentDate()
    ))

    .group("Refunds") {  
      exec(http("GET_Refunds")
      .get(Environment.refundsUrl + "/refunds")
      .header("Authorization", "Bearer #{accessTokenRefund}")
      .header("ServiceAuthorization", "#{s2sTokenRefund}")
      .header("Content-Type", "application/json")
      .queryParam("end_date", "#{refundEndDate}")
      .queryParam("start_date", "#{refundStartDate}")
      .queryParam("refund_reference", "#{reference}")
      .check(jsonPath("$.refunds[0].payment.reference").saveAs("getRefundsResponse")))
    }

  // get status history
  val getRefundStatusHistory =

    group("Refunds") {
      exec(http("GET_RefundStatusHistory")
        .get(Environment.refundsUrl + "/refund/#{refundId}/status-history")
        .header("Authorization", "Bearer #{accessTokenRefund}")
        .header("ServiceAuthorization", "#{s2sTokenRefund}")
        .header("Content-Type", "application/json")
        .check(jsonPath("$.status_history_dto_list[0].id").saveAs("statusHistoryItem")))
    }

  // get status history
  val reprocessFailedNotifications =

    group("Refunds") {
      exec(http("PATCH_ReprocessFailedNotifications")
        .patch(Environment.refundsUrl + "/jobs/refund-notification-update")
        .header("Authorization", "Bearer #{accessTokenRefund}")
        .header("ServiceAuthorization", "#{s2sTokenRefund}")
        .header("Content-Type", "application/json"))
    }

  val getRefundActions =

    group("Refunds") {
      exec(http("GET_RefundActions")
        .get(Environment.refundsUrl + "/refund/#{refundId}/actions")
        .header("Authorization", "Bearer #{accessTokenRefund}")
        .header("ServiceAuthorization", "#{s2sTokenRefund}")
        .header("Content-Type", "application/json")
        .check(jsonPath("$[0].code").saveAs("refundAction")))
    }

  val getRefundReasons =

    group("Refunds") {
      exec(http("GET_RefundReasons")
        .get(Environment.refundsUrl + "/refund/reasons")
        .header("Authorization", "Bearer #{accessTokenRefund}")
        .header("ServiceAuthorization", "#{s2sTokenRefund}")
        .header("Content-Type", "application/json")
        .check(jsonPath("$[0].code").saveAs("refundReasons")))
    }

  val getRefundRejectionReasons =

    group("Refunds") {
      exec(http("GET_RefundRejectionReasons")
        .get(Environment.refundsUrl + "/refund/rejection-reasons")
        .header("Authorization", "Bearer #{accessTokenRefund}")
        .header("ServiceAuthorization", "#{s2sTokenRefund}")
        .header("Content-Type", "application/json")
        .check(jsonPath("$[0].code").saveAs("refundRejectionReasons")))
    }

  val resendNotifications =

    group("Refunds") {
      exec(http("PUT_ResendNotifications")
        .put(Environment.refundsUrl + "/refund/resend/notification/#{refundId}")
        .header("Authorization", "Bearer #{accessTokenRefund}")
        .header("ServiceAuthorization", "#{s2sTokenRefund}")
        .header("Content-Type", "application/json")
        .queryParam("notificationType", "LETTER")
        .body(ElFileBody("RefundV2Data/resendNotifications.json")))
      }
}