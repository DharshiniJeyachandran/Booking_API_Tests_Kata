package com.booking.util;

import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class Hooks {

    private static final Logger LOG = LogManager.getLogger(Hooks.class);
    private final BookingTestContext context;

    // Tags that skip authentication
    private static final Set<String> SKIP_AUTH_TAGS = Set.of("@unauthenticated");

    @Before(order = 0)
    public void createAuthToken(final Scenario scenario) {
        if (shouldSkipAuth(scenario)) {
            LOG.info("Skipping auth for scenario (tagged to skip): " + scenario.getName());
            return;
        }

        String username = requiredProp("auth.username");
        String password = requiredProp("auth.password");

        Response loginResponse = sendLoginRequest(username, password);
        validateLoginResponse(loginResponse);

        String authToken = extractAuthToken(loginResponse);
        saveAuthToken(authToken);

        LOG.info("Stored token in context.session under keys: 'token' and 'Authorization'");
    }

    private boolean shouldSkipAuth(Scenario scenario) {
        return scenario.getSourceTagNames().stream().anyMatch(SKIP_AUTH_TAGS::contains);
    }

    private String requiredProp(String key) {
        String value = Configuration.getProperty(key);
        assertThat(value).as("Missing property '%s'", key).isNotNull().isNotBlank();
        return value.trim();
    }

    private Response sendLoginRequest(String username, String password) {
        final String authPath = "/api/auth/login";
        JSONObject credentialsJson = new JSONObject()
                .put("username", username)
                .put("password", password);

        Response loginResponse = context.requestSetup()
                .body(credentialsJson.toString())
                .when()
                .post(authPath);

        LOG.info("Auth status: " + loginResponse.getStatusCode());
        return loginResponse;
    }

    private void validateLoginResponse(Response loginResponse) {
        assertThat(loginResponse).as("No login response").isNotNull();
        assertThat(loginResponse.getStatusCode())
                .as("Authentication failed. Body: %s", safeBody(loginResponse))
                .isEqualTo(200);
    }

    private String extractAuthToken(Response loginResponse) {
        String authToken = loginResponse.jsonPath().getString("token");
        assertThat(authToken)
                .as("No auth token retrieved. Body: %s", safeBody(loginResponse))
                .isNotNull()
                .isNotBlank();
        return authToken;
    }

    private void saveAuthToken(String authToken) {
        context.session.put("token", authToken);
        context.session.put("Authorization", "Bearer " + authToken);
    }

    private static String safeBody(Response response) {
        return response.asString();
    }
}
