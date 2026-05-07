package com.recicar.marketplace.controller;

import com.recicar.marketplace.entity.User;
import com.recicar.marketplace.entity.UserRole;
import com.recicar.marketplace.repository.UserRepository;
import com.recicar.marketplace.service.OrderService;
import com.recicar.marketplace.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RegisteredUserApiControllerWebMvcTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private OrderService orderService;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
        RegisteredUserApiController controller = new RegisteredUserApiController(
                userRepository, userService, orderService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
    }

    @Test
    void getProfile_returnsAccountJson() throws Exception {
        UserDetails principal = org.springframework.security.core.userdetails.User
                .withUsername("who@example.com")
                .password("x")
                .roles("USER")
                .build();
        User u = new User();
        u.setId(9L);
        u.setFirstName("Pat");
        u.setLastName("Lee");
        u.setEmail("who@example.com");
        u.setPhone("+341");
        u.setRole(UserRole.CUSTOMER);

        when(userRepository.findByEmailIgnoreCase(eq("who@example.com"))).thenReturn(Optional.of(u));

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        try {
            mockMvc.perform(get("/api/v1/user/profile"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.email").value("who@example.com"))
                    .andExpect(jsonPath("$.firstName").value("Pat"));
        } finally {
            SecurityContextHolder.clearContext();
        }
    }

    @Test
    void getProfile_returns401WhenAnonymous() throws Exception {
        mockMvc.perform(get("/api/v1/user/profile"))
                .andExpect(status().isUnauthorized());
    }
}
