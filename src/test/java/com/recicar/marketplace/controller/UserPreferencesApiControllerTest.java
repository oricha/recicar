package com.recicar.marketplace.controller;

import com.recicar.marketplace.config.MvcSliceTestConfig;
import com.recicar.marketplace.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserPreferencesApiController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(MvcSliceTestConfig.class)
class UserPreferencesApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Test
    void regionPreference_setsCookiePayload() throws Exception {
        mockMvc.perform(post("/api/v1/user/preferences/region")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"region\":\"FR\"}"))
                .andExpect(status().isOk())
                .andExpect(cookie().value("mkt_region", "FR"))
                .andExpect(jsonPath("$.region").value("FR"));
    }

    @Test
    void priceFormatPreference_setsVatCookie() throws Exception {
        mockMvc.perform(patch("/api/v1/user/preferences/price-format")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"includeVat\":false}"))
                .andExpect(status().isOk())
                .andExpect(cookie().value("mkt_vat", "0"))
                .andExpect(jsonPath("$.includeVat").value(false));
    }
}
