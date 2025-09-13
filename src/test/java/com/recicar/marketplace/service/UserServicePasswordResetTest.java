package com.recicar.marketplace.service;

import com.recicar.marketplace.entity.PasswordResetToken;
import com.recicar.marketplace.entity.User;
import com.recicar.marketplace.repository.PasswordResetTokenRepository;
import com.recicar.marketplace.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServicePasswordResetTest {

    private UserRepository userRepository;
    private PasswordResetTokenRepository tokenRepository;
    private PasswordEncoder passwordEncoder;
    private UserService userService;

    private User user;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        tokenRepository = mock(PasswordResetTokenRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserService(userRepository, tokenRepository, passwordEncoder);

        user = new User();
        user.setId(1L);
        user.setEmail("reset@example.com");

        when(userRepository.findByEmailIgnoreCase("reset@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.encode("newStrongPass123")).thenReturn("ENCODED");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(tokenRepository.save(any(PasswordResetToken.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void initiateAndResetPassword_updatesPasswordAndMarksTokenUsed() {
        // Initiate
        userService.initiatePasswordReset("reset@example.com");

        // Capture token that was saved
        ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(tokenRepository, atLeastOnce()).save(tokenCaptor.capture());
        PasswordResetToken saved = tokenCaptor.getValue();
        String tokenStr = saved.getToken();

        // Mock findByToken
        when(tokenRepository.findByToken(tokenStr)).thenReturn(Optional.of(saved));

        // Reset
        boolean ok = userService.resetPassword(tokenStr, "newStrongPass123");

        assertThat(ok).isTrue();
        verify(userRepository).save(user);
        verify(tokenRepository, atLeast(2)).save(any());
        assertThat(saved.isUsed()).isTrue();
    }
}

