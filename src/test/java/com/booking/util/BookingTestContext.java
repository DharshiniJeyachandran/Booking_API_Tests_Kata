package com.booking.util;

import com.github.dzieciou.testing.curl.CurlRestAssuredConfigFactory;
import com.github.dzieciou.testing.curl.Options;
import io.restassured.RestAssured;
import io.restassured.config.RestAssuredConfig;
import io.restassured.http.Cookie;
import io.restassured.http.Cookies;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static io.restassured.RestAssured.given;

/**
 * Minimal shared test context.
 *- lastResponse: most recent HTTP response (optional)
 * - requestSetup(): builds a base RestAssured request with baseUri, content-type, logging
 */
public class BookingTestContext {


    public final Map<String, Object> session = new ConcurrentHashMap<>();

    /**
     * Last HTTP response (if you want to read it in later steps).
     */
    @Getter
    private volatile Response lastResponse;

    /**
     * Build a base request
     */
    public RequestSpecification requestSetup() {
        final String baseUrl = safeProp("api.baseUrl", "https://automationintesting.online");
        final String contentType = safeProp("request.contentType", "application/json");
        final boolean curlLog = Boolean.parseBoolean(safeProp("curl.logging.enabled", "true"));

        RestAssured.reset();

        RestAssuredConfig config = curlLog
                ? CurlRestAssuredConfigFactory.createConfig(Options.builder().logStacktrace().build())
                : RestAssured.config();

        RestAssured.baseURI = baseUrl;

        RequestSpecification spec = given()
                .log().all()
                .config(config)
                .contentType(contentType)
                .accept(contentType);

        // Auto-attach Authorization header if present in session
        Object auth = session.get("Authorization");
        if (auth != null) {
            spec.header("Authorization", auth.toString());
        }

        return spec;
    }

    /**
     * Store the last response for later use.
     */
    public void capture(Response response) {
        this.lastResponse = response;
    }


    /**
     * Preferred: pass in the response you got from login.
     */
    public Cookie retrieveAuthenticatedCookie(Response loginResponse) {
        if (loginResponse == null) return null;
        try {
            Cookies cookies = loginResponse.detailedCookies();
            if (cookies == null || cookies.asList().isEmpty()) return null;
            Cookie token = cookies.get("token");                 // adjust name if different
            return token != null ? token : cookies.asList().get(0);
        } catch (Exception e) {
            return null;
        }
    }


    @Deprecated
    public Cookie retriveAuthenticatedCookie(Response loginResponse) { // note the legacy spelling
        return retrieveAuthenticatedCookie(loginResponse);
    }

    /* --------------------------------------- util --------------------------------------- */

    private static String safeProp(String key, String def) {
        try {
            String v = Configuration.getProperty(key);
            return (v == null || v.isBlank()) ? def : v;
        } catch (Throwable t) {
            return def;
        }
    }
}
