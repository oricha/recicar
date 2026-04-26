package com.recicar.marketplace.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import com.recicar.marketplace.service.CategoryService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = LegalInfoController.class)
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
class LegalInfoControllerTest {

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void infoEnvio_ok() throws Exception {
        mockMvc.perform(get("/info-de-envio"))
                .andExpect(status().isOk())
                .andExpect(view().name("info-de-envio"))
                .andExpect(model().attribute("pageTitle", "Información de envío"));
    }

    @Test
    void politicaPrivacidad_bothPaths() throws Exception {
        mockMvc.perform(get("/politica-de-privacidad"))
                .andExpect(status().isOk())
                .andExpect(view().name("politica-de-privacidad"));
        mockMvc.perform(get("/privacy-policy"))
                .andExpect(status().isOk())
                .andExpect(view().name("politica-de-privacidad"));
    }

    @Test
    void garantias_aliasWarranty() throws Exception {
        mockMvc.perform(get("/garantias"))
                .andExpect(status().isOk())
                .andExpect(view().name("garantias"));
        mockMvc.perform(get("/warranty"))
                .andExpect(status().isOk())
                .andExpect(view().name("garantias"));
    }
}
