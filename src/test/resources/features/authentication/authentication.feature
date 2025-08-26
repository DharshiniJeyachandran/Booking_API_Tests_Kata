@auth @login
Feature: Authenticate with login endpoint

  Background:
    Given the user has access to the auth endpoint "https://automationintesting.online/api/auth/login"

  @happyPath @publicAPI
  Scenario Outline: Successfully authenticate with valid credentials
    When the user submits login credentials:
      | username | <username> |
      | password | <password> |
    Then the authentication response status should be 200
    And the authentication response should contain a non-empty "token"

    Examples:
      | username | password |
      | admin    | password |

  @negative @publicAPI
  Scenario Outline: Reject login with invalid credentials
    When the user submits login credentials:
      | username | <username> |
      | password | <password> |
    Then the authentication response status should be 401
    And the authentication response should contain an error message "<expectedError>"

    Examples:
      | username | password | expectedError        |
      | admin    |          | Invalid credentials  |
      | admin    | wrongpwd | Invalid credentials  |
      | wrongusr | password | Invalid credentials  |
