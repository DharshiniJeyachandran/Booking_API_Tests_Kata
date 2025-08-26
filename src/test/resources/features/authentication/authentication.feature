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

  @happyPath @validate @publicAPI
  Scenario Outline: Validate the token received from a successful login
    # Login to obtain a token
    When the user submits login credentials:
      | username | <username> |
      | password | <password> |
    Then the authentication response status should be 200
    And the authentication response should contain a non-empty "token"

    # Switch to validation endpoint and validate the token from login
    And the user has access to the token validation endpoint "https://automationintesting.online/api/auth/validate"
    When the client validates the login token
    Then the token validation response status should be 200
    And the token should be reported as valid

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
      | username | password | expectedError       |
      | admin    |          | Invalid credentials |
      | admin    | wrongpwd | Invalid credentials |
      | wrongusr | password | Invalid credentials |

  @negative @validate @publicAPI
  Scenario Outline: Report invalid token during validation
    And the user has access to the token validation endpoint "https://automationintesting.online/api/auth/validate"
    When the client validates a token:
      | token | <token> |
    Then The the token validation should be rejected
    And the token should be reported as invalid

    Examples:
      | token          |
      | bad-token-123  |
      |                |
