package utils

import com.typesafe.config.{Config, ConfigFactory}
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils._

object IDAMHelper {

 /* private val USERNAME = "testytesttest@test.net"
  private val PASSWORD = "4590fgvhbfgbDdffm3lk4j"*/
// private val USERNAME = "james@swansea.gov.uk"
//  private val PASSWORD = "Password123"
  private val USERNAME = "ccdloadtest1@gmail.com"
  private val PASSWORD = "Password12"
// private val USERNAME = "emshowcase@hmcts.net"
 // private val PASSWORD = "4590fgvhbfgbDdffm3lk4j"
  // below are for aat
/*private val USERNAME = "bundle-tester--518511189@gmail.com"
  private val PASSWORD = "4590fgvhbfgbDdffm3lk4j"*/

  val config: Config = ConfigFactory.load()
  val IDAM_OAUTH_SECRET = config.getString("auth.clientSecret")

  val getIdamToken =
    exec(http("010_GetAuthToken")
        // .post(Environment.idamURL  + "/o/token?client_id=" + OAUTH_CLIENT + "&client_secret=" + IDAM_OAUTH_SECRET + "&grant_type=password&scope=openid profile roles search-user&username=kishanki@gmail.com&password=LevelAt12")
          .post(Environment.idamURL  + "/o/token?client_id=" + Environment.OAUTH_CLIENT + "&client_secret=" + IDAM_OAUTH_SECRET + "&grant_type=password&scope=openid profile roles search-user&username=perftestways2pay@mailnesia.com&password=LevelAt12")
          .header("Content-Type", "application/x-www-form-urlencoded")
         .header("Content-Length", "0")
         .check(status is 200)
         .check(jsonPath("$.access_token").saveAs("accessToken")))

  val refundsGetIdamToken =
    exec(http("PaymentAPIToken_010_GetAuthToken")
      .post(Environment.idamURL  + "/o/token?client_id=" + Environment.OAUTH_CLIENT + "&client_secret=" + IDAM_OAUTH_SECRET + "&grant_type=password&scope=openid profile roles search-user&username=#{email}&password=#{password}")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Content-Length", "0")
      .check(status is 200)
      .check(jsonPath("$.access_token").saveAs("accessTokenRefund")))

  val refundsGetAdminIdamToken =
    exec(http("PaymentAPIToken_010_GetAuthToken")
      .post(Environment.idamURL + "/o/token?client_id=" + Environment.OAUTH_CLIENT + "&client_secret=" + IDAM_OAUTH_SECRET + "&grant_type=password&scope=openid profile roles search-user&username=#{adminEmail}&password=#{adminPassword}")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Content-Length", "0")
      .check(status is 200)
      .check(jsonPath("$.access_token").saveAs("adminAccessTokenRefund")))

  //following is a tested method

  val getIdamTokenLatest =
    exec(http("PaymentAPI#{service}_010_015_GetAuthToken")
      .post(Environment.IDAM_API_BASE_URI + "/o/token?client_id=" + Environment.OAUTH_CLIENT + "&client_secret=" + IDAM_OAUTH_SECRET + "&grant_type=password&scope=openid&username=kishanki@gmail.com&password=LevelAt12")
      .header("Content-Type", "application/x-www-form-urlencoded")
      .header("Content-Length", "0")
      .check(status is 200)
      .check(jsonPath("$.access_token").saveAs("accessToken")))






}
