package utils

import com.typesafe.config.ConfigFactory
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import io.gatling.core.Predef._
import io.gatling.http.Predef._

object Environment {

 val environment: String = System.getProperty("env")
 val idamURL = "https://idam-web-public.#{env}.platform.hmcts.net"
 val baseURL = "https://paybubble.#{env}.platform.hmcts.net"
 val bulkScanURL = "http://ccpay-bulkscanning-api-#{env}.service.core-compute-#{env}.internal"
 val paymentAPIURL = "http://payment-api-#{env}.service.core-compute-#{env}.internal"
 val PCIPALURL = "https://euwest1.pcipalstaging.cloud"
 val adminUserAO = ""
 val adminPasswordAO = ""
 val IDAM_API_BASE_URI = "https://idam-api.#{env}.platform.hmcts.net"
 val IDAM_AUTH_REDIRECT = "https://paybubble.#{env}.platform.hmcts.net/oauth2/callback"
 val OAUTH_CLIENT = "paybubble" //am_role_assignment //paybubble
 val S2S_BASE_URL = "http://rpe-service-auth-provider-#{env}.service.core-compute-#{env}.internal/testing-support"
 val DM_STORE_API_BASE_URI = "http://dm-store-#{env}.service.core-compute-#{env}.internal"
 val refundsUrl = "http://ccpay-refunds-api-#{env}.service.core-compute-#{env}.internal"
 val notificationsUrl = "http://ccpay-notifications-service-#{env}.service.core-compute-#{env}.internal"
 val S2S_SERVICE_NAME = "ccpay_bubble"
// val S2S_SERVICE_NAME = "probate_frontend" //am_role_assignment_service //probate_frontend //payment_app
 val IDAM_OAUTH_SECRET = "" //to be obtained from vault
 val FUNCTIONAL_TEST_CLIENT_S2S_TOKEN = ""

 val thinkTime = 15

 val minThinkTime = 5
 //10
 val maxThinkTime = 6
 //30

 val HttpProtocol = http

 val commonHeader = Map(
  "accept" -> "image/webp,image/apng,image/*,*/*;q=0.8",
  "accept-encoding" -> "gzip, deflate, br",
  "accept-language" -> "en-US,en;q=0.9",
  "sec-fetch-dest" -> "image",
  "sec-fetch-mode" -> "no-cors",
  "sec-fetch-site" -> "same-origin")

 val pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd")
 val current_date = LocalDate.now().format(pattern)

}

