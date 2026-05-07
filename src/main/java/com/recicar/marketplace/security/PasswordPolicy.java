package com.recicar.marketplace.security;

import java.util.regex.Pattern;

/**
 * Password policy: min 8 chars, upper, lower, digit, special symbol.
 */
public final class PasswordPolicy {

    private static final Pattern UPPER = Pattern.compile("[A-Z]");
    private static final Pattern LOWER = Pattern.compile("[a-z]");
    private static final Pattern DIGIT = Pattern.compile("[0-9]");
    private static final Pattern SPECIAL = Pattern.compile("[^A-Za-z0-9]");

    private PasswordPolicy() {}

    public static boolean isAcceptable(String password) {
        if (password == null || password.length() < 8) {
            return false;
        }
        return UPPER.matcher(password).find()
                && LOWER.matcher(password).find()
                && DIGIT.matcher(password).find()
                && SPECIAL.matcher(password).find();
    }

    public static String requirementSummaryEs() {
        return "Al menos 8 caracteres, una mayúscula, una minúscula, un número y un símbolo.";
    }
}
