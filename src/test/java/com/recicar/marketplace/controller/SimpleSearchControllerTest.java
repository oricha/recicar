package com.recicar.marketplace.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Simple test to verify the search controller basic functionality
 */
@SpringBootTest
@ActiveProfiles("test")
class SimpleSearchControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Test
    void contextLoads() {
        assertNotNull(webApplicationContext);
    }

    @Test
    void searchEndpointExists() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        // Test with a short search term - should return template with error
        mockMvc.perform(get("/products/search")
                        .param("q", "a"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/search-results"));
    }

    @Test
    void partSearchEndpointExists() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        // Test with a short part number - should return template with error
        mockMvc.perform(get("/products/part/a"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/part-search"));
    }

    @Test
    void oemSearchEndpointExists() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        // Test with a short OEM number - should return template with error
        mockMvc.perform(get("/products/oem/a"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/part-search"));
    }

    @Test
    void vehicleSearchEndpointExists() throws Exception {
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        
        // Test with a short make - should return template with error
        mockMvc.perform(get("/products/vehicle")
                        .param("make", "T")
                        .param("model", "Camry")
                        .param("year", "2020"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/vehicle-compatibility"));
    }
}
