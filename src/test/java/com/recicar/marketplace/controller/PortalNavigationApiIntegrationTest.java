package com.recicar.marketplace.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PortalNavigationApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void publicNavigationApis_returnCatalogData() throws Exception {
        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(greaterThanOrEqualTo(20)));

        mockMvc.perform(get("/api/v1/categories/motor"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("motor"))
                .andExpect(jsonPath("$.children.length()").value(greaterThanOrEqualTo(2)));

        mockMvc.perform(get("/api/v1/categories/motor-bloque-cigueñal-cojinetes/hierarchy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(greaterThanOrEqualTo(4)));

        mockMvc.perform(get("/api/v1/brands"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(greaterThanOrEqualTo(200)));

        mockMvc.perform(get("/api/v1/brands/bmw/models"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(greaterThanOrEqualTo(2)));

        mockMvc.perform(get("/api/v1/regions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(greaterThanOrEqualTo(2)))
                .andExpect(jsonPath("$[0].code").isNotEmpty());
    }

    @Test
    void userPreferenceApis_requireAuthentication() throws Exception {
        mockMvc.perform(post("/api/v1/user/preferences/region")
                        .contentType("application/json")
                        .content("{\"region\":\"DE\"}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "user@example.com")
    void userPreferenceApis_persistCookiePayloadsForAuthenticatedSessions() throws Exception {
        mockMvc.perform(post("/api/v1/user/preferences/region")
                        .contentType("application/json")
                        .content("{\"region\":\"FR\"}"))
                .andExpect(status().isOk())
                .andExpect(cookie().value("mkt_region", "FR"))
                .andExpect(jsonPath("$.region").value("FR"));

        mockMvc.perform(patch("/api/v1/user/preferences/price-format")
                        .contentType("application/json")
                        .content("{\"includeVat\":false}"))
                .andExpect(status().isOk())
                .andExpect(cookie().value("mkt_vat", "0"))
                .andExpect(jsonPath("$.includeVat").value(false));
    }
}
