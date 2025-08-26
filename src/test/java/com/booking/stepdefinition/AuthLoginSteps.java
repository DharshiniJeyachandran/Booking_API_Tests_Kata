package com.booking.stepdefinition;

import com.booking.util.BookingTestContext;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
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
}
