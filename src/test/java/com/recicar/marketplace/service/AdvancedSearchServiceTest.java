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
class AdvancedSearchServiceTest {

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
    void searchWithFilters_WithAllFilters_ShouldReturnFilteredResults() {
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
    void searchWithFilters_WithNullFilters_ShouldReturnAllResults() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> expectedPage = new PageImpl<>(List.of(testProduct), pageable, 1);
        
        when(productRepository.findWithFilters(any(), any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(expectedPage);

        // Act
        Page<Product> result = searchService.searchWithFilters(null, null, null, null, null, null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(productRepository).findWithFilters(null, null, null, null, null, null, pageable);
    }

    @Test
    void searchWithFilters_WithPriceRangeOnly_ShouldReturnResultsInRange() {
        // Arrange
        BigDecimal minPrice = new BigDecimal("50.00");
        BigDecimal maxPrice = new BigDecimal("150.00");
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> expectedPage = new PageImpl<>(List.of(testProduct), pageable, 1);
        
        when(productRepository.findWithFilters(any(), any(), any(), any(BigDecimal.class), any(BigDecimal.class), any(), any(Pageable.class)))
                .thenReturn(expectedPage);

        // Act
        Page<Product> result = searchService.searchWithFilters(null, null, null, minPrice, maxPrice, null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(productRepository).findWithFilters(null, null, null, minPrice, maxPrice, null, pageable);
    }

    @Test
    void searchWithFilters_WithInvalidPriceRange_ShouldThrowException() {
        // Arrange
        BigDecimal minPrice = new BigDecimal("200.00");
        BigDecimal maxPrice = new BigDecimal("50.00"); // Invalid: min > max
        Pageable pageable = PageRequest.of(0, 10);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            searchService.searchWithFilters(null, null, null, minPrice, maxPrice, null, pageable);
        });
        
        verify(productRepository, never()).findWithFilters(any(), any(), any(), any(), any(), any(), any(Pageable.class));
    }

    @Test
    void searchWithFilters_WithCategoryFilter_ShouldReturnCategoryResults() {
        // Arrange
        Category category = testCategory;
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> expectedPage = new PageImpl<>(List.of(testProduct), pageable, 1);
        
        when(productRepository.findWithFilters(any(), any(Category.class), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(expectedPage);

        // Act
        Page<Product> result = searchService.searchWithFilters(null, category, null, null, null, null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(productRepository).findWithFilters(null, category, null, null, null, null, pageable);
    }

    @Test
    void searchWithFilters_WithConditionFilter_ShouldReturnConditionResults() {
        // Arrange
        ProductCondition condition = ProductCondition.NEW;
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> expectedPage = new PageImpl<>(List.of(testProduct), pageable, 1);
        
        when(productRepository.findWithFilters(any(), any(), any(ProductCondition.class), any(), any(), any(), any(Pageable.class)))
                .thenReturn(expectedPage);

        // Act
        Page<Product> result = searchService.searchWithFilters(null, null, condition, null, null, null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(productRepository).findWithFilters(null, null, condition, null, null, null, pageable);
    }

    @Test
    void searchWithFilters_WithVendorFilter_ShouldReturnVendorResults() {
        // Arrange
        Vendor vendor = testVendor;
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> expectedPage = new PageImpl<>(List.of(testProduct), pageable, 1);
        
        when(productRepository.findWithFilters(any(), any(), any(), any(), any(), any(Vendor.class), any(Pageable.class)))
                .thenReturn(expectedPage);

        // Act
        Page<Product> result = searchService.searchWithFilters(null, null, null, null, null, vendor, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(productRepository).findWithFilters(null, null, null, null, null, vendor, pageable);
    }

    @Test
    void searchWithFilters_WithSearchTermAndCategory_ShouldCombineFilters() {
        // Arrange
        String searchTerm = "brake";
        Category category = testCategory;
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> expectedPage = new PageImpl<>(List.of(testProduct), pageable, 1);
        
        when(productRepository.findWithFilters(anyString(), any(Category.class), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(expectedPage);

        // Act
        Page<Product> result = searchService.searchWithFilters(searchTerm, category, null, null, null, null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(productRepository).findWithFilters(searchTerm, category, null, null, null, null, pageable);
    }

    @Test
    void searchWithFilters_WithComplexFilterCombination_ShouldHandleAllFilters() {
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
    void searchWithFilters_WithEmptyResults_ShouldReturnEmptyPage() {
        // Arrange
        String searchTerm = "nonexistent";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> emptyPage = new PageImpl<>(List.of(), pageable, 0);
        
        when(productRepository.findWithFilters(anyString(), any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(emptyPage);

        // Act
        Page<Product> result = searchService.searchWithFilters(searchTerm, null, null, null, null, null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());
        assertTrue(result.getContent().isEmpty());
    }

    @Test
    void searchWithFilters_WithPagination_ShouldRespectPageSize() {
        // Arrange
        String searchTerm = "brake";
        Pageable pageable = PageRequest.of(1, 5); // Second page, 5 items per page
        Page<Product> expectedPage = new PageImpl<>(List.of(testProduct), pageable, 10); // Total 10 items
        
        when(productRepository.findWithFilters(anyString(), any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(expectedPage);

        // Act
        Page<Product> result = searchService.searchWithFilters(searchTerm, null, null, null, null, null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(10, result.getTotalElements());
        assertEquals(1, result.getNumber()); // Page number
        assertEquals(5, result.getSize()); // Page size
    }

    @Test
    void searchWithFilters_WithSorting_ShouldApplySortOrder() {
        // Arrange
        String searchTerm = "brake";
        Pageable pageable = PageRequest.of(0, 10, org.springframework.data.domain.Sort.by("name").ascending());
        Page<Product> expectedPage = new PageImpl<>(List.of(testProduct), pageable, 1);
        
        when(productRepository.findWithFilters(anyString(), any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(expectedPage);

        // Act
        Page<Product> result = searchService.searchWithFilters(searchTerm, null, null, null, null, null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(productRepository).findWithFilters(searchTerm, null, null, null, null, null, pageable);
    }

    @Test
    void searchWithFilters_WithNullSearchTerm_ShouldHandleGracefully() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> expectedPage = new PageImpl<>(List.of(testProduct), pageable, 1);
        
        when(productRepository.findWithFilters(any(), any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(expectedPage);

        // Act
        Page<Product> result = searchService.searchWithFilters(null, null, null, null, null, null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(productRepository).findWithFilters(null, null, null, null, null, null, pageable);
    }

    @Test
    void searchWithFilters_WithEmptySearchTerm_ShouldHandleGracefully() {
        // Arrange
        String searchTerm = "";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> expectedPage = new PageImpl<>(List.of(testProduct), pageable, 1);
        
        when(productRepository.findWithFilters(anyString(), any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(expectedPage);

        // Act
        Page<Product> result = searchService.searchWithFilters(searchTerm, null, null, null, null, null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(productRepository).findWithFilters(searchTerm, null, null, null, null, null, pageable);
    }

    @Test
    void searchWithFilters_WithWhitespaceSearchTerm_ShouldTrimAndSearch() {
        // Arrange
        String searchTerm = "  brake  ";
        String expectedTrimmedTerm = "brake";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> expectedPage = new PageImpl<>(List.of(testProduct), pageable, 1);
        
        when(productRepository.findWithFilters(anyString(), any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(expectedPage);

        // Act
        Page<Product> result = searchService.searchWithFilters(searchTerm, null, null, null, null, null, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(productRepository).findWithFilters(expectedTrimmedTerm, null, null, null, null, null, pageable);
    }
}

