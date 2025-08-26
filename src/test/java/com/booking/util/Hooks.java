package com.booking.util;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class Hooks {

    private static final Logger LOG = LogManager.getLogger(Hooks.class);
    private final BookingTestContext context;

    @Before(order = 0)
    public void createAuthToken(final Scenario scenario) {
        // Skip auth for scenarios that don’t need it
        if (scenario.getSourceTagNames().contains("@unauthenticated")) {
            LOG.info("Skipping auth for scenario (tagged @unauthenticated): " + scenario.getName());
            return;
        }

        final String authPath = "/api/auth/login";

        final String username = Configuration.getProperty("auth.username");
        final String password = Configuration.getProperty("auth.password");

        assertThat(username).as("Missing property 'username'").isNotNull().isNotBlank();
        assertThat(password).as("Missing property 'password'").isNotNull().isNotBlank();

        final JSONObject credentials = new JSONObject()
                .put("username", username)
                .put("password", password);

        final Response authResponse = context.requestSetup()
                .body(credentials.toString())
                .when()
                .post(authPath);

        LOG.info("Auth status: " + authResponse.getStatusCode());
        assertThat(authResponse.getStatusCode())
                .as("Authentication failed. Body: %s", safeBody(authResponse))
                .isEqualTo(200);

        // Extract token from JSON body
        final String token = authResponse.jsonPath().getString("token");

        assertThat(token)
                .as("No auth token retrieved. Body: %s", authResponse.asString())
                .isNotNull()
                .isNotBlank();

        // Save into context for later requests
        context.session.put("token", token);
        context.session.put("Authorization", "Bearer " + token);

        LOG.info("Stored token in context.session under keys: 'token' and 'Authorization'");
    }

    private static String safeBody(Response r) {
        try { return r == null ? "<null>" : r.asString(); }
        catch (Exception e) { return "<unreadable: " + e.getMessage() + ">"; }
    }
}
