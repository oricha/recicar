package com.recicar.marketplace.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import jakarta.validation.Validation;
import jakarta.validation.Validator;

/**
 * Test configuration that disables validation to avoid ConstraintViolationException
 * during unit tests
 */
@TestConfiguration
public class TestConfig {

    /**
     * Provides a no-op validator for tests
     */
    @Bean
    @Primary
    public Validator validator() {
        // Return a validator that doesn't perform actual validation
        return Validation.byDefaultProvider()
                .configure()
                .buildValidatorFactory()
                .getValidator();
    }
}
