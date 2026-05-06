package com.recicar.marketplace.controller;

import com.recicar.marketplace.entity.User;
import com.recicar.marketplace.repository.UserRepository;
import com.recicar.marketplace.service.SearchService;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class SearchApiControllerSavedSearchMvcTest {

    @Mock
    private SearchService searchService;

    @Mock
    private UserRepository userRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
        mockMvc = MockMvcBuilders.standaloneSetup(new SearchApiController(searchService, userRepository))
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
    }

    @Test
    void delete_savedSearch_returns401WhenAnonymous() throws Exception {
        mockMvc.perform(delete("/api/v1/user/saved-searches/1"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void delete_savedSearch_returns204ForOwner() throws Exception {
        UserDetails principal = org.springframework.security.core.userdetails.User
                .withUsername("cust@example.com")
                .password("x")
                .roles("USER")
                .build();
        User u = new User();
        u.setId(12L);
        when(userRepository.findByEmailIgnoreCase("cust@example.com")).thenReturn(Optional.of(u));

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        try {
            mockMvc.perform(delete("/api/v1/user/saved-searches/66"))
                    .andExpect(status().isNoContent());
        } finally {
            SecurityContextHolder.clearContext();
        }

        verify(searchService).deleteSavedSearch(eq(12L), eq(66L));
    }
}
