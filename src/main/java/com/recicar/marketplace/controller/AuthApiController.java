package com.recicar.marketplace.controller;

import com.recicar.marketplace.dto.LoginForm;
import com.recicar.marketplace.dto.UserRegistrationDto;
import com.recicar.marketplace.entity.User;
import com.recicar.marketplace.service.UserService;
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
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthApiController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public AuthApiController(AuthenticationManager authenticationManager, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginForm loginForm, HttpServletRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginForm.getEmail(), loginForm.getPassword())
            );

            SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
            securityContext.setAuthentication(authentication);
            SecurityContextHolder.setContext(securityContext);

            HttpSession session = request.getSession(true);
            session.setAttribute("SPRING_SECURITY_CONTEXT", securityContext);

            return ResponseEntity.ok(Map.of("message", "Login successful"));
        } catch (AuthenticationException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid email or password"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegistrationDto registrationDto) {
        if (!registrationDto.getPassword().equals(registrationDto.getConfirmPassword())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Passwords do not match"));
        }
        if (userService.existsByEmail(registrationDto.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Email already registered"));
        }

        User created = userService.registerUser(registrationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "id", created.getId(),
                "email", created.getEmail(),
                "role", created.getRole().name()
        ));
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
                        "role", user.getRole().name()
                )))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Not authenticated")));
    }
}
