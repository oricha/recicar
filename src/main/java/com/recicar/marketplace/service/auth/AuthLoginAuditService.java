package com.recicar.marketplace.service.auth;

import com.recicar.marketplace.entity.AuthLoginAudit;
import com.recicar.marketplace.entity.User;
import com.recicar.marketplace.repository.AuthLoginAuditRepository;
import com.recicar.marketplace.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthLoginAuditService {

    private final AuthLoginAuditRepository authLoginAuditRepository;
    private final UserRepository userRepository;

    public AuthLoginAuditService(
            AuthLoginAuditRepository authLoginAuditRepository,
            UserRepository userRepository) {
        this.authLoginAuditRepository = authLoginAuditRepository;
        this.userRepository = userRepository;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void record(boolean success, String emailAttempt, Long userId, HttpServletRequest request) {
        AuthLoginAudit row = new AuthLoginAudit();
        row.setSuccess(success);
        row.setEmailAttempt(emailAttempt != null ? emailAttempt : "");
        if (userId != null) {
            User u = userRepository.getReferenceById(userId);
            row.setUser(u);
        }
        if (request != null) {
            row.setIpAddress(request.getRemoteAddr());
            String ua = request.getHeader("User-Agent");
            if (ua != null && ua.length() > 512) {
                ua = ua.substring(0, 512);
            }
            row.setUserAgent(ua);
        }
        authLoginAuditRepository.save(row);
    }
}
