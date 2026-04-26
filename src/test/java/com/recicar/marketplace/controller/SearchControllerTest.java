package com.recicar.marketplace.controller;

import com.recicar.marketplace.config.MvcSliceTestConfig;
import com.recicar.marketplace.entity.Category;
import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.service.CategoryService;
import com.recicar.marketplace.service.ProductService;
import com.recicar.marketplace.service.SearchService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = SearchController.class)
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
@Import(MvcSliceTestConfig.class)
class SearchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private SearchService searchService;

    @Test
    public void testSearchByPartNumber() throws Exception {
        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPartNumber("12345");
        product.setCategory(category);

        when(productService.findByPartNumber("12345")).thenReturn(Collections.singletonList(product));
        when(categoryService.findRootCategories()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/search").param("query", "12345"))
                .andExpect(status().isOk())
                .andExpect(view().name("shop-list"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("searchQuery", "12345"));
    }

    @Test
    public void testSearchByOemNumber() throws Exception {
        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setOemNumber("54321");
        product.setCategory(category);

        when(productService.findByPartNumber("54321")).thenReturn(Collections.emptyList());
        when(productService.findByOemNumber("54321")).thenReturn(Collections.singletonList(product));
        when(categoryService.findRootCategories()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/search").param("query", "54321"))
                .andExpect(status().isOk())
                .andExpect(view().name("shop-list"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("searchQuery", "54321"));
    }

    @Test
    public void searchByBrandModel_delegatesToSearchService() throws Exception {
        when(searchService.searchAdvanced(anyString(), anyString(), anyString(), isNull(), isNull(), isNull(), isNull(), isNull(), any(Pageable.class)))
                .thenReturn(new org.springframework.data.domain.PageImpl<>(Collections.emptyList(), PageRequest.of(0, 12), 0));
        when(categoryService.findRootCategories()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/search").param("brand", "Audi").param("model", "A4"))
                .andExpect(status().isOk())
                .andExpect(view().name("shop-list"))
                .andExpect(model().attribute("searchType", "advancedVehicle"));

        verify(searchService).searchAdvanced(eq(""), eq("Audi"), eq("A4"), isNull(), isNull(), isNull(), isNull(), isNull(), any(Pageable.class));
    }

    @Test
    public void testGeneralSearch() throws Exception {
        Category category = new Category();
        category.setId(1L);
        category.setName("Test Category");
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setCategory(category);

        when(productService.findByPartNumber("test")).thenReturn(Collections.emptyList());
        when(productService.findByOemNumber("test")).thenReturn(Collections.emptyList());
        when(productService.searchProducts("test", PageRequest.of(0, 12))).thenReturn(new PageImpl<>(Collections.singletonList(product)));
        when(categoryService.findRootCategories()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/search").param("query", "test"))
                .andExpect(status().isOk())
                .andExpect(view().name("shop-list"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("searchQuery", "test"))
                .andExpect(model().attributeExists("page"));
    }

    @Test
    @org.junit.jupiter.api.Disabled("Requires full Thymeleaf template rendering - empty products")
    public void testSearchWithNoResults() throws Exception {
        when(productService.findByPartNumber("no-results")).thenReturn(Collections.emptyList());
        when(productService.findByOemNumber("no-results")).thenReturn(Collections.emptyList());
        when(productService.searchProducts("no-results", PageRequest.of(0, 12))).thenReturn(new PageImpl<>(Collections.emptyList()));
        when(categoryService.findRootCategories()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/search").param("query", "no-results"))
                .andExpect(status().isOk())
                .andExpect(view().name("shop-list"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("searchQuery", "no-results"))
                .andExpect(model().attributeExists("page"));
    }
}
