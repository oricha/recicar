package com.recicar.steps;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;

public class HealthSteps {

    @LocalServerPort
    private int port;

    private WebTestClient webTestClient;
    private WebTestClient.ResponseSpec lastResponse;

    private WebTestClient client() {
        if (webTestClient == null) {
            webTestClient = WebTestClient.bindToServer()
                    .baseUrl("http://localhost:" + port)
                    .responseTimeout(Duration.ofSeconds(10))
                    .build();
        }
        return webTestClient;
    }

    @When("I GET {string}")
    public void i_get(String path) {
        lastResponse = client()
                .get()
                .uri(path)
                .accept(MediaType.APPLICATION_JSON)
                .exchange();
    }

    @Then("the response is OK")
    public void the_response_is_ok() {
        lastResponse.expectStatus().isOk();
    }

    @Then("the JSON path {string} should be {string}")
    public void the_json_path_should_be(String jsonPath, String expected) {
        lastResponse
                .expectHeader().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath(jsonPath).isEqualTo(expected);
    }
}

