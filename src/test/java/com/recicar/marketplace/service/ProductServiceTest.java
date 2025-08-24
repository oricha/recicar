package com.recicar.marketplace.service;

import com.recicar.marketplace.entity.*;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private Category testCategory;
    private Vendor testVendor;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("vendor@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("Vendor");

        testVendor = new Vendor();
        testVendor.setId(1L);
        testVendor.setUser(testUser);
        testVendor.setBusinessName("Test Vendor");

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Engine Parts");
        testCategory.setSlug("engine-parts");

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("Test Product");
        testProduct.setDescription("Test Description");
        testProduct.setPrice(new BigDecimal("99.99"));
        testProduct.setCondition(ProductCondition.NEW);
        testProduct.setStockQuantity(10);
        testProduct.setActive(true);
        testProduct.setVendor(testVendor);
        testProduct.setCategory(testCategory);
    }

    @Test
    void shouldFindActiveProducts() {
        // Given
        Pageable pageable = PageRequest.of(0, 12);
        Page<Product> expectedPage = new PageImpl<>(Arrays.asList(testProduct));
        when(productRepository.findByActiveTrue(pageable)).thenReturn(expectedPage);

        // When
        Page<Product> result = productService.findActiveProducts(pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(testProduct);
        verify(productRepository).findByActiveTrue(pageable);
    }

    @Test
    void shouldFindActiveProductsWithDefaultPagination() {
        // Given
        Page<Product> expectedPage = new PageImpl<>(Arrays.asList(testProduct));
        when(productRepository.findByActiveTrue(any(Pageable.class))).thenReturn(expectedPage);

        // When
        Page<Product> result = productService.findActiveProducts(0);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(productRepository).findByActiveTrue(any(Pageable.class));
    }

    @Test
    void shouldFindProductById() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // When
        Optional<Product> result = productService.findById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testProduct);
        verify(productRepository).findById(1L);
    }

    @Test
    void shouldFindActiveProductById() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // When
        Optional<Product> result = productService.findActiveById(1L);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(testProduct);
        verify(productRepository).findById(1L);
    }

    @Test
    void shouldNotFindInactiveProductById() {
        // Given
        testProduct.setActive(false);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // When
        Optional<Product> result = productService.findActiveById(1L);

        // Then
        assertThat(result).isEmpty();
        verify(productRepository).findById(1L);
    }

    @Test
    void shouldSearchProducts() {
        // Given
        String searchTerm = "test";
        Pageable pageable = PageRequest.of(0, 12);
        Page<Product> expectedPage = new PageImpl<>(Arrays.asList(testProduct));
        when(productRepository.searchByNameOrPartNumber(searchTerm, pageable)).thenReturn(expectedPage);

        // When
        Page<Product> result = productService.searchProducts(searchTerm, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(productRepository).searchByNameOrPartNumber(searchTerm, pageable);
    }

    @Test
    void shouldReturnAllActiveProductsWhenSearchTermIsEmpty() {
        // Given
        Pageable pageable = PageRequest.of(0, 12);
        Page<Product> expectedPage = new PageImpl<>(Arrays.asList(testProduct));
        when(productRepository.findByActiveTrue(pageable)).thenReturn(expectedPage);

        // When
        Page<Product> result = productService.searchProducts("", pageable);

        // Then
        assertThat(result).isNotNull();
        verify(productRepository).findByActiveTrue(pageable);
        verify(productRepository, never()).searchByNameOrPartNumber(any(), any());
    }

    @Test
    void shouldFindProductsByCategory() {
        // Given
        Pageable pageable = PageRequest.of(0, 12);
        Page<Product> expectedPage = new PageImpl<>(Arrays.asList(testProduct));
        when(productRepository.findByCategoryAndActiveTrue(testCategory, pageable)).thenReturn(expectedPage);

        // When
        Page<Product> result = productService.findByCategory(testCategory, pageable);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        verify(productRepository).findByCategoryAndActiveTrue(testCategory, pageable);
    }

    @Test
    void shouldSaveProduct() {
        // Given
        when(productRepository.save(testProduct)).thenReturn(testProduct);

        // When
        Product result = productService.save(testProduct);

        // Then
        assertThat(result).isEqualTo(testProduct);
        verify(productRepository).save(testProduct);
    }

    @Test
    void shouldDeactivateProduct() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(testProduct)).thenReturn(testProduct);

        // When
        productService.deactivateProduct(1L);

        // Then
        assertThat(testProduct.isActive()).isFalse();
        verify(productRepository).findById(1L);
        verify(productRepository).save(testProduct);
    }

    @Test
    void shouldThrowExceptionWhenDeactivatingNonExistentProduct() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productService.deactivateProduct(1L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Product not found");
        
        verify(productRepository).findById(1L);
        verify(productRepository, never()).save(any());
    }

    @Test
    void shouldUpdateStock() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(testProduct)).thenReturn(testProduct);

        // When
        productService.updateStock(1L, 20);

        // Then
        assertThat(testProduct.getStockQuantity()).isEqualTo(20);
        verify(productRepository).findById(1L);
        verify(productRepository).save(testProduct);
    }

    @Test
    void shouldDecreaseStock() {
        // Given
        testProduct.setStockQuantity(10);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(testProduct)).thenReturn(testProduct);

        // When
        productService.decreaseStock(1L, 3);

        // Then
        assertThat(testProduct.getStockQuantity()).isEqualTo(7);
        verify(productRepository).findById(1L);
        verify(productRepository).save(testProduct);
    }

    @Test
    void shouldThrowExceptionWhenDecreasingStockBelowZero() {
        // Given
        testProduct.setStockQuantity(5);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // When & Then
        assertThatThrownBy(() -> productService.decreaseStock(1L, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Insufficient stock");
        
        verify(productRepository).findById(1L);
        verify(productRepository, never()).save(any());
    }

    @Test
    void shouldIncreaseStock() {
        // Given
        testProduct.setStockQuantity(10);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(testProduct)).thenReturn(testProduct);

        // When
        productService.increaseStock(1L, 5);

        // Then
        assertThat(testProduct.getStockQuantity()).isEqualTo(15);
        verify(productRepository).findById(1L);
        verify(productRepository).save(testProduct);
    }

    @Test
    void shouldCheckIfProductIsAvailable() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // When
        boolean result = productService.isAvailable(1L);

        // Then
        assertThat(result).isTrue();
        verify(productRepository).findById(1L);
    }

    @Test
    void shouldReturnFalseForUnavailableProduct() {
        // Given
        testProduct.setActive(false);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // When
        boolean result = productService.isAvailable(1L);

        // Then
        assertThat(result).isFalse();
        verify(productRepository).findById(1L);
    }

    @Test
    void shouldCheckStockAvailability() {
        // Given
        testProduct.setStockQuantity(10);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // When
        boolean result = productService.hasStock(1L, 5);

        // Then
        assertThat(result).isTrue();
        verify(productRepository).findById(1L);
    }

    @Test
    void shouldReturnFalseWhenInsufficientStock() {
        // Given
        testProduct.setStockQuantity(3);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // When
        boolean result = productService.hasStock(1L, 5);

        // Then
        assertThat(result).isFalse();
        verify(productRepository).findById(1L);
    }
}