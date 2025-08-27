package com.booking.stepdefinition;

import com.booking.util.BookingTestContext;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class AuthLoginSteps {

    private static final Logger LOG = LogManager.getLogger(AuthLoginSteps.class);

    private final BookingTestContext context;
    private Response response;

    // ---------- Login ----------

    @Given("the user has access to the auth endpoint {string}")
    public void userHasAccessToAuthEndpoint(String endpoint) {
        context.session.put("endpoint", endpoint);
    }

    @When("the user submits login credentials:")
    public void submitLoginCredentials(DataTable table) {
        final String endpoint = Objects.toString(context.session.get("endpoint"), null);
        assertThat(endpoint).as("Endpoint not set. Did the Background step run?").isNotBlank();

        Map<String, String> creds = table.asMap(String.class, String.class);

        JSONObject body = new JSONObject()
                .put("username", creds.get("username"))
                .put("password", creds.get("password"));

        response = context.requestSetup()
                .contentType("application/json")
                .accept("application/json")
                .body(body.toString())
                .when()
                .post(endpoint);

        context.session.put("lastResponse", response);

        // Save token for later validation step if present
        String token = response.jsonPath().getString("token");
        if (token != null && !token.isBlank()) {
            context.session.put("token", token);
        }

        LOG.info("POST " + endpoint + " -> " + response.getStatusCode() + " " + response.asString());
    }

    @Then("the authentication response status should be {int}")
    public void authStatusShouldBe(int expected) {
        Response r = response != null ? response : (Response) context.session.get("lastResponse");
        assertThat(r).as("No response captured").isNotNull();
        assertThat(r.getStatusCode()).isEqualTo(expected);
    }

    @Then("the authentication response should contain a non-empty {string}")
    public void authBodyHasNonEmpty(String key) {
        Response r = response != null ? response : (Response) context.session.get("lastResponse");
        assertThat(r).as("No response captured").isNotNull();

        Object value = r.jsonPath().get(key);
        assertThat(value)
                .as("Field '%s' missing in body: %s", key, r.asString())
                .isNotNull();

        String asText = String.valueOf(value).trim();
        assertThat(asText)
                .as("Field '%s' is blank", key)
                .isNotEmpty();
    }

    @Then("the authentication response should contain an error message {string}")
    public void authErrorMessageShouldBe(String expected) {
        Response r = response != null ? response : (Response) context.session.get("lastResponse");
        assertThat(r).as("No response captured").isNotNull();

        String actualError = r.jsonPath().getString("error");
        assertThat(actualError)
                .as("Expected error '%s' but got: %s", expected, r.asString())
                .isEqualTo(expected);
    }

    // ---------- Token validation ----------

    @And("the user has access to the token validation endpoint {string}")
    public void userHasAccessToValidationEndpoint(String endpoint) {
        context.session.put("endpoint", endpoint);
    }

    @When("the client validates the login token")
    public void validateLoginToken() {
        String endpoint = Objects.toString(context.session.get("endpoint"), null);
        assertThat(endpoint).isNotBlank();

        String token = Objects.toString(context.session.get("token"), null);
        assertThat(token).as("No token stored from login response").isNotBlank();

        JSONObject body = new JSONObject().put("token", token);

        response = context.requestSetup()
                .contentType("application/json")
                .accept("application/json")
                .body(body.toString())
                .when()
                .post(endpoint);

        context.session.put("lastResponse", response);
        LOG.info("POST " + endpoint + " -> " + response.getStatusCode() + " " + response.asString());
    }

    @When("the client validates a token:")
    public void validateProvidedToken(DataTable table) {
        String endpoint = Objects.toString(context.session.get("endpoint"), null);
        assertThat(endpoint).isNotBlank();

        Map<String, String> map = table.asMap(String.class, String.class);
        String token = map.get("token"); // may be blank for negative case

        JSONObject body = new JSONObject().put("token", token);

        response = context.requestSetup()
                .contentType("application/json")
                .accept("application/json")
                .body(body.toString())
                .when()
                .post(endpoint);

        context.session.put("lastResponse", response);
        LOG.info("POST " + endpoint + " -> " + response.getStatusCode() + " " + response.asString());
    }

    @Then("the token validation response status should be {int}")
    public void tokenValidationStatusShouldBe(int expected) {
        Response r = response != null ? response : (Response) context.session.get("lastResponse");
        assertThat(r).as("No response captured").isNotNull();
        assertThat(r.getStatusCode()).isEqualTo(expected);
    }

    @Then("the token should be reported as valid")
    public void tokenShouldBeValid() {
        Response r = response != null ? response : (Response) context.session.get("lastResponse");
        assertThat(r).isNotNull();
        Object v = r.jsonPath().get("valid");
        boolean valid = Boolean.parseBoolean(String.valueOf(v));
        assertThat(valid).as("Expected valid=true, body: %s", r.asString()).isTrue();
    }

    @Then("The the token validation should be rejected")
    public void tokenValidationRejected() {
        Response response = (Response) context.session.get("lastResponse");
        assertThat(response).isNotNull();

        int status = response.getStatusCode();
        // API may return 401 (Unauthorized) or 403 (Forbidden) for invalid/bad tokens
        boolean isRejected = (status == 401) || (status == 403);
        assertThat(isRejected)
                .as("Expected token validation to be rejected. Status=%s Body=%s",
                        Integer.valueOf(status), response.asString())
                .isTrue();
    }

    @Then("the token should be reported as invalid")
    public void tokenShouldBeInvalid() {
        Response r = response != null ? response : (Response) context.session.get("lastResponse");
        assertThat(r).isNotNull();


        Object v = r.jsonPath().get("valid");
        boolean valid = Boolean.parseBoolean(String.valueOf(v));

        assertThat(valid)
                .as("Expected valid=false, body: %s", r.asString())
                .isFalse();
    }
}
