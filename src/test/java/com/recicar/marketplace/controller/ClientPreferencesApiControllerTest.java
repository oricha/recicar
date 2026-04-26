package com.recicar.marketplace.controller;

import com.recicar.marketplace.config.MvcSliceTestConfig;
import com.recicar.marketplace.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ClientPreferencesApiController.class)
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
@Import(MvcSliceTestConfig.class)
class ClientPreferencesApiControllerTest {

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getReturnsRegionAndVat() throws Exception {
        mockMvc.perform(get("/api/v1/client-preferences"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.region").exists())
                .andExpect(jsonPath("$.includeVat").exists())
                .andExpect(jsonPath("$.regions").isArray());
    }

    @Test
    void postUpdatesAndReturnsJson() throws Exception {
        mockMvc.perform(post("/api/v1/client-preferences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"region\":\"FR\",\"includeVat\":true}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.region").value("FR"))
                .andExpect(jsonPath("$.includeVat").value(true));
    }
}
