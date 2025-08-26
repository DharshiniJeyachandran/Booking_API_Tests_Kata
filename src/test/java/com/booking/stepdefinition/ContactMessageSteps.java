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
public class ContactMessageSteps {

    private static final Logger LOG = LogManager.getLogger(ContactMessageSteps.class);
    private final BookingTestContext context;
    private Response response;

    @Given("the user has access to the message endpoint {string}")
    public void userHasAccessToMessageEndpoint(String endpoint) {
        context.session.put("endpoint", endpoint);
    }

    @When("the user submits a form with the following details:")
    public void submitForm(DataTable dataTable) {
        final String endpoint = Objects.toString(context.session.get("endpoint"), null);
        assertThat(endpoint).as("Endpoint not set. Did the Background step run?").isNotBlank();

        // 2-column table → single map
        Map<String, String> form = dataTable.asMap(String.class, String.class);

        JSONObject body = new JSONObject()
                .put("name",        toJson(form.get("name")))
                .put("email",       toJson(form.get("email")))
                .put("phone",       toJson(form.get("phone")))
                .put("subject",     toJson(form.get("subject")))
                .put("description", toJson(form.get("description")));

        response = context.requestSetup()
                .contentType("application/json")
                .accept("application/json")
                .body(body.toString())
                .when()
                .post(endpoint);

        context.session.put("lastResponse", response);
        LOG.info("POST " + endpoint + " -> " + response.getStatusCode() + " " + response.asString());
    }

    @Then("User should get the status code {int}")
    public void verifyStatusCode(int expected) {
        Response r = response != null ? response : (Response) context.session.get("lastResponse");
        assertThat(r).as("No response captured").isNotNull();
        assertThat(r.getStatusCode()).isEqualTo(expected);
    }

    @Then("the response body field {string} should be true")
    public void fieldShouldBeTrue(String key) {
        Response r = response != null ? response : (Response) context.session.get("lastResponse");
        assertThat(r).isNotNull();
        assertThat(r.jsonPath().getBoolean(key)).isTrue();
    }


    private static Object toJson(String s) {
        return (s == null || s.isBlank()) ? JSONObject.NULL : s;
    }
}
