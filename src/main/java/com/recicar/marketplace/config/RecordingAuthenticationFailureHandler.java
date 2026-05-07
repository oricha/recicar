package com.recicar.marketplace.config;

import com.recicar.marketplace.repository.UserRepository;
import com.recicar.marketplace.service.UserService;
import com.recicar.marketplace.service.auth.AuthLoginAuditService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import java.io.IOException;

/**
 * Records failed attempts, lockout, and audit trail for form login.
 */
public class RecordingAuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    private final UserService userService;
    private final AuthLoginAuditService authLoginAuditService;
    private final UserRepository userRepository;

    public RecordingAuthenticationFailureHandler(
            String failureUrl,
            UserService userService,
            AuthLoginAuditService authLoginAuditService,
            UserRepository userRepository) {
        super(failureUrl);
        this.userService = userService;
        this.authLoginAuditService = authLoginAuditService;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String email = request.getParameter("email");
        if (email != null && !email.isBlank()) {
            userService.recordFailedLoginForEmail(email);
            userRepository.findByEmailIgnoreCase(email)
                    .ifPresentOrElse(
                            u -> authLoginAuditService.record(false, email.toLowerCase(), u.getId(), request),
                            () -> authLoginAuditService.record(false, email.toLowerCase(), null, request));
        }
        super.onAuthenticationFailure(request, response, exception);
    }
}
