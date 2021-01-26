package uk.gov.hmcts.paybubble.util;

import java.util.Properties;
import com.typesafe.config.ConfigFactory;

public class Env {
    private static String file = "CV-CMC-GOR-ENG-0004-UI-Test.docx";
    static Properties defaults = new Properties();
    static {
        defaults.setProperty("IDAM_API_BASE_URI", "https://idam-web-public.perftest.platform.hmcts.net");
        defaults.setProperty("IDAM_AUTH_REDIRECT", "https://paybubble.perftest.platform.hmcts.net/oauth2/callback");
        defaults.setProperty("OAUTH_CLIENT", "paybubble");//am_role_assignment
        defaults.setProperty("S2S_BASE_URI", "http://rpe-service-auth-provider-perftest.service.core-compute-perftest.internal/testing-support");
        defaults.setProperty("DM_STORE_API_BASE_URI", "http://dm-store-perftest.service.core-compute-perftest.internal");
        //defaults.setProperty("S2S_SERVICE_NAME", "api_gw");
        defaults.setProperty("S2S_SERVICE_NAME", "probate_frontend");//am_role_assignment_service
        defaults.setProperty("IDAM_OAUTH_SECRET", "");
        //defaults.setProperty("IDAM_OAUTH_SECRET", ConfigFactory.load.getString("IDAM_OAUTH_SECRET"));
        defaults.setProperty("FUNCTIONAL_TEST_CLIENT_S2S_TOKEN", "");
        //defaults.setProperty("FUNCTIONAL_TEST_CLIENT_S2S_TOKEN", ConfigFactory.load.getString("FUNCTIONAL_TEST_CLIENT_S2S_TOKEN"));

    }

    public static String require(String name) {
        return System.getenv(name) == null ? defaults.getProperty(name) : System.getenv(name);
    }

    public static String getS2sUrl() {
        return require("S2S_BASE_URI");
    }

    public static String getS2sSecret() {
        return require("FUNCTIONAL_TEST_CLIENT_S2S_TOKEN");
    }

    public static String getS2sMicroservice() {
        System.out.println("getS2sMicroservice");
        return require("S2S_SERVICE_NAME");
    }

    public static String getIdamUrl() {
        return require("IDAM_API_BASE_URI");
    }

    public static String getOAuthClient() {
        return require("OAUTH_CLIENT");
    }

    public static String getOAuthRedirect() {
        return require("IDAM_AUTH_REDIRECT");
    }

    public static String getOAuthSecret() {
        return require("IDAM_OAUTH_SECRET");
    }


}
