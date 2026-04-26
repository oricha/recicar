package com.recicar.marketplace.integration;

import com.recicar.marketplace.entity.Category;
import com.recicar.marketplace.repository.BrandRepository;
import com.recicar.marketplace.repository.CategoryRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Full Flyway against PostgreSQL (GIN in V15, etc.). Excluded from default test unless
 * {@code -PincludeIntegration} (see {@code build.gradle}).
 * <p>Uses in order: {@code SPRING_DATASOURCE_URL} / {@code TEST_DATABASE_URL} (CI, .env) or, if none,
 * a {@link org.testcontainers.containers.PostgreSQLContainer} (local Docker).
 */
@Tag("integration")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@ActiveProfiles("test")
class CategoryAndBrandSystemPostgresIT {

    private static final PostgreSQLContainer<?> EMBEDDED_POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("recicar_it")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void registerDatasource(DynamicPropertyRegistry registry) {
        if (isExternalPostgres()) {
            registry.add("spring.datasource.url", () -> firstPostgresUrl());
            registry.add("spring.datasource.username", CategoryAndBrandSystemPostgresIT::firstPostgresUser);
            registry.add("spring.datasource.password", CategoryAndBrandSystemPostgresIT::firstPostgresPassword);
        } else {
            EMBEDDED_POSTGRES.start();
            registry.add("spring.datasource.url", EMBEDDED_POSTGRES::getJdbcUrl);
            registry.add("spring.datasource.username", EMBEDDED_POSTGRES::getUsername);
            registry.add("spring.datasource.password", EMBEDDED_POSTGRES::getPassword);
        }
        registry.add("spring.flyway.enabled", () -> "true");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
    }

    private static boolean isExternalPostgres() {
        return firstPostgresUrl() != null;
    }

    private static String firstPostgresUrl() {
        String u = firstNonEmpty(
                System.getenv("SPRING_DATASOURCE_URL"),
                System.getenv("TEST_DATABASE_URL"));
        if (u != null && u.startsWith("jdbc:postgresql:")) {
            return u;
        }
        return null;
    }

    private static String firstPostgresUser() {
        return Optional.ofNullable(
                firstNonEmpty(System.getenv("SPRING_DATASOURCE_USERNAME"), System.getenv("TEST_DATABASE_USERNAME"))
        ).orElse("test");
    }

    private static String firstPostgresPassword() {
        return Optional.ofNullable(
                firstNonEmpty(System.getenv("SPRING_DATASOURCE_PASSWORD"), System.getenv("TEST_DATABASE_PASSWORD"))
        ).orElse("test");
    }

    private static String firstNonEmpty(String a, String b) {
        return Stream.of(a, b).filter(s -> s != null && !s.isEmpty()).findFirst().orElse(null);
    }

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void flywayMigrations_ran_categoriesAndBrandsPresent() {
        long categories = categoryRepository.count();
        long brands = brandRepository.count();
        assertThat(categories).isGreaterThan(0);
        assertThat(brands).isGreaterThan(0);
    }

    @Test
    void categoryHierarchy_motorBloqueExists() {
        Optional<Category> motorBloque = categoryRepository.findBySlug("motor-bloque");
        assertThat(motorBloque).isPresent();
    }

    @Test
    void categoryApi_listRoots() throws Exception {
        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].slug").exists());
    }

    @Test
    void brandApi_listBrands() throws Exception {
        mockMvc.perform(get("/api/v1/brands"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").exists());
    }
}
