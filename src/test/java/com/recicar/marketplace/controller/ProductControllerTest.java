package com.recicar.marketplace.controller;

import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class ProductControllerTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductController productController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(productController)
                .setViewResolvers((viewName, locale) -> new org.springframework.web.servlet.view.InternalResourceView(viewName + ".html"))
                .build();
    }

    @Test
    void productDetails_shouldReturnProductDetails_whenProductExists() throws Exception {
        // Given
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(BigDecimal.TEN);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

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
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/product-details").param("id", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("error"));
    }
}
