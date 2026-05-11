package com.recicar.marketplace.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration coverage for warranties-and-policies OpenSpec tasks 1.7-5.7.
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class LegalInfoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shippingInfoPage_rendersIntegratedLegalVersion() throws Exception {
        mockMvc.perform(get("/info-de-envio"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Información de envío")))
                .andExpect(content().string(containsString("Versión vinculante:")))
                .andExpect(content().string(containsString("v2026.05")));
    }

    @Test
    void paymentInfoPage_rendersIntegratedLegalVersion() throws Exception {
        mockMvc.perform(get("/info-de-pago"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Información de pago")))
                .andExpect(content().string(containsString("Versión vinculante:")))
                .andExpect(content().string(containsString("v2026.05")));
    }

    @Test
    void returnPolicyPage_rendersIntegratedLegalVersion() throws Exception {
        mockMvc.perform(get("/politica-de-devolucion"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("14 días naturales")))
                .andExpect(content().string(containsString("Versión vinculante:")))
                .andExpect(content().string(containsString("v2026.05")));
    }

    @Test
    void termsPage_rendersIntegratedLegalVersion() throws Exception {
        mockMvc.perform(get("/terminos-de-uso"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Términos de uso")))
                .andExpect(content().string(containsString("Versión vinculante:")))
                .andExpect(content().string(containsString("v2026.05")));
    }

    @Test
    void privacyPage_rendersIntegratedLegalVersion() throws Exception {
        mockMvc.perform(get("/politica-de-privacidad"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Política de privacidad")))
                .andExpect(content().string(containsString("Versión vinculante:")))
                .andExpect(content().string(containsString("v2026.05")));
    }
}
