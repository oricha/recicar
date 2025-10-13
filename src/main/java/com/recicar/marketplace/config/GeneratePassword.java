package com.recicar.marketplace.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Slf4j
public class GeneratePassword {
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "user123";
        String hashedPassword = encoder.encode(password);
        log.info("Password: {}", password);
        log.info("Hashed: {}", hashedPassword);
    }
}

