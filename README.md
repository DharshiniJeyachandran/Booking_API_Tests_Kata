
# Booking API Tests Kata

Booking API Tests (Kata)

This project contains automated API tests for the Automation in Testing Room booking website
, using Cucumber (BDD) and RestAssured.

The tests cover:

Booking API – creating and validating room bookings.

Message API – submitting contact forms.

Authentication API – logging in and validating tokens.


Application Under Test

Hotel Booking Website: https://automationintesting.online

Swagger Documentation:

Booking endpoint: Swagger UI

Authentication endpoint (optional): Swagger UI



## 📦 Tech & Requirements

- Java 21
- Maven 3.9+
- Cucumber (Gherkin), JUnit Platform, Rest Assured, AssertJ, Log4j

---

⚙️ Tech Stack

Java – JDK 21

Maven – build tool

JUnit 5 – test runner

Cucumber – BDD with Gherkin feature files

RestAssured – REST API testing library

AssertJ – fluent assertions

------------------------------------------------------
**Local Setup**

Clone the below repository

git clone https://github.com/DharshiniJeyachandran/Booking_API_Tests_Kata.git


Import as Maven project

Open your IDE (IntelliJ/Eclipse).

Select Import Project → Maven → choose the cloned repo.

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


## ▶️ How to Run

### Run everything (respecting runner's default tags)
```bash
mvn clean test
```

## ✍️ Feature Files

### Authentication (`features/authentication/authentication.feature`)
### Booking (`features/booking/create_booking_validations.feature`)
### Message Submission (`features/booking/contact_message.feature`)



## 📊 Reports



Open the report after a run:
```
target/cucumber-report/cucumber.html
```

---
the recent latest report is inside the reports/latest
```
report/latest/cucumber.html
```

Happy testing! 🧪
