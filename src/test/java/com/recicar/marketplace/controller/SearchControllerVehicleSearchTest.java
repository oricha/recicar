package com.recicar.marketplace.controller;

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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = SearchController.class)
class SearchControllerVehicleSearchTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;
    
    @MockBean
    private CategoryService categoryService;

    @Test
    void searchByMakeModelEngineAndPartName_returnsShopList() throws Exception {
        Page<Product> page = new PageImpl<>(List.of(), PageRequest.of(0, 12), 0);
        when(productService.findByMakeModelEngineAndPartName(anyString(), anyString(), anyString(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/search/vehicle")
                        .param("make", "toyota")
                        .param("model", "camry")
                        .param("engineType", "2.0 petrol")
                        .param("partName", "filter"))
                .andExpect(status().isOk())
                .andExpect(view().name("shop-list"));
    }
}

