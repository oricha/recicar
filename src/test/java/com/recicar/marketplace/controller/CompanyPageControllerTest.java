package com.recicar.marketplace.controller;

import com.recicar.marketplace.config.MvcSliceTestConfig;
import com.recicar.marketplace.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = CompanyPageController.class)
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
@Import(MvcSliceTestConfig.class)
class CompanyPageControllerTest {

    @MockBean
    private CategoryService categoryService;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void acercaDeNosotrosRenders() throws Exception {
        mockMvc.perform(get("/acerca-de-nosotros"))
                .andExpect(status().isOk())
                .andExpect(view().name("company/acerca-de-nosotros"));
    }

    @Test
    void aboutRedirects() throws Exception {
        mockMvc.perform(get("/about"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/acerca-de-nosotros"));
    }
}
