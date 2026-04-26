package com.recicar.marketplace.controller;

import com.recicar.marketplace.service.CategoryService;
import com.recicar.marketplace.service.PartCodeReferenceService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = ContentSeoController.class)
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
class ContentSeoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PartCodeReferenceService partCodeReferenceService;

    @MockBean
    private CategoryService categoryService;

    @Test
    void sitemapAliasRedirects() throws Exception {
        mockMvc.perform(get("/sitemap"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/sitemap.xml"));
    }
}
