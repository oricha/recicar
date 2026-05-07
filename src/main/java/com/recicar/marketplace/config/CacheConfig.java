package com.recicar.marketplace.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Enables declarative caching ({@code @Cacheable}). TTL and backend (Caffeine vs Redis)
 * are configured via {@code spring.cache.*} in {@code application.yml}.
 */
@Configuration
@EnableCaching
public class CacheConfig {
}
