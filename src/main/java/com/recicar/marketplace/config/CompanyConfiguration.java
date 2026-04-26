package com.recicar.marketplace.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(CompanyInfoProperties.class)
public class CompanyConfiguration {
}
