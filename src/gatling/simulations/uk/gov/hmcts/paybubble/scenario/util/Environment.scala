package uk.gov.hmcts.paybubble.scenario.util

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import com.typesafe.config.{Config, ConfigFactory}

object Environment {

  val config: Config = ConfigFactory.load()

  val environment: String = System.getProperty("env")
  val idamURL = "https://idam-web-public.aat.platform.hmcts.net"
  val baseURL = "https://paybubble.aat.platform.hmcts.net"
  val bulkScanURL="http://ccpay-bulkscanning-api-aat.service.core-compute-aat.internal"
  val paymentAPIURL="http://payment-api-aat.service.core-compute-aat.internal"
  val adminUserAO = ""
  val adminPasswordAO = ""
  val IDAM_API_BASE_URI = "https://idam-api.aat.platform.hmcts.net"
  val IDAM_AUTH_REDIRECT = "https://paybubble.aat.platform.hmcts.net/oauth2/callback"
  val OAUTH_CLIENT = "paybubble"//am_role_assignment
  val FUNCTIONAL_TEST_CLIENT_OAUTH_SECRET = "evidence-management-show"
  val S2S_BASE_URI = "http://rpe-service-auth-provider-aat.service.core-compute-aat.internal/testing-support"
  val DM_STORE_API_BASE_URI = "http://dm-store-aat.service.core-compute-aat.internal"
  //val S2S_SERVICE_NAME = "api_gw"
  val S2S_SERVICE_NAME = "probate_frontend" //am_role_assignment_service
  val IDAM_OAUTH_SECRET = config.getString("IDAM_OAUTH_SECRET")
  val FUNCTIONAL_TEST_CLIENT_S2S_TOKEN = config.getString("FUNCTIONAL_TEST_CLIENT_S2S_TOKEN") //PVHJTPD552HUVO5G

  val thinkTime = 1

  val minThinkTime = 5
  //10
  val maxThinkTime = 6
  //30

  val commonHeader = Map(
    "accept" -> "image/webp,image/apng,image/*,*/*;q=0.8",
    "accept-encoding" -> "gzip, deflate, br",
    "accept-language" -> "en-US,en;q=0.9",
    "sec-fetch-dest" -> "image",
    "sec-fetch-mode" -> "no-cors",
    "sec-fetch-site" -> "same-origin")
}
