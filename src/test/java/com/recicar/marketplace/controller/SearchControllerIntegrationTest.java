package com.recicar.marketplace.controller;

import com.recicar.marketplace.entity.Category;
import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.entity.ProductCondition;
import com.recicar.marketplace.entity.Vendor;
import com.recicar.marketplace.service.CategoryService;
import com.recicar.marketplace.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SearchController.class)
class SearchControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private CategoryService categoryService;

    private Product testProduct;
    private Category testCategory;
    private Vendor testVendor;

    @BeforeEach
    void setUp() {
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Test Category");

        testVendor = new Vendor();
        testVendor.setId(1L);
        testVendor.setBusinessName("Test Vendor");

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(new BigDecimal("99.99"));
        testProduct.setPartNumber("P12345");
        testProduct.setOemNumber("OEM12345");
        testProduct.setCondition(ProductCondition.NEW);
        testProduct.setStockQuantity(10);
        testProduct.setCategory(testCategory);
        testProduct.setVendor(testVendor);
        testProduct.setActive(true);

        when(categoryService.findRootCategories()).thenReturn(Collections.singletonList(testCategory));
    }

    @Test
    void searchByExactPartNumber_ShouldReturnResults() throws Exception {
        when(productService.findByPartNumber("P12345")).thenReturn(Collections.singletonList(testProduct));

        mockMvc.perform(get("/search").param("query", "P12345"))
                .andExpect(status().isOk())
                .andExpect(view().name("shop-list"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("searchQuery", "P12345"))
                .andExpect(model().attribute("searchType", "partNumber"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    void searchByExactOemNumber_ShouldReturnResults() throws Exception {
        when(productService.findByPartNumber("OEM12345")).thenReturn(Collections.emptyList());
        when(productService.findByOemNumber("OEM12345")).thenReturn(Collections.singletonList(testProduct));

        mockMvc.perform(get("/search").param("query", "OEM12345"))
                .andExpect(status().isOk())
                .andExpect(view().name("shop-list"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("searchQuery", "OEM12345"))
                .andExpect(model().attribute("searchType", "oemNumber"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    void searchByPartialPartNumber_ShouldReturnResults() throws Exception {
        Page<Product> productPage = new PageImpl<>(Collections.singletonList(testProduct));
        when(productService.findByPartNumber("P123")).thenReturn(Collections.emptyList());
        when(productService.findByOemNumber("P123")).thenReturn(Collections.emptyList());
        when(productService.findByPartNumberContaining("P123", PageRequest.of(0, 12))).thenReturn(productPage);

        mockMvc.perform(get("/search").param("query", "P123"))
                .andExpect(status().isOk())
                .andExpect(view().name("shop-list"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("searchQuery", "P123"))
                .andExpect(model().attribute("searchType", "partNumberContaining"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    void searchByGeneralTerm_ShouldReturnResults() throws Exception {
        Page<Product> productPage = new PageImpl<>(Collections.singletonList(testProduct));
        when(productService.findByPartNumber("brake")).thenReturn(Collections.emptyList());
        when(productService.findByOemNumber("brake")).thenReturn(Collections.emptyList());
        when(productService.searchProducts("brake", PageRequest.of(0, 12))).thenReturn(productPage);

        mockMvc.perform(get("/search").param("query", "brake"))
                .andExpect(status().isOk())
                .andExpect(view().name("shop-list"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("searchQuery", "brake"))
                .andExpect(model().attribute("searchType", "general"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    void searchByPartName_ShouldReturnResults() throws Exception {
        Page<Product> productPage = new PageImpl<>(Collections.singletonList(testProduct));
        when(productService.findByProductName("Brake Pad", PageRequest.of(0, 12))).thenReturn(productPage);

        mockMvc.perform(get("/search/part-name").param("partName", "Brake Pad"))
                .andExpect(status().isOk())
                .andExpect(view().name("shop-list"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("partName", "Brake Pad"))
                .andExpect(model().attribute("searchType", "partName"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    void searchByVehicle_ShouldReturnResults() throws Exception {
        Page<Product> productPage = new PageImpl<>(Collections.singletonList(testProduct));
        when(productService.findByMakeModelEngineAndPartName("Honda", "Civic", "1.8L", null, PageRequest.of(0, 12)))
                .thenReturn(productPage);

        mockMvc.perform(get("/search/vehicle")
                .param("make", "Honda")
                .param("model", "Civic")
                .param("engineType", "1.8L"))
                .andExpect(status().isOk())
                .andExpect(view().name("shop-list"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("vehicleMake", "Honda"))
                .andExpect(model().attribute("vehicleModel", "Civic"))
                .andExpect(model().attribute("vehicleEngine", "1.8L"))
                .andExpect(model().attribute("searchType", "vehicle"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    void searchByVehicleWithPartName_ShouldReturnResults() throws Exception {
        Page<Product> productPage = new PageImpl<>(Collections.singletonList(testProduct));
        when(productService.findByMakeModelEngineAndPartName("Toyota", "Camry", "2.0L", "filter", PageRequest.of(0, 12)))
                .thenReturn(productPage);

        mockMvc.perform(get("/search/vehicle")
                .param("make", "Toyota")
                .param("model", "Camry")
                .param("engineType", "2.0L")
                .param("partName", "filter"))
                .andExpect(status().isOk())
                .andExpect(view().name("shop-list"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("vehicleMake", "Toyota"))
                .andExpect(model().attribute("vehicleModel", "Camry"))
                .andExpect(model().attribute("vehicleEngine", "2.0L"))
                .andExpect(model().attribute("partName", "filter"))
                .andExpect(model().attribute("searchType", "vehicle"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    void searchWithShortQuery_ShouldShowError() throws Exception {
        mockMvc.perform(get("/search").param("query", "a"))
                .andExpect(status().isOk())
                .andExpect(view().name("shop-list"))
                .andExpect(model().attribute("errorMessage", "Search term must be at least 2 characters long"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    void searchWithEmptyQuery_ShouldRedirectToProducts() throws Exception {
        mockMvc.perform(get("/search").param("query", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"));
    }

    @Test
    void searchWithNullQuery_ShouldRedirectToProducts() throws Exception {
        mockMvc.perform(get("/search"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"));
    }

    @Test
    void searchByPartNameWithShortName_ShouldShowError() throws Exception {
        mockMvc.perform(get("/search/part-name").param("partName", "a"))
                .andExpect(status().isOk())
                .andExpect(view().name("shop-list"))
                .andExpect(model().attribute("errorMessage", "Part name must be at least 2 characters long"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    void searchByVehicleWithMissingParameters_ShouldShowError() throws Exception {
        mockMvc.perform(get("/search/vehicle")
                .param("make", "")
                .param("model", "Civic")
                .param("engineType", "1.8L"))
                .andExpect(status().isOk())
                .andExpect(view().name("shop-list"))
                .andExpect(model().attribute("errorMessage", "Make, Model and Engine Type are required"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    void searchWithBackwardCompatibility_ShouldWorkWithQParameter() throws Exception {
        Page<Product> productPage = new PageImpl<>(Collections.singletonList(testProduct));
        when(productService.findByPartNumber("brake")).thenReturn(Collections.emptyList());
        when(productService.findByOemNumber("brake")).thenReturn(Collections.emptyList());
        when(productService.searchProducts("brake", PageRequest.of(0, 12))).thenReturn(productPage);

        mockMvc.perform(get("/search").param("q", "brake"))
                .andExpect(status().isOk())
                .andExpect(view().name("shop-list"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("searchQuery", "brake"))
                .andExpect(model().attribute("searchType", "general"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    void searchWithPagination_ShouldWork() throws Exception {
        Page<Product> productPage = new PageImpl<>(Collections.singletonList(testProduct));
        when(productService.findByPartNumber("brake")).thenReturn(Collections.emptyList());
        when(productService.findByOemNumber("brake")).thenReturn(Collections.emptyList());
        when(productService.searchProducts("brake", PageRequest.of(1, 12))).thenReturn(productPage);

        mockMvc.perform(get("/search").param("query", "brake").param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("shop-list"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attributeExists("page"))
                .andExpect(model().attribute("searchQuery", "brake"))
                .andExpect(model().attribute("searchType", "general"))
                .andExpect(model().attributeExists("categories"));
    }
}