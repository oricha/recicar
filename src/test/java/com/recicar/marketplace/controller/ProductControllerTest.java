package com.recicar.marketplace.controller;

import com.recicar.marketplace.entity.Category;
import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.service.CategoryService;
import com.recicar.marketplace.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

    @Mock
    private ProductService productService;
    
    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private ProductController productController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Use a proper ViewResolver that supports "redirect:" and "forward:" prefixes
        org.springframework.web.servlet.view.InternalResourceViewResolver viewResolver =
                new org.springframework.web.servlet.view.InternalResourceViewResolver();
        viewResolver.setPrefix("");
        viewResolver.setSuffix(".html");

        mockMvc = MockMvcBuilders.standaloneSetup(productController)
                .setViewResolvers(viewResolver)
                .build();
    }

    @Test
    void productList_shouldReturnShopListWithProductsAndCategories() throws Exception {
        // Given
        List<Product> products = new ArrayList<>();
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(BigDecimal.TEN);
        products.add(product);
        
        Page<Product> productPage = new PageImpl<>(products);
        
        List<Category> categories = new ArrayList<>();
        Category category = new Category("Test Category", "test-category");
        category.setId(1L);
        categories.add(category);
        
        when(productService.findActiveProducts(anyInt(), anyInt())).thenReturn(productPage);
        when(categoryService.findAllActive()).thenReturn(categories);

        // When & Then
        mockMvc.perform(get("/shop-list"))
                .andExpect(status().isOk())
                .andExpect(view().name("shop-list"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attributeExists("page"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attribute("categories", categories));
    }

    @Test
    void productDetails_shouldReturnProductDetails_whenProductExists() throws Exception {
        // Given
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(BigDecimal.TEN);

        when(productService.findById(1L)).thenReturn(Optional.of(product));

        // When & Then
        mockMvc.perform(get("/product-details").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("product-details"))
                .andExpect(model().attributeExists("product"))
                .andExpect(model().attribute("product", product));
    }

    @Test
    void productDetails_shouldReturnErrorPage_whenProductDoesNotExist() throws Exception {
        // Given
        when(productService.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/product-details").param("id", "1"))
                .andDo(print())
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/"));
    }
}
