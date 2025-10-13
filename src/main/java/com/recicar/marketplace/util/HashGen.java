package com.recicar.marketplace.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Slf4j
public class HashGen {
    public static void main(String[] args) {
        var encoder = new BCryptPasswordEncoder();
        log.info("admin123: {}", encoder.encode("admin123"));
        log.info("vendor123: {}", encoder.encode("vendor123"));
        log.info("customer123: {}", encoder.encode("customer123"));
    }
}

