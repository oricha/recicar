package com.recicar.marketplace.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@org.springframework.test.context.ActiveProfiles("test")
@AutoConfigureMockMvc
class WishlistApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void addAndRemoveWishlistItem_maintainsCountInSession() throws Exception {
        // Add one item
        var session = mockMvc.perform(post("/api/wishlist/items")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("productId", "101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1))
                .andReturn().getRequest().getSession(false);

        // Add another with same session
        mockMvc.perform(post("/api/wishlist/items")
                        .session((org.springframework.mock.web.MockHttpSession) session)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("productId", "202"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(2));

        // Remove one
        mockMvc.perform(delete("/api/wishlist/items/101")
                        .session((org.springframework.mock.web.MockHttpSession) session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1));
    }
}
