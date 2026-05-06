package com.recicar.marketplace.service;

import com.recicar.marketplace.dto.ProfileUpdateRequest;
import com.recicar.marketplace.entity.User;
import com.recicar.marketplace.entity.UserRole;
import com.recicar.marketplace.repository.PasswordResetTokenRepository;
import com.recicar.marketplace.repository.UserPasswordHistoryRepository;
import com.recicar.marketplace.repository.UserRepository;
import com.recicar.marketplace.repository.VendorRepository;
import com.recicar.marketplace.service.auth.EmailVerificationService;
import com.recicar.marketplace.service.auth.UserRoleGrantService;
import com.recicar.marketplace.service.notification.AuthMailNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceProfileUpdateTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordResetTokenRepository tokenRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private UserPasswordHistoryRepository passwordHistoryRepository;
    @Mock
    private EmailVerificationService emailVerificationService;
    @Mock
    private UserRoleGrantService userRoleGrantService;
    @Mock
    private AuthMailNotificationService mailNotificationService;
    @Mock
    private VendorRepository vendorRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void injectBaseUrl() {
        ReflectionTestUtils.setField(userService, "appBaseUrl", "http://localhost:8080");
    }

    @Test
    void updateAccountFromProfileRequest_trimsEmailAndStores() {
        User existing = new User();
        existing.setId(40L);
        existing.setEmail("old@example.com");
        existing.setFirstName("O");
        existing.setLastName("L");
        existing.setRole(UserRole.CUSTOMER);

        ProfileUpdateRequest req = new ProfileUpdateRequest();
        req.setEmail("  NEW@EXAMPLE.COM  ");
        req.setFirstName("");
        req.setLastName("");
        req.setPhone("");

        when(userRepository.findById(40L)).thenReturn(Optional.of(existing));
        when(userRepository.existsByEmailIgnoreCase("new@example.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        User out = userService.updateAccountFromProfileRequest(40L, req);

        assertThat(out.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    void updateAccountFromProfileRequest_rejectsDuplicateEmail() {
        User existing = new User();
        existing.setId(41L);
        existing.setEmail("me@example.com");

        ProfileUpdateRequest req = new ProfileUpdateRequest();
        req.setEmail("taken@example.com");

        when(userRepository.findById(41L)).thenReturn(Optional.of(existing));
        when(userRepository.existsByEmailIgnoreCase("taken@example.com")).thenReturn(true);

        assertThatThrownBy(() -> userService.updateAccountFromProfileRequest(41L, req))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email");
    }
}
