
# Booking API Tests Kata

Automated API testing using **Java 21**, **Maven**, **Cucumber (JUnit 5)** and **Rest Assured** for the public site
[automationintesting.online](https://automationintesting.online).

This suite covers three areas:

- **Authentication** — `/api/auth/login`, `/api/auth/validate`
- **Booking** — `/api/booking` (positive + validation errors)
- **Message submission** — `/api/message`

---

## 📦 Tech & Requirements

- Java 21
- Maven 3.9+
- Cucumber (Gherkin), JUnit Platform, Rest Assured, AssertJ, Log4j

---

## 📁 Project Structure

```
repo/Booking_API_Tests_Kata-main
├─ pom.xml
├─ src
│  └─ test
│     ├─ java
│     │  └─ com.booking
│     │     ├─ runner/
│     │     │  └─ TestRunner.java          # Cucumber runner (JUnit 5)
│     │     ├─ stepdefinition/
│     │     │  ├─ AuthLoginSteps.java      # login + token validate
│     │     │  ├─ CreateBookingSteps.java  # create booking + validations
│     │     │  └─ ContactMessageSteps.java # message submission
│     │     └─ util/
│     │        ├─ BookingTestContext.java  # shared session/spec builder
│     │        ├─ Configuration.java       # simple classpath config loader
│     │        └─ Hooks.java               # auth pre-hook (can be skipped)
│     └─ resources
│        ├─ features/
│        │  ├─ authentication/authentication.feature
│        │  ├─ booking/create_booking_validations.feature
│        │  └─ booking/contact_message.feature
│        └─ application.properties         # api.baseUrl etc.
└─ README.md
```

---

## ⚙️ Configuration

`src/test/resources/application.properties`

```properties
# Request configuration
request.contentType=application/json

# API base URL
api.baseUrl=https://automationintesting.online

# Authentication (only used when auth is enabled)
auth.username=admin
auth.password=password
```

> Tip: The features should prefer **base URL + path** (set in steps/hooks) instead of hard-coding the full URL, so you can change `api.baseUrl` without touching Gherkin.

---

## 🏷 Tags in this repo



```
@auth, @bookingAPI, @createBooking, @createMessage, @createReservation, @gmail, @happyPath, @login, @messageAPI, @negative, @publicAPI, @submitMessage, @test, @unauthenticated, @validate, @validationErrors
```

---

## ▶️ How to Run

### Run everything (respecting runner's default tags)
```bash
mvn clean test
```

### Override tag filter from CLI (ignores @IncludeTags in runner)
- **Only authentication (login + validate):**
  ```bash
  mvn test -Dcucumber.filter.tags="@auth"
  ```
- **Only booking:**
  ```bash
  mvn test -Dcucumber.filter.tags="@bookingAPI"
  ```
- **Only message submission:**
  ```bash
  mvn test -Dcucumber.filter.tags="@messageAPI"
  ```
- **Only happy path:**
  ```bash
  mvn test -Dcucumber.filter.tags="@happyPath"
  ```
- **Only negative validations:**
  ```bash
  mvn test -Dcucumber.filter.tags="@negative"
  ```
- **Only token validation:**
  ```bash
  mvn test -Dcucumber.filter.tags="@login and @validate"
  ```

> If the runner's `@IncludeTags` still interferes in your IDE, temporarily remove that
annotation or keep the runner generic (no tag include) and filter via CLI only.

---

## ✍️ Feature Files

### Authentication (`features/authentication/authentication.feature`)
- **Login (happy path):** 200 and non-empty `token`
- **Login (negative):** 401 with `error: Invalid credentials`
- **Token validation:** validate token from login (valid=true) and invalid token flows

### Booking (`features/booking/create_booking_validations.feature`)
- **@createBooking @unauthenticated:** positive bookings via examples
- **@validationErrors:** invalid payloads return 400; `Then the error messages should include "<expectedErrors>"`

### Message Submission (`features/booking/contact_message.feature`)
- **@createMessage @publicAPI @happyPath:** POST `/api/message` with `name,email,phone,subject,description` → `success: true`
- **@negative:** invalid inputs assert specific validation error texts



## 📊 Reports



Open the report after a run:
```
target/cucumber-report/cucumber.html
```

---


Happy testing! 🧪
