package uk.gov.hmcts.paybubble.scenario

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import uk.gov.hmcts.paybubble.util._

object Notifications {

  /* NOTIFICATIONS */


  /*get notifications.  This request will return all notifications that have been sent for an existing refund.
  * A refund must be created prior to running this request and stored in session param ${refundId} */

  val getNotifications =

    group("Notifications") {
      exec(http("GET_Notifications")
        .get(Environment.notificationsUrl + "/notifications/${refundId}")
        .header("Authorization", "Bearer ${accessTokenRefund}")
        .header("ServiceAuthorization", "${s2sTokenRefund}")
        .header("Content-Type", "application/json")
        .check(status in (200, 404))
        .check(regex("Notification has not been sent for this refund").optional.saveAs("noNotificationSent")))
    }

  /*post notification preview.  This request will create a document preview for a notification for an existing refund.
    A refund must be created prior to running this request and stored in session param ${refundId} */

  val notificationsDocPreview =

    group("Notifications") {
       randomSwitch(50d -> exec(_.set("notificationType", "EMAIL")),
                               50d -> exec(_.set("notificationType", "LETTER")))
      .exec(http("POST_DocPreview")
        .post(Environment.notificationsUrl + "/notifications/doc-preview")
        .header("Authorization", "Bearer ${accessTokenRefund}")
        .header("ServiceAuthorization", "${s2sTokenRefund}")
        .header("Content-Type", "application/json")
        .body(ElFileBody("RefundV2Data/notificationsDocPreview.json"))
        .check(jsonPath("$.template_id").saveAs("templateId")))
    }

  /*post notification email. This request will create a notification for an existing refund via email.
      A refund must be created prior to running this request and stored in session param ${refundId}
      Other session variables are extracted from notificationsData.csv and used in notificationsEmail.json*/

  val notificationsEmail =

    group("Notifications") {
        exec(http("POST_NotificationsEmail")
          .post(Environment.notificationsUrl + "/notifications/email")
          .header("Authorization", "Bearer ${accessTokenRefund}")
          .header("ServiceAuthorization", "${s2sTokenRefund}")
          .header("Content-Type", "application/json")
          .body(ElFileBody("RefundV2Data/notificationsEmail.json"))
          .check(regex("Notification sent successfully via email").saveAs("emailNotification")))
    }


  /*post notification letter. This request will create a notification for an existing refund via letter.
       A refund must be created prior to running this request and stored in session param ${refundId}
       Other session variables are extracted from notificationsData.csv and used in notificationsLetter.json*/

  val notificationsLetter =

    group("Notifications") {
        exec(http("POST_NotificationsLetter")
          .post(Environment.notificationsUrl + "/notifications/letter")
          .header("Authorization", "Bearer ${accessTokenRefund}")
          .header("ServiceAuthorization", "${s2sTokenRefund}")
          .header("Content-Type", "application/json")
          .body(ElFileBody("RefundV2Data/notificationsLetter.json"))
          .check(regex("Notification sent successfully via letter").saveAs("letterNotification")))
    }


  val notificationPostcode =

    group("Notifications") {
      exec(http("GET_Postcode")
        .get(Environment.notificationsUrl + "/notifications/postcode-lookup/${postcode}")
        .header("Authorization", "Bearer ${accessTokenRefund}")
        .header("ServiceAuthorization", "${s2sTokenRefund}")
        .header("Content-Type", "application/json")
        .check(jsonPath("$.results[0].DPA.ADDRESS").saveAs("postcodeAddress")))
    }


}
