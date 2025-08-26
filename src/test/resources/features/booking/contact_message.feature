@messageAPI @createMessage
Feature: Send a contact message successfully

  Background:
    Given the user has access to the message endpoint "/api/message"

  @submitMessage @unauthenticated
  Scenario Outline: Successfully send a contact message with valid details
    When the user submits a form with the following details:
      | name        | <name>        |
      | email       | <email>       |
      | phone       | <phone>       |
      | subject     | <subject>     |
      | description | <description> |
    Then User should get the status code 200
    And the response body field "success" should be true

    Examples:
      | name       | email              | phone        | subject      | description                                  |
      | Dharshini  | yamini.jc@gmail.com| 12345678911  | test11111    | 1234567890asdfghjklasdfghjk                  |
      | Raj Kapoor | raj.kapoor@test.com| 55598765431  | Payment Info | Could you confirm if a deposit is required?  |
