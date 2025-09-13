Feature: Sample addition
  Simple scenario to validate Cucumber wiring.

  Scenario: Add two numbers
    Given I have number 2
    And I have number 3
    When I add the numbers
    Then the result should be 5

