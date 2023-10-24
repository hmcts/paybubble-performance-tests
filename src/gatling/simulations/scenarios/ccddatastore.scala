package scenarios

import io.gatling.core.Predef._
import io.gatling.http.Predef._
import utils._
import java.io.{BufferedWriter, FileWriter}

object ccddatastore {

  val baseURL = Environment.baseURL

  val CCDAPI_DivorceSolicitorCreate =

    exec(http("API_Divorce_GetEventToken")
      .get(Environment.ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/DIVORCE/case-types/DIVORCE/event-triggers/solicitorCreate/token")
      .header("ServiceAuthorization", "Bearer #{ccdS2SToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .check(jsonPath("$.token").saveAs("eventToken")))

    .exec(http("API_Divorce_SolCreateCase")
      .post(Environment.ccdDataStoreUrl + "/caseworkers/#{idamId}/jurisdictions/DIVORCE/case-types/DIVORCE/cases")
      .header("ServiceAuthorization", "Bearer #{ccdS2SToken}")
      .header("Authorization", "Bearer #{access_token}")
      .header("Content-Type","application/json")
      .body(ElFileBody("CCD_DivorceCreateSol.json"))
      .check(jsonPath("$.id").saveAs("caseid"))
      .check(status.saveAs("statusvalue")))

    /*//Outputs the Case ID CSV, can be commented out if not needed  
    // .doIf(session=>session("statusvalue").as[String].contains("200")) {
      .exec {
        session =>
          val fw = new BufferedWriter(new FileWriter("CCDCaseIDs.csv", true))
          try {
            fw.write(session("caseid").as[String] + "\r\n")
          }
          finally fw.close()
          session
      }
    // }*/
}