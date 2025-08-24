package com.recicar.marketplace.security;

import com.recicar.marketplace.entity.User;
import com.recicar.marketplace.entity.UserRole;
import com.recicar.marketplace.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.authenticated;
import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
class SecurityConfigTest {

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();
    }

    @Test
    void shouldAllowAccessToPublicEndpoints() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/login"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/register"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldRedirectToLoginForProtectedEndpoints() throws Exception {
        mockMvc.perform(get("/profile"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        mockMvc.perform(get("/vendor/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));

        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }

    @Test
    void shouldAuthenticateValidUser() throws Exception {
        // Create a test user
        User user = new User();
        user.setEmail("test@example.com");
        user.setPasswordHash(passwordEncoder.encode("password123"));
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRole(UserRole.CUSTOMER);
        user.setActive(true);
        user.setEmailVerified(true);
        userRepository.save(user);

        mockMvc.perform(formLogin("/login")
                .user("email", "test@example.com")
                .password("password", "password123"))
                .andExpect(authenticated().withUsername("test@example.com"));
    }

    @Test
    void shouldRejectInvalidCredentials() throws Exception {
        mockMvc.perform(formLogin("/login")
                .user("email", "invalid@example.com")
                .password("password", "wrongpassword"))
                .andExpect(unauthenticated())
                .andExpect(redirectedUrl("/login?error"));
    }

    @Test
    void shouldRejectInactiveUser() throws Exception {
        // Create an inactive user
        User user = new User();
        user.setEmail("inactive@example.com");
        user.setPasswordHash(passwordEncoder.encode("password123"));
        user.setFirstName("Inactive");
        user.setLastName("User");
        user.setRole(UserRole.CUSTOMER);
        user.setActive(false); // Inactive user
        user.setEmailVerified(true);
        userRepository.save(user);

        mockMvc.perform(formLogin("/login")
                .user("email", "inactive@example.com")
                .password("password", "password123"))
                .andExpect(unauthenticated());
    }

    @Test
    void shouldRejectUnverifiedUser() throws Exception {
        // Create an unverified user
        User user = new User();
        user.setEmail("unverified@example.com");
        user.setPasswordHash(passwordEncoder.encode("password123"));
        user.setFirstName("Unverified");
        user.setLastName("User");
        user.setRole(UserRole.CUSTOMER);
        user.setActive(true);
        user.setEmailVerified(false); // Unverified email
        userRepository.save(user);

        mockMvc.perform(formLogin("/login")
                .user("email", "unverified@example.com")
                .password("password", "password123"))
                .andExpect(unauthenticated());
    }

    @Test
    void shouldAllowUserRegistration() throws Exception {
        mockMvc.perform(post("/register")
                .param("firstName", "John")
                .param("lastName", "Doe")
                .param("email", "john.doe@example.com")
                .param("password", "password123")
                .param("confirmPassword", "password123")
                .param("agreeToTerms", "true")
                .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        // Verify user was created
        var user = userRepository.findByEmailIgnoreCase("john.doe@example.com");
        assert user.isPresent();
        assert user.get().getFirstName().equals("John");
        assert user.get().getRole() == UserRole.CUSTOMER;
    }

    @Test
    void shouldRejectRegistrationWithExistingEmail() throws Exception {
        // Create existing user
        User existingUser = new User();
        existingUser.setEmail("existing@example.com");
        existingUser.setPasswordHash(passwordEncoder.encode("password"));
        existingUser.setFirstName("Existing");
        existingUser.setLastName("User");
        existingUser.setRole(UserRole.CUSTOMER);
        userRepository.save(existingUser);

        mockMvc.perform(post("/register")
                .param("firstName", "John")
                .param("lastName", "Doe")
                .param("email", "existing@example.com")
                .param("password", "password123")
                .param("confirmPassword", "password123")
                .param("agreeToTerms", "true")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("auth/register"))
                .andExpect(model().hasErrors());
    }
}