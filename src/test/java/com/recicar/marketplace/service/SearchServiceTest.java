package com.recicar.marketplace.service;

import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.entity.Category;
import com.recicar.marketplace.entity.ProductCondition;
import com.recicar.marketplace.entity.Vendor;
import com.recicar.marketplace.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private SearchService searchService;

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
        testProduct.setPartNumber("TP001");
        testProduct.setOemNumber("OEM001");
        testProduct.setCondition(ProductCondition.NEW);
        testProduct.setStockQuantity(10);
        testProduct.setCategory(testCategory);
        testProduct.setVendor(testVendor);
        testProduct.setActive(true);
    }

    @Test
    void searchProducts_WithValidSearchTerm_ShouldReturnResults() {
        // Arrange
        String searchTerm = "brake pads";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> expectedPage = new PageImpl<>(List.of(testProduct), pageable, 1);
        
        when(productRepository.searchByNameOrPartNumber(anyString(), any(Pageable.class)))
                .thenReturn(expectedPage);

        // Act
        Page<Product> result = searchService.searchProducts(searchTerm, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(testProduct, result.getContent().get(0));
        verify(productRepository).searchByNameOrPartNumber(searchTerm, pageable);
    }

    @Test
    void searchProducts_WithEmptySearchTerm_ShouldThrowException() {
        // Arrange
        String searchTerm = "";
        Pageable pageable = PageRequest.of(0, 10);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            searchService.searchProducts(searchTerm, pageable);
        });
        
        verify(productRepository, never()).searchByNameOrPartNumber(anyString(), any(Pageable.class));
    }

    @Test
    void searchProducts_WithNullSearchTerm_ShouldThrowException() {
        // Arrange
        String searchTerm = null;
        Pageable pageable = PageRequest.of(0, 10);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            searchService.searchProducts(searchTerm, pageable);
        });
        
        verify(productRepository, never()).searchByNameOrPartNumber(anyString(), any(Pageable.class));
    }

    @Test
    void searchProducts_WithShortSearchTerm_ShouldThrowException() {
        // Arrange
        String searchTerm = "a";
        Pageable pageable = PageRequest.of(0, 10);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            searchService.searchProducts(searchTerm, pageable);
        });
        
        verify(productRepository, never()).searchByNameOrPartNumber(anyString(), any(Pageable.class));
    }

    @Test
    void searchProducts_WithMaliciousSearchTerm_ShouldThrowException() {
        // Arrange
        String searchTerm = "<script>alert('xss')</script>";
        Pageable pageable = PageRequest.of(0, 10);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            searchService.searchProducts(searchTerm, pageable);
        });
        
        verify(productRepository, never()).searchByNameOrPartNumber(anyString(), any(Pageable.class));
    }

    @Test
    void searchProducts_WithLongSearchTerm_ShouldTruncateAndSearch() {
        // Arrange
        String longSearchTerm = "a".repeat(150);
        String expectedTruncatedTerm = "a".repeat(100);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> expectedPage = new PageImpl<>(List.of(testProduct), pageable, 1);
        
        when(productRepository.searchByNameOrPartNumber(anyString(), any(Pageable.class)))
                .thenReturn(expectedPage);

        // Act
        Page<Product> result = searchService.searchProducts(longSearchTerm, pageable);

        // Assert
        assertNotNull(result);
        verify(productRepository).searchByNameOrPartNumber(expectedTruncatedTerm, pageable);
    }

    @Test
    void searchByPartNumber_WithValidPartNumber_ShouldReturnResults() {
        // Arrange
        String partNumber = "TP001";
        when(productRepository.findByPartNumberIgnoreCase(partNumber))
                .thenReturn(List.of(testProduct));

        // Act
        List<Product> result = searchService.searchByPartNumber(partNumber);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testProduct, result.get(0));
        verify(productRepository).findByPartNumberIgnoreCase(partNumber);
    }

    @Test
    void searchByPartNumber_WithEmptyPartNumber_ShouldThrowException() {
        // Arrange
        String partNumber = "";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            searchService.searchByPartNumber(partNumber);
        });
        
        verify(productRepository, never()).findByPartNumberIgnoreCase(anyString());
    }

    @Test
    void searchByOemNumber_WithValidOemNumber_ShouldReturnResults() {
        // Arrange
        String oemNumber = "OEM001";
        when(productRepository.findByOemNumberIgnoreCase(oemNumber))
                .thenReturn(List.of(testProduct));

        // Act
        List<Product> result = searchService.searchByOemNumber(oemNumber);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testProduct, result.get(0));
        verify(productRepository).findByOemNumberIgnoreCase(oemNumber);
    }

    @Test
    void searchByVehicleCompatibility_WithValidParameters_ShouldReturnResults() {
        // Arrange
        String make = "Toyota";
        String model = "Camry";
        Integer year = 2020;
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> expectedPage = new PageImpl<>(List.of(testProduct), pageable, 1);
        
        when(productRepository.findByVehicleCompatibility(anyString(), anyString(), anyInt(), any(Pageable.class)))
                .thenReturn(expectedPage);

        // Act
        Page<Product> result = searchService.searchByVehicleCompatibility(make, model, year, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(productRepository).findByVehicleCompatibility(make, model, year, pageable);
    }

    @Test
    void searchByVehicleCompatibility_WithNullMake_ShouldThrowException() {
        // Arrange
        String make = null;
        String model = "Camry";
        Integer year = 2020;
        Pageable pageable = PageRequest.of(0, 10);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            searchService.searchByVehicleCompatibility(make, model, year, pageable);
        });
        
        verify(productRepository, never()).findByVehicleCompatibility(anyString(), anyString(), anyInt(), any(Pageable.class));
    }

    @Test
    void searchByVehicleCompatibility_WithEmptyModel_ShouldThrowException() {
        // Arrange
        String make = "Toyota";
        String model = "";
        Integer year = 2020;
        Pageable pageable = PageRequest.of(0, 10);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            searchService.searchByVehicleCompatibility(make, model, year, pageable);
        });
        
        verify(productRepository, never()).findByVehicleCompatibility(anyString(), anyString(), anyInt(), any(Pageable.class));
    }

    @Test
    void searchByVehicleCompatibility_WithInvalidYear_ShouldThrowException() {
        // Arrange
        String make = "Toyota";
        String model = "Camry";
        Integer year = 1800; // Invalid year
        Pageable pageable = PageRequest.of(0, 10);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            searchService.searchByVehicleCompatibility(make, model, year, pageable);
        });
        
        verify(productRepository, never()).findByVehicleCompatibility(anyString(), anyString(), anyInt(), any(Pageable.class));
    }

    @Test
    void searchWithFilters_WithValidParameters_ShouldReturnResults() {
        // Arrange
        String searchTerm = "brake";
        Category category = testCategory;
        ProductCondition condition = ProductCondition.NEW;
        BigDecimal minPrice = new BigDecimal("50.00");
        BigDecimal maxPrice = new BigDecimal("200.00");
        Vendor vendor = testVendor;
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> expectedPage = new PageImpl<>(List.of(testProduct), pageable, 1);
        
        when(productRepository.findWithFilters(anyString(), any(Category.class), any(ProductCondition.class), 
                any(BigDecimal.class), any(BigDecimal.class), any(Vendor.class), any(Pageable.class)))
                .thenReturn(expectedPage);

        // Act
        Page<Product> result = searchService.searchWithFilters(searchTerm, category, condition, 
                minPrice, maxPrice, vendor, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(productRepository).findWithFilters(searchTerm, category, condition, 
                minPrice, maxPrice, vendor, pageable);
    }

    @Test
    void searchWithFilters_WithInvalidPriceRange_ShouldThrowException() {
        // Arrange
        String searchTerm = "brake";
        Category category = testCategory;
        ProductCondition condition = ProductCondition.NEW;
        BigDecimal minPrice = new BigDecimal("200.00");
        BigDecimal maxPrice = new BigDecimal("50.00"); // Invalid: min > max
        Vendor vendor = testVendor;
        Pageable pageable = PageRequest.of(0, 10);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            searchService.searchWithFilters(searchTerm, category, condition, 
                    minPrice, maxPrice, vendor, pageable);
        });
        
        verify(productRepository, never()).findWithFilters(anyString(), any(Category.class), 
                any(ProductCondition.class), any(BigDecimal.class), any(BigDecimal.class), 
                any(Vendor.class), any(Pageable.class));
    }

    @Test
    void getSearchSuggestions_WithValidPartialTerm_ShouldReturnEmptyList() {
        // Arrange
        String partialTerm = "bra";

        // Act
        List<String> result = searchService.getSearchSuggestions(partialTerm);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        // Note: This currently returns empty list as per the basic implementation
        // In a real application, this would return actual suggestions
    }

    @Test
    void getSearchSuggestions_WithShortPartialTerm_ShouldReturnEmptyList() {
        // Arrange
        String partialTerm = "a";

        // Act
        List<String> result = searchService.getSearchSuggestions(partialTerm);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void getSearchStatistics_WithValidSearchTerm_ShouldReturnStatistics() {
        // Arrange
        String searchTerm = "brake";
        Page<Product> searchPage = new PageImpl<>(List.of(testProduct), PageRequest.of(0, 1), 1);
        
        when(productRepository.searchByNameOrPartNumber(anyString(), any(Pageable.class)))
                .thenReturn(searchPage);

        // Act
        SearchService.SearchStatistics result = searchService.getSearchStatistics(searchTerm);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalResults());
        assertEquals(1, result.getInStockResults());
        assertEquals(0, result.getOutOfStockResults());
    }

    @Test
    void getSearchStatistics_WithEmptySearchTerm_ShouldReturnZeroStatistics() {
        // Arrange
        String searchTerm = "";

        // Act
        SearchService.SearchStatistics result = searchService.getSearchStatistics(searchTerm);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getTotalResults());
        assertEquals(0, result.getInStockResults());
        assertEquals(0, result.getOutOfStockResults());
    }

    @Test
    void searchProducts_WithWhitespaceSearchTerm_ShouldTrimAndSearch() {
        // Arrange
        String searchTerm = "  brake pads  ";
        String expectedTrimmedTerm = "brake pads";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> expectedPage = new PageImpl<>(List.of(testProduct), pageable, 1);
        
        when(productRepository.searchByNameOrPartNumber(anyString(), any(Pageable.class)))
                .thenReturn(expectedPage);

        // Act
        Page<Product> result = searchService.searchProducts(searchTerm, pageable);

        // Assert
        assertNotNull(result);
        verify(productRepository).searchByNameOrPartNumber(expectedTrimmedTerm, pageable);
    }
}

