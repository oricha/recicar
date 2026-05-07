package com.recicar.marketplace.service;

import com.recicar.marketplace.dto.ProfileUpdateRequest;
import com.recicar.marketplace.dto.UserRegistrationDto;
import com.recicar.marketplace.entity.PasswordResetToken;
import com.recicar.marketplace.entity.User;
import com.recicar.marketplace.entity.UserPasswordHistory;
import com.recicar.marketplace.entity.UserRole;
import com.recicar.marketplace.entity.Vendor;
import com.recicar.marketplace.entity.VendorStatus;
import com.recicar.marketplace.repository.PasswordResetTokenRepository;
import com.recicar.marketplace.repository.UserPasswordHistoryRepository;
import com.recicar.marketplace.repository.UserRepository;
import com.recicar.marketplace.repository.VendorRepository;
import com.recicar.marketplace.security.PasswordPolicy;
import com.recicar.marketplace.service.auth.EmailVerificationService;
import com.recicar.marketplace.service.auth.UserRoleGrantService;
import com.recicar.marketplace.service.notification.AuthMailNotificationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    private static final int PASSWORD_HISTORY_DEPTH = 5;

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserPasswordHistoryRepository passwordHistoryRepository;
    private final EmailVerificationService emailVerificationService;
    private final UserRoleGrantService userRoleGrantService;
    private final AuthMailNotificationService mailNotificationService;
    private final VendorRepository vendorRepository;

    @Value("${app.baseUrl:http://localhost:8080}")
    private String appBaseUrl;

    public UserService(
            UserRepository userRepository,
            PasswordResetTokenRepository tokenRepository,
            PasswordEncoder passwordEncoder,
            UserPasswordHistoryRepository passwordHistoryRepository,
            EmailVerificationService emailVerificationService,
            UserRoleGrantService userRoleGrantService,
            AuthMailNotificationService mailNotificationService,
            VendorRepository vendorRepository) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordHistoryRepository = passwordHistoryRepository;
        this.emailVerificationService = emailVerificationService;
        this.userRoleGrantService = userRoleGrantService;
        this.mailNotificationService = mailNotificationService;
        this.vendorRepository = vendorRepository;
    }

    public User registerUser(UserRegistrationDto registrationDto) {
        if (existsByEmail(registrationDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        if (!PasswordPolicy.isAcceptable(registrationDto.getPassword())) {
            throw new IllegalArgumentException(PasswordPolicy.requirementSummaryEs());
        }

        User user = new User();
        user.setEmail(registrationDto.getEmail().toLowerCase().trim());
        String encoded = passwordEncoder.encode(registrationDto.getPassword());
        user.setPasswordHash(encoded);
        user.setFirstName(registrationDto.getFirstName().trim());
        user.setLastName(registrationDto.getLastName().trim());
        user.setPhone(registrationDto.getPhone());
        user.setRole(UserRole.CUSTOMER);
        user.setActive(true);
        user.setEmailVerified(false);

        User saved = userRepository.save(user);
        recordPasswordHistory(saved, encoded);
        userRoleGrantService.syncPrimaryRole(saved);

        if (registrationDto.isRegisteringAsVendor()) {
            Vendor vendor = new Vendor();
            vendor.setUser(saved);
            vendor.setBusinessName(registrationDto.getVendorBusinessName().trim());
            vendor.setTaxId(registrationDto.getVendorTaxId().trim());
            vendor.setStatus(VendorStatus.PENDING);
            vendor.setCommissionRate(new BigDecimal("0.1000"));
            vendorRepository.save(vendor);
        }

        emailVerificationService.issueTokenAndSendEmail(saved);
        return saved;
    }

    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User updateUser(User user) {
        return userRepository.save(user);
    }

    public User updateAccountFromProfileRequest(Long userId, ProfileUpdateRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (request.getFirstName() != null && !request.getFirstName().isBlank()) {
            user.setFirstName(request.getFirstName().trim());
        }
        if (request.getLastName() != null && !request.getLastName().isBlank()) {
            user.setLastName(request.getLastName().trim());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone().isBlank() ? null : request.getPhone().trim());
        }
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            String newEmail = request.getEmail().toLowerCase().trim();
            if (!newEmail.equalsIgnoreCase(user.getEmail()) && userRepository.existsByEmailIgnoreCase(newEmail)) {
                throw new IllegalArgumentException("Email already in use");
            }
            user.setEmail(newEmail);
        }
        return userRepository.save(user);
    }

    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!PasswordPolicy.isAcceptable(newPassword)) {
            throw new IllegalArgumentException(PasswordPolicy.requirementSummaryEs());
        }
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        if (matchesRecentPasswords(user, newPassword)) {
            throw new IllegalArgumentException("No puede reutilizar una contraseña reciente.");
        }

        String encoded = passwordEncoder.encode(newPassword);
        user.setPasswordHash(encoded);
        bumpCredentialVersion(user);
        recordPasswordHistory(user, encoded);
        userRepository.save(user);
        mailNotificationService.sendPasswordChangedNotice(user);
    }

    public void verifyEmail(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setEmailVerified(true);
        userRepository.save(user);
    }

    public void initiatePasswordReset(String email) {
        userRepository.findByEmailIgnoreCase(email).ifPresent(user -> {
            String tokenStr = UUID.randomUUID().toString();
            PasswordResetToken token = new PasswordResetToken();
            token.setToken(tokenStr);
            token.setUser(user);
            token.setExpiresAt(LocalDateTime.now().plusHours(1));
            tokenRepository.save(token);
            mailNotificationService.sendPasswordReset(user, "/reset-password?token=" + tokenStr);
        });
    }

    public boolean resetPassword(String tokenStr, String newPassword) {
        if (!PasswordPolicy.isAcceptable(newPassword)) {
            return false;
        }
        Optional<PasswordResetToken> tokenOpt = tokenRepository.findByToken(tokenStr);
        if (tokenOpt.isEmpty()) {
            return false;
        }
        PasswordResetToken token = tokenOpt.get();
        if (token.isUsed() || token.getExpiresAt().isBefore(LocalDateTime.now())) {
            return false;
        }
        User user = token.getUser();
        if (matchesRecentPasswords(user, newPassword)) {
            return false;
        }
        String encoded = passwordEncoder.encode(newPassword);
        user.setPasswordHash(encoded);
        bumpCredentialVersion(user);
        recordPasswordHistory(user, encoded);
        userRepository.save(user);
        token.setUsed(true);
        tokenRepository.save(token);
        return true;
    }

    /** Increments auth version to drop existing sessions (API + server session invalidation hook). */
    public void revokeAllSessions(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setAuthVersion(user.getAuthVersion() + 1);
        userRepository.save(user);
    }

    public void setUserActive(Long userId, boolean active) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setActive(active);
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<User> findByRole(UserRole role) {
        return userRepository.findByRole(role);
    }

    @Transactional(readOnly = true)
    public List<User> findActiveUsers() {
        return userRepository.findByActiveTrue();
    }

    @Transactional(readOnly = true)
    public long countByRole(UserRole role) {
        return userRepository.countByRole(role);
    }

    public void promoteToVendor(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (user.getRole() != UserRole.CUSTOMER) {
            throw new IllegalArgumentException("Only customers can be promoted to vendors");
        }

        user.setRole(UserRole.VENDOR);
        User saved = userRepository.save(user);
        userRoleGrantService.syncPrimaryRole(saved);
    }

    public User createAdminUser(String email, String password, String firstName, String lastName) {
        if (existsByEmail(email)) {
            throw new IllegalArgumentException("Admin email already exists");
        }

        User admin = new User();
        admin.setEmail(email.toLowerCase().trim());
        admin.setPasswordHash(passwordEncoder.encode(password));
        admin.setFirstName(firstName);
        admin.setLastName(lastName);
        admin.setRole(UserRole.ADMIN);
        admin.setActive(true);
        admin.setEmailVerified(true);

        User saved = userRepository.save(admin);
        userRoleGrantService.syncPrimaryRole(saved);
        return saved;
    }

    public void dataInitializer(Long userId, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public User ensureOAuthUser(String email, String givenName, String familyName) {
        Optional<User> existing = userRepository.findByEmailIgnoreCase(email);
        if (existing.isPresent()) {
            return existing.get();
        }
        User u = new User();
        u.setEmail(email.toLowerCase().trim());
        u.setPasswordHash(passwordEncoder.encode(UUID.randomUUID().toString()));
        u.setFirstName(givenName != null && !givenName.isBlank() ? givenName : "Usuario");
        u.setLastName(familyName != null && !familyName.isBlank() ? familyName : "OAuth");
        u.setRole(UserRole.CUSTOMER);
        u.setActive(true);
        u.setEmailVerified(true);
        User saved = userRepository.save(u);
        userRoleGrantService.syncPrimaryRole(saved);
        return saved;
    }

    public void recordFailedLoginForEmail(String email) {
        userRepository.findByEmailIgnoreCase(email).ifPresent(user -> {
            int n = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(n);
            if (n >= 10) {
                user.setLockedUntil(LocalDateTime.now().plusMinutes(30));
            }
            userRepository.save(user);
        });
    }

    public void resetFailedLoginsForEmail(String email) {
        userRepository.findByEmailIgnoreCase(email).ifPresent(user -> {
            user.setFailedLoginAttempts(0);
            user.setLockedUntil(null);
            userRepository.save(user);
        });
    }

    public void deleteAccountAnonymize(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        String previousEmail = user.getEmail();
        mailNotificationService.sendFarewellAnonymized(previousEmail);
        user.setEmail("deleted-" + user.getId() + "@invalid.local");
        user.setFirstName("Usuario");
        user.setLastName("Eliminado");
        user.setPhone(null);
        user.setActive(false);
        user.setEmailVerified(false);
        user.setPasswordHash(passwordEncoder.encode(UUID.randomUUID().toString()));
        bumpCredentialVersion(user);
        userRepository.save(user);
    }

    private void bumpCredentialVersion(User user) {
        user.setAuthVersion(user.getAuthVersion() + 1);
        user.setPasswordChangedAt(LocalDateTime.now());
    }

    private void recordPasswordHistory(User user, String encodedHash) {
        UserPasswordHistory h = new UserPasswordHistory();
        h.setUser(user);
        h.setPasswordHash(encodedHash);
        passwordHistoryRepository.save(h);

        List<UserPasswordHistory> all = passwordHistoryRepository.findByUser_IdOrderByCreatedAtDesc(user.getId());
        if (all.size() > PASSWORD_HISTORY_DEPTH) {
            for (int i = PASSWORD_HISTORY_DEPTH; i < all.size(); i++) {
                passwordHistoryRepository.deleteById(all.get(i).getId());
            }
        }
    }

    private boolean matchesRecentPasswords(User user, String plain) {
        List<UserPasswordHistory> recent = passwordHistoryRepository.findByUser_IdOrderByCreatedAtDesc(user.getId());
        int limit = Math.min(recent.size(), PASSWORD_HISTORY_DEPTH);
        for (int i = 0; i < limit; i++) {
            if (passwordEncoder.matches(plain, recent.get(i).getPasswordHash())) {
                return true;
            }
        }
        return false;
    }
}
