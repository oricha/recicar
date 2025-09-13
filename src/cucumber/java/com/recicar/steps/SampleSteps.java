package com.recicar.steps;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SampleSteps {

    private int a;
    private int b;
    private int result;

    @Given("I have number {int}")
    public void i_have_number(Integer n) {
        if (a == 0) {
            a = n;
        } else {
            b = n;
        }
    }

    @And("I have number {int}")
    public void i_also_have_number(Integer n) {
        if (a == 0) {
            a = n;
        } else {
            b = n;
        }
    }

    @When("I add the numbers")
    public void i_add_the_numbers() {
        result = a + b;
    }

    @Then("the result should be {int}")
    public void the_result_should_be(Integer expected) {
        assertEquals(expected.intValue(), result);
    }
}

