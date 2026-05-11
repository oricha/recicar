package com.recicar.marketplace.controller;

import com.recicar.marketplace.entity.ContactMessage;
import com.recicar.marketplace.repository.ContactMessageRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Integration coverage for support-help-section OpenSpec tasks 1.7-5.7.
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
@TestPropertySource(properties = "app.support.email=soporte-test@recicar.es")
class HelpSupportIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ContactMessageRepository contactMessageRepository;

    @Test
    void helpCenterPage_rendersSupportHubLinksAndInbox() throws Exception {
        mockMvc.perform(get("/help"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Centro de ayuda")))
                .andExpect(view().name("support/help-center"))
                .andExpect(content().string(containsString("viewport")));
    }

    @Test
    void faqPageAndApi_renderSeededCategoriesAndEntries() throws Exception {
        mockMvc.perform(get("/faq"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Preguntas frecuentes")))
                .andExpect(view().name("support/faq"));

        mockMvc.perform(get("/api/v1/support/faqs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(4)))
                .andExpect(jsonPath("$[0].slug").value("comprar"))
                .andExpect(jsonPath("$[0].entries[0].question")
                        .value("¿Cómo busco una pieza compatible con mi coche?"));
    }

    @Test
    void blogListApiAndDetail_renderPublishedContent() throws Exception {
        mockMvc.perform(get("/blog"))
                .andExpect(status().isOk())
                .andExpect(view().name("support/blog-list"));

        mockMvc.perform(get("/api/v1/support/blog"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].slug").exists())
                .andExpect(jsonPath("$[0].title").isNotEmpty());

        mockMvc.perform(get("/blog/elegir-pieza-segunda-mano"))
                .andExpect(status().isOk())
                .andExpect(view().name("support/blog-post"));
    }

    @Test
    void contactSubmit_persistsMessageAndRedirectsWithSuccessFlash() throws Exception {
        mockMvc.perform(post("/contact")
                        .with(csrf())
                        .param("name", "Cliente Test")
                        .param("email", "cliente@example.com")
                        .param("subject", "Necesito ayuda con un pedido")
                        .param("message", "Mi pedido no muestra seguimiento todavía."))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/contact"))
                .andExpect(flash().attribute("success", containsString("Gracias. Hemos recibido tu mensaje.")));

        List<ContactMessage> messages = contactMessageRepository.findAll();
        ContactMessage saved = messages.get(messages.size() - 1);
        Assertions.assertEquals("Cliente Test", saved.getName());
        Assertions.assertEquals("cliente@example.com", saved.getEmail());
        Assertions.assertEquals("Necesito ayuda con un pedido", saved.getSubject());
        Assertions.assertEquals("Mi pedido no muestra seguimiento todavía.", saved.getMessage());
    }

    @Test
    void supportInfoApi_exposesConfiguredInbox() throws Exception {
        mockMvc.perform(get("/api/v1/support/info"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.supportEmail").value("soporte-test@recicar.es"));
    }
}
