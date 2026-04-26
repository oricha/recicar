package com.recicar.marketplace.controller;

import com.recicar.marketplace.config.MvcSliceTestConfig;
import com.recicar.marketplace.service.CategoryService;
import com.recicar.marketplace.service.SearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = SearchSuggestionCompatController.class)
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
@Import(MvcSliceTestConfig.class)
class SearchSuggestionCompatControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SearchService searchService;

    @MockBean
    private CategoryService categoryService;

    @Test
    void usesQueryParamQ() throws Exception {
        when(searchService.getSearchSuggestions("bo")).thenReturn(List.of("bomba", "bol"));
        mockMvc.perform(get("/api/search/suggestions").param("q", "bo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("bomba"));
    }

    @Test
    void usesLegacyQueryParam() throws Exception {
        when(searchService.getSearchSuggestions("xy")).thenReturn(List.of("xy"));
        mockMvc.perform(get("/api/search/suggestions").param("query", "xy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value("xy"));
    }
}
