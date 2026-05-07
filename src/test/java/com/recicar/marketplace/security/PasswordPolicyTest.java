package com.recicar.marketplace.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PasswordPolicyTest {

    @Test
    void acceptsStrongPassword() {
        assertThat(PasswordPolicy.isAcceptable("Aa1!aaaa")).isTrue();
    }

    @Test
    void rejectsShortOrWeak() {
        assertThat(PasswordPolicy.isAcceptable("short")).isFalse();
        assertThat(PasswordPolicy.isAcceptable("alllowercase1!")).isFalse();
        assertThat(PasswordPolicy.isAcceptable("ALLUPPER1!")).isFalse();
    }
}
