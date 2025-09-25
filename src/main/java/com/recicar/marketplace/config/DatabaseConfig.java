package com.recicar.marketplace.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.sql.DataSource;
import org.springframework.boot.jdbc.DataSourceBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Database configuration class that handles SSL certificates for production environments.
 * This class provides SSL certificate support for PostgreSQL connections in cloud environments.
 */
@Configuration
@Profile("prod")
public class DatabaseConfig {

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @Value("${spring.datasource.driver-class-name}")
    private String driverClassName;

    @Value("${database.ssl.certificate:#{null}}")
    private String sslCertificate;

    @Value("${database.ssl.certificate.path:#{null}}")
    private String sslCertificatePath;

    /**
     * Creates a DataSource with SSL certificate support for production environments.
     * The SSL certificate can be provided either as:
     * 1. Base64-encoded string via DATABASE_SSL_CERTIFICATE environment variable
     * 2. File path via DATABASE_SSL_CERTIFICATE_PATH environment variable
     */
    @Bean
    @ConditionalOnProperty(name = "database.ssl.enabled", havingValue = "true", matchIfMissing = false)
    public DataSource dataSourceWithSsl() {
        DataSourceBuilder<?> builder = DataSourceBuilder.create()
                .url(databaseUrl)
                .username(username)
                .password(password)
                .driverClassName(driverClassName);

        // Add SSL properties to the URL if certificate is provided
        if (sslCertificate != null || sslCertificatePath != null) {
            String sslUrl = addSslPropertiesToUrl(databaseUrl);
            builder.url(sslUrl);
        }

        return builder.build();
    }

    /**
     * Adds SSL properties to the database URL for secure connections.
     * This method handles both certificate content and file path scenarios.
     */
    private String addSslPropertiesToUrl(String originalUrl) {
        StringBuilder urlBuilder = new StringBuilder(originalUrl);
        
        // Check if URL already has parameters
        if (originalUrl.contains("?")) {
            urlBuilder.append("&");
        } else {
            urlBuilder.append("?");
        }

        // Add SSL mode requirement
        urlBuilder.append("sslmode=require");

        // Add certificate if provided
        if (sslCertificate != null) {
            try {
                // Decode base64 certificate and write to temporary file
                byte[] certBytes = Base64.getDecoder().decode(sslCertificate);
                String certContent = new String(certBytes, StandardCharsets.UTF_8);
                
                // For PostgreSQL, we need to set the SSL root certificate
                // This is typically handled by the JDBC driver when sslmode=require
                urlBuilder.append("&sslrootcert=classpath:ssl/ca-cert.pem");
                
                // Store certificate in classpath for JDBC driver to use
                storeCertificateInClasspath(certContent);
                
            } catch (Exception e) {
                throw new RuntimeException("Failed to process SSL certificate", e);
            }
        } else if (sslCertificatePath != null) {
            urlBuilder.append("&sslrootcert=").append(sslCertificatePath);
        }

        return urlBuilder.toString();
    }

    /**
     * Stores the SSL certificate in the classpath for the JDBC driver to access.
     * This is a workaround since we can't easily pass certificate content directly to PostgreSQL JDBC.
     */
    private void storeCertificateInClasspath(String certificateContent) {
        try {
            // Create the ssl directory in resources if it doesn't exist
            java.nio.file.Path sslDir = java.nio.file.Paths.get("src/main/resources/ssl");
            java.nio.file.Files.createDirectories(sslDir);
            
            // Write certificate to classpath
            java.nio.file.Path certFile = sslDir.resolve("ca-cert.pem");
            java.nio.file.Files.write(certFile, certificateContent.getBytes(StandardCharsets.UTF_8));
            
        } catch (IOException e) {
            throw new RuntimeException("Failed to store SSL certificate in classpath", e);
        }
    }
}