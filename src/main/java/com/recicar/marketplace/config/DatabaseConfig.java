package com.recicar.marketplace.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "com.recicar.marketplace.repository")
@EnableTransactionManagement
public class DatabaseConfig {
    // Database configuration will be handled by Spring Boot auto-configuration
    // This class serves as a marker for JPA repository scanning
}