package com.recicar.marketplace.service.auth;

import com.recicar.marketplace.entity.EmailVerificationToken;
import com.recicar.marketplace.entity.User;
import com.recicar.marketplace.repository.EmailVerificationTokenRepository;
import com.recicar.marketplace.repository.UserRepository;
import com.recicar.marketplace.service.notification.AuthMailNotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class EmailVerificationService {

    private static final int TTL_HOURS = 24;

    private final EmailVerificationTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final AuthMailNotificationService mailNotificationService;

    public EmailVerificationService(
            EmailVerificationTokenRepository tokenRepository,
            UserRepository userRepository,
            AuthMailNotificationService mailNotificationService) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
        this.mailNotificationService = mailNotificationService;
    }

    @Transactional
    public void issueTokenAndSendEmail(User user) {
        EmailVerificationToken record = new EmailVerificationToken();
        record.setToken(UUID.randomUUID());
        record.setUser(user);
        record.setExpiresAt(LocalDateTime.now().plusHours(TTL_HOURS));
        record.setUsed(false);
        tokenRepository.save(record);
        mailNotificationService.sendEmailVerification(user, "/verify-email?token=" + record.getToken());
    }

    @Transactional
    public Optional<User> verifyAndActivate(String tokenStr) {
        UUID uuid;
        try {
            uuid = UUID.fromString(tokenStr.trim());
        } catch (Exception e) {
            return Optional.empty();
        }
        Optional<EmailVerificationToken> rowOpt = tokenRepository.findByToken(uuid);
        if (rowOpt.isEmpty()) {
            return Optional.empty();
        }
        EmailVerificationToken row = rowOpt.get();
        if (row.isUsed() || row.getExpiresAt().isBefore(LocalDateTime.now())) {
            return Optional.empty();
        }
        User user = userRepository.findById(row.getUser().getId())
                .orElseThrow();
        user.setEmailVerified(true);
        row.setUsed(true);
        tokenRepository.save(row);
        userRepository.save(user);
        return Optional.of(user);
    }
}
