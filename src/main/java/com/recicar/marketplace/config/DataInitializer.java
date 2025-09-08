package com.recicar.marketplace.config;

import com.recicar.marketplace.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Data initializer that runs on application startup
 * Creates default admin user if it doesn't exist
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting data initialization...");
        
        try {
            // Check if admin user already exists
//            if (!userService.existsByEmail("admin@recicar.com")) {
//                logger.info("Creating default admin user...");
//
//                userService.createAdminUser(
//                    "admin@recicar.com",
//                    System.getenv().getOrDefault("ADMIN_PASSWORD", "admin123"),
//                    "Admin",
//                    "User"
//                );
//
//                logger.info("Default admin user created successfully!");
//                logger.info("Email: admin@recicar.com");
//                logger.info("Password: [HIDDEN] - Check DataInitializer.java for default password");
//            } else {
//                logger.info("Admin user already exists, skipping creation.");
//            }
            userService.dataInitializer(1L, "user123");
            userService.dataInitializer(2L, "user123");
            userService.dataInitializer(3L, "user123");
            
        } catch (Exception e) {
            logger.error("Error during data initialization: {}", e.getMessage(), e);
        }
        
        logger.info("Data initialization completed.");
    }
}
