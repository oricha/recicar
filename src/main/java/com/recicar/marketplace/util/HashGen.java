package com.recicar.marketplace.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class HashGen {
    public static void main(String[] args) {
        var encoder = new BCryptPasswordEncoder();
        System.out.println(encoder.encode("admin123"));
        System.out.println(encoder.encode("vendor123"));
        System.out.println(encoder.encode("customer123"));
    }
}
