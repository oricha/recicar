package com.recicar.marketplace.controller;

import com.recicar.marketplace.config.MvcSliceTestConfig;
import com.recicar.marketplace.dto.BrandListItemDto;
import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.service.CategoryService;
import com.recicar.marketplace.service.ProductService;
import com.recicar.marketplace.service.SearchFilterOptionsService;
import com.recicar.marketplace.service.SearchService;
import com.recicar.marketplace.web.ShopListingConstants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = SearchController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(MvcSliceTestConfig.class)
class SearchControllerAdvancedWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private SearchService searchService;

    @MockBean
    private SearchFilterOptionsService searchFilterOptionsService;

    @Test
    void advanced_withoutSubmit_showsForm() throws Exception {
        when(searchFilterOptionsService.listBrandOptions()).thenReturn(List.of(
                new BrandListItemDto(1L, "Toyota", "toyota", "")
        ));
        when(categoryService.findRootCategories()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/search/advanced"))
                .andExpect(status().isOk())
                .andExpect(view().name("search-advanced"))
                .andExpect(model().attribute("searchExecuted", false));
    }

    @Test
    void advanced_submitted_runsSearch() throws Exception {
        when(searchFilterOptionsService.listBrandOptions()).thenReturn(Collections.emptyList());
        when(categoryService.findRootCategories()).thenReturn(Collections.emptyList());
        Product p = new Product();
        p.setId(9L);
        p.setName("Filtro");
        when(searchService.searchAdvanced(any(), any(), any(), any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(p), PageRequest.of(0, ShopListingConstants.PAGE_SIZE), 1));

        mockMvc.perform(get("/search/advanced")
                        .param("submitted", "true")
                        .param("q", "filtro"))
                .andExpect(status().isOk())
                .andExpect(view().name("search-advanced"))
                .andExpect(model().attribute("searchExecuted", true))
                .andExpect(model().attributeExists("advPages"));
    }
}
