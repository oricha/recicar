package com.recicar.marketplace.controller;

import com.recicar.marketplace.config.MvcSliceTestConfig;
import com.recicar.marketplace.service.CategoryService;
import com.recicar.marketplace.service.ContactMessageService;
import com.recicar.marketplace.service.SupportContentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = HelpSupportController.class)
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
@Import(MvcSliceTestConfig.class)
class HelpSupportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SupportContentService supportContentService;

    @MockBean
    private ContactMessageService contactMessageService;

    @MockBean
    private CategoryService categoryService;

    @Test
    void helpCenterRenders() throws Exception {
        mockMvc.perform(get("/help"))
                .andExpect(status().isOk())
                .andExpect(view().name("support/help-center"));
    }

    @Test
    void faqRenders() throws Exception {
        when(supportContentService.listFaqCategories()).thenReturn(List.of());
        mockMvc.perform(get("/faq"))
                .andExpect(status().isOk())
                .andExpect(view().name("support/faq"));
    }

    @Test
    void blogRenders() throws Exception {
        when(supportContentService.listPublishedBlogSummaries(anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(List.of()));
        mockMvc.perform(get("/blog"))
                .andExpect(status().isOk())
                .andExpect(view().name("support/blog-list"));
    }

    @Test
    void contactRenders() throws Exception {
        mockMvc.perform(get("/contact"))
                .andExpect(status().isOk())
                .andExpect(view().name("support/contact"));
    }

    @Test
    void contactosAliasRenders() throws Exception {
        mockMvc.perform(get("/contactos"))
                .andExpect(status().isOk())
                .andExpect(view().name("support/contact"));
    }
}
