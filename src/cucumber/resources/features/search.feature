Feature: Product Search
  As a customer
  I want to search for car parts
  So that I can find the exact parts I need for my vehicle

  Background:
    Given the application is running
    And there are products in the system

  @search-part-number
  Scenario: Search for a product by exact part number
    When I search for a product with part number "P12345"
    Then I should see a list of products with part number "P12345"
    And the search type should be "partNumber"

  @search-part-number-containing
  Scenario: Search for a product by partial part number
    When I search for a product with part number "P123"
    Then I should see a list of products containing part number "P123"
    And the search type should be "partNumberContaining"

  @search-oem-number
  Scenario: Search for a product by exact OEM number
    When I search for a product with OEM number "OEM12345"
    Then I should see a list of products with OEM number "OEM12345"
    And the search type should be "oemNumber"

  @search-oem-number-containing
  Scenario: Search for a product by partial OEM number
    When I search for a product with OEM number "OEM123"
    Then I should see a list of products containing OEM number "OEM123"
    And the search type should be "oemNumberContaining"

  @search-part-name
  Scenario: Search for a product by part name
    When I search for a product with part name "Brake Pad"
    Then I should see a list of products with "Brake Pad" in their name
    And the search type should be "partName"

  @search-general
  Scenario: Search for a product by general term
    When I search for a product with general term "brake"
    Then I should see a list of products containing "brake"
    And the search type should be "general"

  @search-vehicle
  Scenario: Search for a product by make, model, and engine type
    When I search for a product with make "Honda", model "Civic", and engine type "1.8L"
    Then I should see a list of products compatible with a "Honda Civic 1.8L"
    And the search type should be "vehicle"

  @search-vehicle-with-part
  Scenario: Search for a product by vehicle and part name
    When I search for a product with make "Toyota", model "Camry", engine type "2.0L", and part name "filter"
    Then I should see a list of products compatible with a "Toyota Camry 2.0L" containing "filter"
    And the search type should be "vehicle"

  @search-error-cases
  Scenario Outline: Search with invalid inputs should show error messages
    When I search for a product with "<search_term>"
    Then I should see an error message "<error_message>"

    Examples:
      | search_term | error_message                                    |
      | a           | Search term must be at least 2 characters long  |
      | ""          | redirect to products page                       |
      | null        | redirect to products page                       |

  @search-part-name-error
  Scenario: Search by part name with invalid input should show error
    When I search for a product with part name "a"
    Then I should see an error message "Part name must be at least 2 characters long"

  @search-vehicle-error
  Scenario: Search by vehicle with missing parameters should show error
    When I search for a product with make "", model "Civic", and engine type "1.8L"
    Then I should see an error message "Make, Model and Engine Type are required"

  @search-no-results
  Scenario: Search with no results should show empty list
    When I search for a product with part number "NONEXISTENT123"
    Then I should see an empty list of products
    And I should see "No products found" message

  @search-pagination
  Scenario: Search results should support pagination
    When I search for a product with general term "brake" on page 0
    Then I should see a list of products containing "brake"
    And I should see pagination controls
    And the current page should be 0

  @search-backward-compatibility
  Scenario: Search should support both 'query' and 'q' parameters
    When I search using parameter "q" with value "brake"
    Then I should see a list of products containing "brake"
    And the search type should be "general"