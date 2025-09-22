package com.recicar.steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
public class SearchSteps {

    @Autowired
    private WebTestClient webTestClient;

    private WebTestClient.ResponseSpec responseSpec;
    private String lastSearchType;

    @Given("the application is running")
    public void theApplicationIsRunning() {
        // The SpringBootTest annotation starts the application
    }

    @Given("there are products in the system")
    public void thereAreProductsInTheSystem() {
        // This step assumes products are already seeded in the test database
        // In a real implementation, you might want to seed test data here
    }

    @When("I search for a product with part number {string}")
    public void iSearchForAProductWithPartNumber(String partNumber) {
        responseSpec = webTestClient.get().uri("/search?query=" + partNumber).exchange();
    }

    @When("I search for a product with OEM number {string}")
    public void iSearchForAProductWithOEMNumber(String oemNumber) {
        responseSpec = webTestClient.get().uri("/search?query=" + oemNumber).exchange();
    }

    @When("I search for a product with part name {string}")
    public void iSearchForAProductWithPartName(String partName) {
        responseSpec = webTestClient.get().uri("/search/part-name?partName=" + partName).exchange();
    }

    @When("I search for a product with general term {string}")
    public void iSearchForAProductWithGeneralTerm(String searchTerm) {
        responseSpec = webTestClient.get().uri("/search?query=" + searchTerm).exchange();
    }

    @When("I search for a product with make {string}, model {string}, and engine type {string}")
    public void iSearchForAProductWithMakeModelAndEngineType(String make, String model, String engineType) {
        responseSpec = webTestClient.get().uri("/search/vehicle?make=" + make + "&model=" + model + "&engineType=" + engineType).exchange();
    }

    @When("I search for a product with make {string}, model {string}, engine type {string}, and part name {string}")
    public void iSearchForAProductWithMakeModelEngineTypeAndPartName(String make, String model, String engineType, String partName) {
        responseSpec = webTestClient.get().uri("/search/vehicle?make=" + make + "&model=" + model + "&engineType=" + engineType + "&partName=" + partName).exchange();
    }

    @When("I search for a product with {string}")
    public void iSearchForAProductWith(String searchTerm) {
        if ("null".equals(searchTerm) || "".equals(searchTerm)) {
            responseSpec = webTestClient.get().uri("/search?query=").exchange();
        } else {
            responseSpec = webTestClient.get().uri("/search?query=" + searchTerm).exchange();
        }
    }

    @When("I search using parameter {string} with value {string}")
    public void iSearchUsingParameterWithValue(String param, String value) {
        if ("q".equals(param)) {
            responseSpec = webTestClient.get().uri("/search?q=" + value).exchange();
        } else {
            responseSpec = webTestClient.get().uri("/search?query=" + value).exchange();
        }
    }

    @When("I search for a product with general term {string} on page {int}")
    public void iSearchForAProductWithGeneralTermOnPage(String searchTerm, int page) {
        responseSpec = webTestClient.get().uri("/search?query=" + searchTerm + "&page=" + page).exchange();
    }

    @Then("I should see a list of products with part number {string}")
    public void iShouldSeeAListOfProductsWithPartNumber(String partNumber) {
        responseSpec.expectStatus().isOk()
                .expectBody()
                .xpath("//div[contains(@class,'product')]//span[contains(text(),'" + partNumber + "')]").exists();
    }

    @Then("I should see a list of products containing part number {string}")
    public void iShouldSeeAListOfProductsContainingPartNumber(String partNumber) {
        responseSpec.expectStatus().isOk()
                .expectBody()
                .xpath("//div[contains(@class,'product')]//span[contains(text(),'" + partNumber + "')]").exists();
    }

    @Then("I should see a list of products with OEM number {string}")
    public void iShouldSeeAListOfProductsWithOEMNumber(String oemNumber) {
        responseSpec.expectStatus().isOk()
                .expectBody()
                .xpath("//div[contains(@class,'product')]//span[contains(text(),'" + oemNumber + "')]").exists();
    }

    @Then("I should see a list of products containing OEM number {string}")
    public void iShouldSeeAListOfProductsContainingOEMNumber(String oemNumber) {
        responseSpec.expectStatus().isOk()
                .expectBody()
                .xpath("//div[contains(@class,'product')]//span[contains(text(),'" + oemNumber + "')]").exists();
    }

    @Then("I should see a list of products with {string} in their name")
    public void iShouldSeeAListOfProductsWithInTheirName(String partName) {
        responseSpec.expectStatus().isOk()
                .expectBody()
                .xpath("//div[contains(@class,'product')]//h3[contains(text(),'" + partName + "')]").exists();
    }

    @Then("I should see a list of products containing {string}")
    public void iShouldSeeAListOfProductsContaining(String searchTerm) {
        responseSpec.expectStatus().isOk()
                .expectBody()
                .xpath("//div[contains(@class,'product')]//h3[contains(text(),'" + searchTerm + "')]").exists();
    }

    @Then("I should see a list of products compatible with a {string}")
    public void iShouldSeeAListOfProductsCompatibleWithA(String vehicle) {
        responseSpec.expectStatus().isOk()
                .expectBody()
                .xpath("//div[contains(@class,'product')]").exists();
    }

    @Then("I should see a list of products compatible with a {string} containing {string}")
    public void iShouldSeeAListOfProductsCompatibleWithAContaining(String vehicle, String partName) {
        responseSpec.expectStatus().isOk()
                .expectBody()
                .xpath("//div[contains(@class,'product')]//h3[contains(text(),'" + partName + "')]").exists();
    }

    @Then("the search type should be {string}")
    public void theSearchTypeShouldBe(String expectedSearchType) {
        responseSpec.expectStatus().isOk()
                .expectBody()
                .xpath("//input[@name='searchType' and @value='" + expectedSearchType + "']").exists();
    }

    @Then("I should see an error message {string}")
    public void iShouldSeeAnErrorMessage(String expectedMessage) {
        if ("redirect to products page".equals(expectedMessage)) {
            responseSpec.expectStatus().is3xxRedirection();
        } else {
            responseSpec.expectStatus().isOk()
                    .expectBody()
                    .xpath("//div[contains(@class,'alert') and contains(text(),'" + expectedMessage + "')]").exists();
        }
    }

    @Then("I should see an empty list of products")
    public void iShouldSeeAnEmptyListOfProducts() {
        responseSpec.expectStatus().isOk()
                .expectBody()
                .xpath("//div[contains(@class,'product')]").doesNotExist();
    }

    @Then("I should see {string} message")
    public void iShouldSeeMessage(String message) {
        responseSpec.expectStatus().isOk()
                .expectBody()
                .xpath("//div[contains(text(),'" + message + "')]").exists();
    }

    @Then("I should see pagination controls")
    public void iShouldSeePaginationControls() {
        responseSpec.expectStatus().isOk()
                .expectBody()
                .xpath("//nav[contains(@class,'pagination')]").exists();
    }

    @Then("the current page should be {int}")
    public void theCurrentPageShouldBe(int expectedPage) {
        responseSpec.expectStatus().isOk()
                .expectBody()
                .xpath("//nav[contains(@class,'pagination')]//li[contains(@class,'active')]//span[contains(text(),'" + (expectedPage + 1) + "')]").exists();
    }
}
