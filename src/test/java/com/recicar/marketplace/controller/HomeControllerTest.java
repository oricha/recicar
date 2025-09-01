package com.recicar.marketplace.controller;

import com.recicar.marketplace.entity.Category;
import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.service.CategoryService;
import com.recicar.marketplace.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = HomeController.class)
class HomeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private CategoryService categoryService;

    @Test
    void home_addsBodyAndEngineParts() throws Exception {
        // Featured products
        Page<Product> featured = new PageImpl<>(List.of());
        when(productService.findActiveProducts(anyInt(), anyInt())).thenReturn(featured);
        // Categories for header
        when(categoryService.findRootCategories()).thenReturn(List.of());
        // Category lookup
        Category body = new Category(); body.setId(1L); body.setName("Body Parts");
        Category engine = new Category(); engine.setId(2L); engine.setName("Engine Parts");
        when(categoryService.searchByName("Body")).thenReturn(List.of(body));
        when(categoryService.searchByName("Engine")).thenReturn(List.of(engine));
        when(productService.findByCategory(body, PageRequest.of(0, 9))).thenReturn(new PageImpl<>(List.of()));
        when(productService.findByCategory(engine, PageRequest.of(0, 9))).thenReturn(new PageImpl<>(List.of()));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("bodyParts"))
                .andExpect(model().attributeExists("engineParts"));
    }
}

