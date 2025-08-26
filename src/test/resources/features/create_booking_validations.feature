@bookingAPI @createReservation
Feature: Create a room booking

  Background:
    Given the user has access to the booking endpoint "https://automationintesting.online/api/booking"

  @createBooking @noAuth
  Scenario Outline: Successfully create a booking with valid details
    When the user submits a reservation request with the following details:
      | roomid   | firstname   | lastname   | depositpaid | checkin    | checkout   | email                  | phone        |
      | <roomid> | <firstname> | <lastname> | <depositpaid> | <checkin> | <checkout> | <email>                | <phone>      |
    Then the response status code should be 200

    Examples:
      | roomid | firstname | lastname  | depositpaid | checkin     | checkout    | email                      | phone        |
      | 1      | Emma      | Johnson   | true        | 2025-09-05  | 2025-09-10  | emma.johnson@test.com      | 55512345671  |
      | 2      | Raj       | Kapoor    | false       | 2025-09-15  | 2025-09-20  | raj.kapoor@test.com        | 55598765431  |
      | 3      | Sofia     | Martinez  | true        | 2025-10-01  | 2025-10-05  | sofia.martinez@test.com    | 55545678901  |
      | 1      | David     | Kim       | false       | 2025-10-10  | 2025-10-15  | david.kim@test.com         | 55567812341  |
      | 2      | Aisha     | Patel     | true        | 2025-11-01  | 2025-11-06  | aisha.patel@test.com       | 55522233441  |
      | 3      | Luca      | Rossi     | false       | 2025-11-15  | 2025-11-18  | luca.rossi@test.com        | 55588899901  |
      | 1      | Hannah    | Müller    | true        | 2025-12-05  | 2025-12-10  | hannah.muller@test.com     | 55511122331  |
      | 2      | Kenji     | Tanaka    | false       | 2025-12-15  | 2025-12-20  | kenji.tanaka@test.com      | 55544455661  |
      | 3      | Maria     | Silva     | true        | 2026-01-05  | 2026-01-09  | maria.silva@test.com       | 55533311221  |

  @createBooking @validationErrors @validationErrors @noAuth
  Scenario Outline: Reject invalid payload with proper validation errors
    When the user tries to book a room with the following booking details
      | roomid   | firstname   | lastname   | depositpaid | checkin    | checkout   | email                  | phone         |
      | <roomid> | <firstname> | <lastname> | <depositpaid> | <checkin> | <checkout> | <email>                | <phone>       |
    Then the response status code should be 400
    And the error messages should include "<expectedErrors>"

    Examples:
      # Completely blank/invalid values
      | roomid | firstname | lastname | depositpaid | checkin | checkout | email | phone | expectedErrors                                                                                                            |
      | 0      |           |          | true        |         |          |       |       | Lastname should not be blank; Firstname should not be blank; must be greater than or equal to 1; must not be empty; size must be between 11 and 21 |
      | 1      | Al        | Bo       | false       | 2025-09-10  | 2025-09-11  | al.bo@test.com    | 12345678901 | size must be between 3 and 30; size must be between 3 and 18                  |
      | 2      | Maria     | Silva    | true        | 2025-10-05  | 2025-10-06  | maria@test.com    | 123   | size must be between 11 and 21                   |
      | 3      | Emma      | Stone    | false       |         |          | emma.stone@test.com  | 12345678901 | must not be null, must not be null |
      | 0      |           |          | true        |         |          |       |       | Lastname should not be blank       |
      | 0      |           |          | true        |         |          |       |       | Firstname should not be blank      |
      | 0      |           |          | true        |         |          |       |       | must be greater than or equal to 1 |
      | 0      |           |          | true        |         |          |       |       | must not be empty                  |
      | 0      |           |          | true        |         |          |       |       | size must be between 11 and 21     |
