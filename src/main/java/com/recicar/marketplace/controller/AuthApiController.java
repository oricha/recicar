package com.recicar.marketplace.controller;

import com.recicar.marketplace.dto.ChangePasswordRequest;
import com.recicar.marketplace.dto.ForgotPasswordRequest;
import com.recicar.marketplace.dto.LoginForm;
import com.recicar.marketplace.dto.ResetPasswordApiRequest;
import com.recicar.marketplace.dto.UserRegistrationDto;
import com.recicar.marketplace.entity.User;
import com.recicar.marketplace.security.SessionAuthVersionFilter;
import com.recicar.marketplace.service.CustomUserDetailsService;
import com.recicar.marketplace.service.PostLoginActionsService;
import com.recicar.marketplace.service.UserService;
import com.recicar.marketplace.service.auth.AuthLoginAuditService;
import com.recicar.marketplace.service.auth.EmailVerificationService;
import com.recicar.marketplace.service.auth.InMemoryAuthRateLimiter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthApiController {

    private static final Duration LOGIN_WINDOW = Duration.ofMinutes(15);
    private static final Duration REGISTER_WINDOW = Duration.ofHours(1);
    private static final int LOGIN_MAX_PER_IP = 5;
    private static final int REGISTER_MAX_PER_IP = 3;

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final PostLoginActionsService postLoginActionsService;
    private final EmailVerificationService emailVerificationService;
    private final InMemoryAuthRateLimiter rateLimiter;
    private final AuthLoginAuditService authLoginAuditService;

    public AuthApiController(
            AuthenticationManager authenticationManager,
            UserService userService,
            PostLoginActionsService postLoginActionsService,
            EmailVerificationService emailVerificationService,
            InMemoryAuthRateLimiter rateLimiter,
            AuthLoginAuditService authLoginAuditService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
        this.postLoginActionsService = postLoginActionsService;
        this.emailVerificationService = emailVerificationService;
        this.rateLimiter = rateLimiter;
        this.authLoginAuditService = authLoginAuditService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginForm loginForm, HttpServletRequest request) {
        String ip = clientIp(request);
        String bucket = "login:" + ip;
        if (!rateLimiter.allow(bucket, LOGIN_MAX_PER_IP, LOGIN_WINDOW)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("message", "Demasiados intentos. Espera unos minutos."));
        }
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginForm.getEmail(), loginForm.getPassword()));

            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(securityContext);

            HttpSession session = request.getSession(true);
            session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, securityContext);

            if (authentication.getPrincipal() instanceof CustomUserDetailsService.CustomUserPrincipal p) {
                session.setAttribute(SessionAuthVersionFilter.SESSION_AUTH_VERSION_ATTR, p.getUser().getAuthVersion());
                authLoginAuditService.record(true, loginForm.getEmail(), p.getUserId(), request);
                postLoginActionsService.mergeGuestDataIntoUserSession(session, p.getUsername());
                userService.resetFailedLoginsForEmail(loginForm.getEmail());
            }

            return ResponseEntity.ok(Map.of("message", "Autenticación correcta"));
        } catch (AuthenticationException ex) {
            userService.recordFailedLoginForEmail(loginForm.getEmail());
            authLoginAuditService.record(false, loginForm.getEmail(), null, request);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Email o contraseña incorrectos"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegistrationDto registrationDto, HttpServletRequest request) {
        String ip = clientIp(request);
        if (!rateLimiter.allow("reg:" + ip, REGISTER_MAX_PER_IP, REGISTER_WINDOW)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("message", "Demasiados registros desde esta IP."));
        }
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Las contraseñas no coinciden"));
        }
        if (userService.existsByEmail(registrationDto.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "No se pudo completar el registro."));
        }
        try {
            User created = userService.registerUser(registrationDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                    "message", "Revisa tu correo para verificar la cuenta.",
                    "id", created.getId()
            ));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        SecurityContextHolder.clearContext();
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmailApi(@RequestParam("token") String token) {
        return emailVerificationService.verifyAndActivate(token)
                .<ResponseEntity<?>>map(u ->
                        ResponseEntity.ok(Map.of("verified", true, "email", u.getEmail())))
                .orElseGet(() -> ResponseEntity.badRequest().body(Map.of("verified", false)));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgotPasswordRequest body, HttpServletRequest request) {
        String ip = clientIp(request);
        if (!rateLimiter.allow("forgot:" + ip, 5, Duration.ofHours(1))) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("message", "Demasiadas solicitudes."));
        }
        userService.initiatePasswordReset(body.getEmail());
        return ResponseEntity.accepted()
                .body(Map.of("message", "Si existe una cuenta con ese correo, recibirás instrucciones."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody ResetPasswordApiRequest body) {
        if (!body.getNewPassword().equals(body.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Las contraseñas no coinciden"));
        }
        boolean ok = userService.resetPassword(body.getToken(), body.getNewPassword());
        if (!ok) {
            return ResponseEntity.badRequest().body(Map.of("message", "Token inválido o caducado."));
        }
        return ResponseEntity.ok(Map.of("message", "Contraseña actualizada."));
    }

    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerification(@RequestBody ForgotPasswordRequest body, HttpServletRequest request) {
        String ip = clientIp(request);
        if (!rateLimiter.allow("resend:" + ip, 3, Duration.ofHours(1))) {
            return ResponseEntity.accepted().body(Map.of("message", "Si procede, enviaremos un correo."));
        }
        userService.findByEmail(body.getEmail()).ifPresent(u -> {
            if (!u.isEmailVerified()) {
                emailVerificationService.issueTokenAndSendEmail(u);
            }
        });
        return ResponseEntity.accepted()
                .body(Map.of("message", "Si existe la cuenta y no está verificada, enviamos un nuevo enlace."));
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Not authenticated"));
        }
        return userService.findByEmail(userDetails.getUsername())
                .<ResponseEntity<?>>map(user -> ResponseEntity.ok(Map.of(
                        "id", user.getId(),
                        "email", user.getEmail(),
                        "firstName", user.getFirstName(),
                        "lastName", user.getLastName(),
                        "role", user.getRole().name(),
                        "emailVerified", user.isEmailVerified()
                )))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Not authenticated")));
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody ChangePasswordRequest body) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        try {
            User u = userService.findByEmail(userDetails.getUsername()).orElseThrow();
            userService.changePassword(u.getId(), body.getCurrentPassword(), body.getNewPassword());
            return ResponseEntity.ok(Map.of("message", "Contraseña actualizada."));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(Map.of("message", ex.getMessage()));
        }
    }

    @PostMapping("/sessions/revoke")
    public ResponseEntity<?> revokeSessions(@AuthenticationPrincipal UserDetails userDetails, HttpServletRequest request) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User u = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        userService.revokeAllSessions(u.getId());
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(Map.of("message", "Sesiones cerradas."));
    }

    @DeleteMapping("/account")
    public ResponseEntity<?> deleteAccount(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        User u = userService.findByEmail(userDetails.getUsername()).orElseThrow();
        userService.deleteAccountAnonymize(u.getId());
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok(Map.of("message", "Cuenta anonimizada."));
    }

    private static String clientIp(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
