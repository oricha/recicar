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
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@ExtendWith(MockitoExtension.class)
class SavedSearchesPageControllerWebMvcTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SearchService searchService;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");
        mockMvc = MockMvcBuilders.standaloneSetup(new SavedSearchesPageController(userRepository, searchService))
                .setViewResolvers(viewResolver)
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
    }

    @Test
    void savedSearchesPage_rendersView() throws Exception {
        UserDetails principal = org.springframework.security.core.userdetails.User
                .withUsername("save@example.com")
                .password("x")
                .roles("USER")
                .build();
        User u = new User();
        u.setId(3L);
        when(userRepository.findByEmailIgnoreCase("save@example.com")).thenReturn(Optional.of(u));
        when(searchService.getSavedSearches(eq(3L))).thenReturn(List.of());

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        try {
            mockMvc.perform(get("/saved-searches"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("saved-searches"));
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
