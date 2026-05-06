package com.recicar.marketplace.service;

import com.recicar.marketplace.entity.PasswordResetToken;
import com.recicar.marketplace.entity.User;
import com.recicar.marketplace.repository.PasswordResetTokenRepository;
import com.recicar.marketplace.repository.UserRepository;
import com.recicar.marketplace.repository.UserPasswordHistoryRepository;
import com.recicar.marketplace.repository.VendorRepository;
import com.recicar.marketplace.service.auth.EmailVerificationService;
import com.recicar.marketplace.service.auth.UserRoleGrantService;
import com.recicar.marketplace.service.notification.AuthMailNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserServicePasswordResetTest {

    private UserRepository userRepository;
    private PasswordResetTokenRepository tokenRepository;
    private PasswordEncoder passwordEncoder;
    private UserPasswordHistoryRepository passwordHistoryRepository;
    private EmailVerificationService emailVerificationService;
    private UserRoleGrantService userRoleGrantService;
    private AuthMailNotificationService mailNotificationService;
    private VendorRepository vendorRepository;
    private UserService userService;

    private User user;

    @BeforeEach
    void setup() {
        userRepository = mock(UserRepository.class);
        tokenRepository = mock(PasswordResetTokenRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        passwordHistoryRepository = mock(UserPasswordHistoryRepository.class);
        emailVerificationService = mock(EmailVerificationService.class);
        userRoleGrantService = mock(UserRoleGrantService.class);
        mailNotificationService = mock(AuthMailNotificationService.class);
        vendorRepository = mock(VendorRepository.class);

        userService = new UserService(
                userRepository,
                tokenRepository,
                passwordEncoder,
                passwordHistoryRepository,
                emailVerificationService,
                userRoleGrantService,
                mailNotificationService,
                vendorRepository);

        user = new User();
        user.setId(1L);
        user.setEmail("reset@example.com");
        user.setAuthVersion(0L);

        when(userRepository.findByEmailIgnoreCase("reset@example.com")).thenReturn(Optional.of(user));
        when(passwordHistoryRepository.findByUser_IdOrderByCreatedAtDesc(1L)).thenReturn(Collections.emptyList());
        when(passwordEncoder.encode(anyString())).thenReturn("ENCODED");
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(tokenRepository.save(any(PasswordResetToken.class))).thenAnswer(inv -> inv.getArgument(0));
        when(passwordHistoryRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
    }

    @Test
    void initiateAndResetPassword_updatesPasswordAndMarksTokenUsed() {
        userService.initiatePasswordReset("reset@example.com");

        ArgumentCaptor<PasswordResetToken> tokenCaptor = ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(tokenRepository, atLeastOnce()).save(tokenCaptor.capture());
        PasswordResetToken saved = tokenCaptor.getValue();
        String tokenStr = saved.getToken();

        when(tokenRepository.findByToken(tokenStr)).thenReturn(Optional.of(saved));

        boolean ok = userService.resetPassword(tokenStr, "Newstrong1!");

        assertThat(ok).isTrue();
        verify(userRepository, atLeastOnce()).save(user);
        verify(tokenRepository, atLeast(2)).save(any());
        assertThat(saved.isUsed()).isTrue();
    }
}
