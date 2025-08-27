package com.booking.stepdefinition;

import com.booking.util.BookingTestContext;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;
import io.restassured.response.Response;
import lombok.RequiredArgsConstructor;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@RequiredArgsConstructor
public class CreateBookingSteps {

    private final BookingTestContext context;
    private Response response;
    private static final Logger LOG = LogManager.getLogger(CreateBookingSteps.class);

    @Given("the user has access to the booking endpoint {string}")
    public void userHasAccessToEndpoint(final String endpoint) {
        context.session.put("endpoint", endpoint);
    }

    // Support BOTH phrasings used in the features
    @When("the user submits a reservation request with the following details:")
    @When("the user tries to book a room with the following booking details")
    public void theUserBooksRoomWithBookingDetails(final DataTable dataTable) {
        final String endpoint = Objects.toString(context.session.get("endpoint"), null);
        assertThat(endpoint)
                .as("No endpoint set in context. Did you call the Given step?")
                .isNotBlank();

        final List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);

        for (Map<String, String> row : rows) {
            final JSONObject body = getJsonObject(row);

            response = context.requestSetup()
                    .contentType("application/json")
                    .body(body.toString())
                    .when()
                    .post(endpoint);

            // IMPORTANT: store it so Then steps can read it
            context.capture(response);

            LOG.info("Create booking -> status: " + response.getStatusCode() + " body: " + response.asString());
        }
    }

    private static JSONObject getJsonObject(Map<String, String> row) {
        final JSONObject body = new JSONObject()
                .put("roomid", mapInt(row.get("roomid")))
                .put("firstname", mapPlaceholder(row.get("firstname")))
                .put("lastname", mapPlaceholder(row.get("lastname")))
                .put("depositpaid", mapBoolean(row.get("depositpaid")))
                .put("email", mapPlaceholder(row.get("email")))
                .put("phone", mapPlaceholder(row.get("phone")))
                .put("bookingdates", new JSONObject()
                        .put("checkin", mapPlaceholder(row.get("checkin")))
                        .put("checkout", mapPlaceholder(row.get("checkout"))));
        return body;
    }

    @Then("the response status code should be {int}")
    public void theResponseStatusCodeShouldBe(final int expectedStatusCode) {
        Response r = (response != null) ? response : context.getLastResponse();
        assertThat(r)
                .as("No response captured. Ensure the When step sent the request and called context.capture(response).")
                .isNotNull();
        assertThat(r.getStatusCode()).isEqualTo(expectedStatusCode);
    }

    @Then("the error messages should include {string}")
    public void the_error_messages_should_include(String expectedErrors) {
        Response r = (response != null) ? response : context.getLastResponse();
        assertThat(r)
                .as("No response captured. Ensure the When step called context.capture(response).")
                .isNotNull();

        String raw = r.asString();

        String joined;
        try {
            java.util.List<String> errs = r.jsonPath().getList("errors", String.class);
            joined = (errs != null && !errs.isEmpty()) ? String.join(", ", errs) : raw;
        } catch (Exception e) {
            joined = raw;
        }

        String haystack = joined.replaceAll("[\\[\\]\"]", "");
        for (String expected : expectedErrors.split(";")) {
            String needle = expected.trim();
            if (!needle.isEmpty()) {
                assertThat(haystack)
                        .as("Expected an error containing: \"%s\" but got: [%s]", needle, haystack)
                        .contains(needle);
            }
        }
    }

    private static Object mapPlaceholder(String s) {
        if (s == null) return "";
        String t = s.trim();
        if ("[null]".equalsIgnoreCase(t)) return JSONObject.NULL;
        if ("[empty]".equalsIgnoreCase(t)) return "";
        return s;
    }

    private static Object mapInt(String s) {
        if (s == null) return JSONObject.NULL;
        return Integer.parseInt(s.trim());
    }


    private static Object mapBoolean(String value) {
        if (value == null) return JSONObject.NULL;
        String normalized = value.trim().toLowerCase();
        return "true".equals(normalized) ? Boolean.TRUE : Boolean.FALSE;
    }

}
