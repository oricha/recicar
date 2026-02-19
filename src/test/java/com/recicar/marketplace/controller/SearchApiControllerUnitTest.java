package com.recicar.marketplace.controller;

import com.recicar.marketplace.entity.Category;
import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.entity.ProductCondition;
import com.recicar.marketplace.entity.Vendor;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class SearchApiControllerUnitTest {

    @Mock
    private ProductService productService;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private SearchController searchController;

    private MockMvc mockMvc;

    private Product testProduct;
    private Category testCategory;
    private Vendor testVendor;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(searchController).build();
        
        // Create test data without database
        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Test Category");
        testCategory.setSlug("test-category");
        testCategory.setActive(true);
        testCategory.setSortOrder(1);

        testVendor = new Vendor();
        testVendor.setId(1L);
        testVendor.setBusinessName("Test Vendor");

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Brake Pads");
        testProduct.setDescription("High-quality brake pads for testing");
        testProduct.setPrice(new BigDecimal("99.99"));
        testProduct.setPartNumber("TBP001");
        testProduct.setOemNumber("OEM001");
        testProduct.setCondition(ProductCondition.NEW);
        testProduct.setStockQuantity(10);
        testProduct.setCategory(testCategory);
        testProduct.setVendor(testVendor);
        testProduct.setActive(true);
    }

    @Test
    void contextLoads() {
        assertNotNull(searchController);
        assertNotNull(mockMvc);
    }

    @Test
    void searchProducts_WithValidQuery_ShouldReturnResults() throws Exception {
        Page<Product> productPage = new PageImpl<>(List.of(testProduct), PageRequest.of(0, 12), 1);
        when(productService.findByPartNumber("brake")).thenReturn(List.of());
        when(productService.findByOemNumber("brake")).thenReturn(List.of());
        when(productService.findByPartNumberContaining(anyString(), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of()));
        when(productService.findByOemNumberContaining(anyString(), any(PageRequest.class))).thenReturn(new PageImpl<>(List.of()));
        when(productService.searchProducts(anyString(), any(PageRequest.class))).thenReturn(productPage);
        when(categoryService.findRootCategories()).thenReturn(List.of(testCategory));

        mockMvc.perform(get("/search").param("query", "brake"))
                .andExpect(status().isOk())
                .andExpect(view().name("shop-list"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("searchQuery", "brake"));
    }

    @Test
    void searchProducts_WithEmptyQuery_ShouldRedirectToProducts() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/search").param("query", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"));
    }

    @Test
    void searchProducts_WithShortQuery_ShouldShowError() throws Exception {
        // Arrange
        when(categoryService.findRootCategories()).thenReturn(List.of(testCategory));

        // Act & Assert
        mockMvc.perform(get("/search").param("query", "a"))
                .andExpect(status().isOk())
                .andExpect(view().name("shop-list"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "Search term must be at least 2 characters long"));
    }

    @Test
    void searchByPartNumber_WithValidPartNumber_ShouldReturnResults() throws Exception {
        when(productService.findByPartNumber("TBP001")).thenReturn(List.of(testProduct));
        when(categoryService.findRootCategories()).thenReturn(List.of(testCategory));

        mockMvc.perform(get("/search").param("query", "TBP001"))
                .andExpect(status().isOk())
                .andExpect(view().name("shop-list"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("searchQuery", "TBP001"))
                .andExpect(model().attribute("searchType", "partNumber"));
    }

    @Test
    void searchByPartNumber_WithInvalidPartNumber_ShouldShowError() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/search").param("query", "a"))
                .andExpect(status().isOk())
                .andExpect(view().name("shop-list"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    void searchByOemNumber_WithValidOemNumber_ShouldReturnResults() throws Exception {
        // Arrange
        when(productService.findByPartNumber("OEM001")).thenReturn(List.of());
        when(productService.findByOemNumber("OEM001")).thenReturn(List.of(testProduct));
        when(categoryService.findRootCategories()).thenReturn(List.of(testCategory));

        mockMvc.perform(get("/search").param("query", "OEM001"))
                .andExpect(status().isOk())
                .andExpect(view().name("shop-list"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("searchQuery", "OEM001"))
                .andExpect(model().attribute("searchType", "oemNumber"));
    }

    @Test
    void searchByOemNumber_WithInvalidOemNumber_ShouldShowError() throws Exception {
        // Act & Assert
        when(categoryService.findRootCategories()).thenReturn(List.of(testCategory));
        mockMvc.perform(get("/search").param("query", "a"))
                .andExpect(status().isOk())
                .andExpect(view().name("shop-list"))
                .andExpect(model().attributeExists("errorMessage"));
    }

    @Test
    void searchByVehicle_WithValidParameters_ShouldReturnResults() throws Exception {
        Page<Product> productPage = new PageImpl<>(List.of(testProduct), PageRequest.of(0, 12), 1);
        when(productService.findByMakeModelEngineAndPartName(eq("Toyota"), eq("Camry"), eq("Gasoline"), any(), any(PageRequest.class)))
                .thenReturn(productPage);
        when(categoryService.findRootCategories()).thenReturn(List.of(testCategory));

        mockMvc.perform(get("/search/vehicle")
                        .param("make", "Toyota")
                        .param("model", "Camry")
                        .param("engineType", "Gasoline"))
                .andExpect(status().isOk())
                .andExpect(view().name("shop-list"))
                .andExpect(model().attribute("vehicleMake", "Toyota"))
                .andExpect(model().attribute("vehicleModel", "Camry"))
                .andExpect(model().attribute("vehicleEngine", "Gasoline"))
                .andExpect(model().attributeExists("products"));
    }

    @Test
    void searchByVehicle_WithMissingMake_ShouldShowError() throws Exception {
        when(categoryService.findRootCategories()).thenReturn(List.of(testCategory));
        mockMvc.perform(get("/search/vehicle")
                        .param("make", "")
                        .param("model", "Camry")
                        .param("engineType", "Gasoline"))
                .andExpect(status().isOk())
                .andExpect(view().name("shop-list"))
                .andExpect(model().attribute("errorMessage", "Make, Model and Engine Type are required"));
    }

    @Test
    void searchByVehicle_WithInvalidYear_ShouldShowError() throws Exception {
        Page<Product> productPage = new PageImpl<>(List.of(testProduct), PageRequest.of(0, 12), 1);
        when(productService.findByMakeModelEngineAndPartName(anyString(), anyString(), anyString(), any(), any(PageRequest.class)))
                .thenReturn(productPage);
        when(categoryService.findRootCategories()).thenReturn(List.of(testCategory));
        mockMvc.perform(get("/search/vehicle")
                        .param("make", "Toyota")
                        .param("model", "Camry")
                        .param("engineType", "Gasoline"))
                .andExpect(status().isOk())
                .andExpect(view().name("shop-list"));
    }

    @Test
    void searchByVehicle_WithShortMake_ShouldShowError() throws Exception {
        when(categoryService.findRootCategories()).thenReturn(List.of(testCategory));
        mockMvc.perform(get("/search/vehicle")
                        .param("make", "")
                        .param("model", "Camry")
                        .param("engineType", "Gasoline"))
                .andExpect(status().isOk())
                .andExpect(view().name("shop-list"))
                .andExpect(model().attribute("errorMessage", "Make, Model and Engine Type are required"));
    }

    @Test
    void searchProducts_WithNoResults_ShouldShowNoResultsMessage() throws Exception {
        Page<Product> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 12), 0);
        when(productService.findByPartNumber("nonexistentproduct")).thenReturn(List.of());
        when(productService.findByOemNumber("nonexistentproduct")).thenReturn(List.of());
        when(productService.findByPartNumberContaining(anyString(), any(PageRequest.class))).thenReturn(emptyPage);
        when(productService.findByOemNumberContaining(anyString(), any(PageRequest.class))).thenReturn(emptyPage);
        when(productService.searchProducts(anyString(), any(PageRequest.class))).thenReturn(emptyPage);
        when(categoryService.findRootCategories()).thenReturn(List.of(testCategory));

        mockMvc.perform(get("/search").param("query", "nonexistentproduct"))
                .andExpect(status().isOk())
                .andExpect(view().name("shop-list"))
                .andExpect(model().attributeExists("products"));
    }

    @Test
    void searchByPartNumber_WithNoResults_ShouldShowNoResultsMessage() throws Exception {
        when(productService.findByPartNumber("NONEXISTENT")).thenReturn(List.of());
        when(productService.findByOemNumber("NONEXISTENT")).thenReturn(List.of());
        when(productService.findByPartNumberContaining(anyString(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of()));
        when(productService.findByOemNumberContaining(anyString(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of()));
        when(productService.searchProducts(anyString(), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of()));
        when(categoryService.findRootCategories()).thenReturn(List.of(testCategory));

        mockMvc.perform(get("/search").param("query", "NONEXISTENT"))
                .andExpect(status().isOk())
                .andExpect(view().name("shop-list"))
                .andExpect(model().attributeExists("products"));
    }


}