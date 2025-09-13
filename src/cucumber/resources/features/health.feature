Feature: Spring Boot health
  Validate the Spring context loads and health endpoint responds.

  Scenario: Health endpoint is UP
    When I GET "/actuator/health"
    Then the response is OK
    And the JSON path "$.status" should be "UP"

