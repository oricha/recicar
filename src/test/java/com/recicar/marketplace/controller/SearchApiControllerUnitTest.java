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
    private SearchApiController searchApiController;

    private MockMvc mockMvc;

    private Product testProduct;
    private Category testCategory;
    private Vendor testVendor;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(searchApiController).build();
        
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
        assertNotNull(searchApiController);
        assertNotNull(mockMvc);
    }

    @Test
    void searchProducts_WithValidQuery_ShouldReturnResults() throws Exception {
        // Arrange
        Page<Product> productPage = new PageImpl<>(List.of(testProduct), PageRequest.of(0, 12), 1);
        when(productService.searchProducts(anyString(), any(PageRequest.class)))
                .thenReturn(productPage);
        when(categoryService.findAllActive()).thenReturn(List.of(testCategory));

        // Act & Assert
        mockMvc.perform(get("/products/search")
                        .param("q", "brake"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/search-results"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("searchQuery", "brake"))
                .andExpect(model().attributeExists("totalElements"));
    }

    @Test
    void searchProducts_WithEmptyQuery_ShouldRedirectToProducts() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/products/search")
                        .param("q", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/products"));
    }

    @Test
    void searchProducts_WithShortQuery_ShouldShowError() throws Exception {
        // Arrange
        when(categoryService.findAllActive()).thenReturn(List.of(testCategory));

        // Act & Assert
        mockMvc.perform(get("/products/search")
                        .param("q", "a"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/search-results"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "Search term must be at least 2 characters long"));
    }

    @Test
    void searchByPartNumber_WithValidPartNumber_ShouldReturnResults() throws Exception {
        // Arrange
        when(productService.findByPartNumber(anyString()))
                .thenReturn(List.of(testProduct));

        // Act & Assert
        mockMvc.perform(get("/products/part/TBP001"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/part-search"))
                .andExpect(model().attribute("partNumber", "TBP001"))
                .andExpect(model().attribute("searchType", "Part Number"))
                .andExpect(model().attributeExists("products"));
    }

    @Test
    void searchByPartNumber_WithInvalidPartNumber_ShouldShowError() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/products/part/a"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/part-search"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "Part number must be at least 2 characters long"));
    }

    @Test
    void searchByOemNumber_WithValidOemNumber_ShouldReturnResults() throws Exception {
        // Arrange
        when(productService.findByOemNumber(anyString()))
                .thenReturn(List.of(testProduct));

        // Act & Assert
        mockMvc.perform(get("/products/oem/OEM001"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/part-search"))
                .andExpect(model().attribute("oemNumber", "OEM001"))
                .andExpect(model().attribute("searchType", "OEM Number"))
                .andExpect(model().attributeExists("products"));
    }

    @Test
    void searchByOemNumber_WithInvalidOemNumber_ShouldShowError() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/products/oem/a"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/part-search"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "OEM number must be at least 2 characters long"));
    }

    @Test
    void searchByVehicle_WithValidParameters_ShouldReturnResults() throws Exception {
        // Arrange
        Page<Product> productPage = new PageImpl<>(List.of(testProduct), PageRequest.of(0, 12), 1);
        when(productService.findByVehicleCompatibility(anyString(), anyString(), anyString(), any(Integer.class), any(PageRequest.class)))
                .thenReturn(productPage);

        // Act & Assert
        mockMvc.perform(get("/products/vehicle")
                        .param("make", "Toyota")
                        .param("model", "Camry")
                        .param("engine", "Gasoline") // Added engine parameter
                        .param("year", "2020"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/vehicle-compatibility"))
                .andExpect(model().attribute("vehicleMake", "Toyota"))
                .andExpect(model().attribute("vehicleModel", "Camry"))
                .andExpect(model().attribute("vehicleEngine", "Gasoline")) // Assert engine
                .andExpect(model().attribute("vehicleYear", 2020))
                .andExpect(model().attributeExists("products"));
    }

    @Test
    void searchByVehicle_WithMissingMake_ShouldShowError() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/products/vehicle")
                        .param("model", "Camry")
                        .param("year", "2020"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/vehicle-compatibility"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "Vehicle make, model, engine, and year are required"));
    }

    @Test
    void searchByVehicle_WithInvalidYear_ShouldShowError() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/products/vehicle")
                        .param("make", "Toyota")
                        .param("model", "Camry")
                        .param("engine", "Gasoline") // Added valid engine
                        .param("year", "1800"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/vehicle-compatibility"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "Vehicle year must be between 1900 and 2030"));
    }

    @Test
    void searchByVehicle_WithShortMake_ShouldShowError() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/products/vehicle")
                        .param("make", "T")
                        .param("model", "Camry")
                        .param("engine", "Gasoline") // Added valid engine
                        .param("year", "2020"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/vehicle-compatibility"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "Vehicle make, model, and engine must be at least 2 characters long"));
    }

    @Test
    void searchProducts_WithNoResults_ShouldShowNoResultsMessage() throws Exception {
        // Arrange
        Page<Product> emptyPage = new PageImpl<>(List.of(), PageRequest.of(0, 12), 0);
        when(productService.searchProducts(anyString(), any(PageRequest.class)))
                .thenReturn(emptyPage);
        when(categoryService.findAllActive()).thenReturn(List.of(testCategory));

        // Act & Assert
        mockMvc.perform(get("/products/search")
                        .param("q", "nonexistentproduct"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/search-results"))
                .andExpect(model().attribute("totalElements", 0L));
    }

    @Test
    void searchByPartNumber_WithNoResults_ShouldShowNoResultsMessage() throws Exception {
        // Arrange
        when(productService.findByPartNumber(anyString()))
                .thenReturn(List.of());

        // Act & Assert
        mockMvc.perform(get("/products/part/NONEXISTENT"))
                .andExpect(status().isOk())
                .andExpect(view().name("products/part-search"))
                .andExpect(model().attributeExists("products"));
    }


}