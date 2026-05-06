package com.recicar.marketplace.controller;

import com.recicar.marketplace.entity.User;
import com.recicar.marketplace.entity.UserRole;
import com.recicar.marketplace.repository.UserRepository;
import com.recicar.marketplace.service.VendorConversationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class UserConversationApiControllerWebMvcTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private VendorConversationService conversationService;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
        mockMvc = MockMvcBuilders.standaloneSetup(
                new UserConversationApiController(userRepository, conversationService))
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
    }

    @Test
    void listMessages_returns404WhenNotParticipant() throws Exception {
        UserDetails principal = org.springframework.security.core.userdetails.User
                .withUsername("buyer@example.com")
                .password("x")
                .roles("USER")
                .build();

        User u = new User();
        u.setId(5L);
        u.setRole(UserRole.CUSTOMER);
        when(userRepository.findByEmailIgnoreCase("buyer@example.com")).thenReturn(Optional.of(u));
        when(conversationService.getConversationIfParticipant(eq(999L), eq(u))).thenReturn(Optional.empty());

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        try {
            mockMvc.perform(get("/api/v1/user/conversations/999/messages")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
