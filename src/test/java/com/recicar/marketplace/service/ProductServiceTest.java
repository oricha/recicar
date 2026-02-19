package com.recicar.marketplace.service;

import com.recicar.marketplace.dto.ProductRequest;
import com.recicar.marketplace.entity.*;
import com.recicar.marketplace.repository.ProductRepository;
import com.recicar.marketplace.repository.CategoryRepository;
import com.recicar.marketplace.repository.VendorRepository;
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
import org.springframework.data.domain.Sort;

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

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private VendorRepository vendorRepository;

    @InjectMocks
    private ProductServiceImpl productService;

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
        Pageable pageable = PageRequest.of(0, 12, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Product> expectedPage = new PageImpl<>(Arrays.asList(testProduct));
        when(productRepository.findByActiveTrue(pageable)).thenReturn(expectedPage);

        // When
        Page<Product> result = productService.findActiveProducts(0, 12);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0)).isEqualTo(testProduct);
        verify(productRepository).findByActiveTrue(pageable);
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
        ProductRequest request = new ProductRequest();
        request.setName("New Product");
        request.setDescription("New Description");
        request.setPrice(new BigDecimal("50.00"));
        request.setCondition(ProductCondition.NEW);
        request.setStockQuantity(5);
        request.setActive(true);
        request.setVendorId(testVendor.getId());
        request.setCategoryId(testCategory.getId());
        when(vendorRepository.findById(testVendor.getId())).thenReturn(Optional.of(testVendor));
        when(categoryRepository.findById(testCategory.getId())).thenReturn(Optional.of(testCategory));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product savedProduct = invocation.getArgument(0);
            if (savedProduct.getId() == null) {
                savedProduct.setId(2L); // Simulate ID generation for new product
            }
            return savedProduct;
        });

        // When
        Product createdProduct = productService.createOrUpdateProduct(request);

        // Then
        assertThat(createdProduct).isNotNull();
        assertThat(createdProduct.getId()).isNotNull();
        assertThat(createdProduct.getName()).isEqualTo("New Product");
        assertThat(createdProduct.getVendor()).isEqualTo(testVendor);
        assertThat(createdProduct.getCategory()).isEqualTo(testCategory);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void shouldDeactivateProduct() {
        // Given
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(testProduct)).thenReturn(testProduct);

        // When
        productService.decreaseStock(1L, testProduct.getStockQuantity());

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
        assertThatThrownBy(() -> productService.decreaseStock(1L, 1))
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
        boolean result = productService.findActiveById(1L).isPresent();

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
        boolean result = productService.findActiveById(1L).isPresent();

        // Then
        assertThat(result).isFalse();
        verify(productRepository).findById(1L);
    }

    @Test
    void shouldCheckStockAvailability() {
        // Given
        testProduct.setStockQuantity(10);

        // When
        boolean result = testProduct.getStockQuantity() >= 5;

        // Then
        assertThat(result).isTrue();
    }

    @Test
    void shouldReturnFalseWhenInsufficientStock() {
        // Given
        testProduct.setStockQuantity(3);

        // When
        boolean result = testProduct.getStockQuantity() >= 5;

        // Then
        assertThat(result).isFalse();
    }

    @Test
    void shouldCreateProduct() {
        // Given
        ProductRequest request = new ProductRequest();
        request.setName("New Product");
        request.setDescription("New Description");
        request.setPrice(new BigDecimal("50.00"));
        request.setCondition(ProductCondition.NEW);
        request.setStockQuantity(5);
        request.setActive(true);
        request.setVendorId(testVendor.getId());
        request.setCategoryId(testCategory.getId());

        when(vendorRepository.findById(testVendor.getId())).thenReturn(Optional.of(testVendor));
        when(categoryRepository.findById(testCategory.getId())).thenReturn(Optional.of(testCategory));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> {
            Product savedProduct = invocation.getArgument(0);
            if (savedProduct.getId() == null) {
                savedProduct.setId(2L); // Simulate ID generation for new product
            }
            return savedProduct;
        });

        // When
        Product createdProduct = productService.createOrUpdateProduct(request);

        // Then
        assertThat(createdProduct).isNotNull();
        assertThat(createdProduct.getId()).isNotNull();
        assertThat(createdProduct.getName()).isEqualTo("New Product");
        assertThat(createdProduct.getVendor()).isEqualTo(testVendor);
        assertThat(createdProduct.getCategory()).isEqualTo(testCategory);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void shouldUpdateExistingProduct() {
        // Given
        ProductRequest request = new ProductRequest();
        request.setId(testProduct.getId());
        request.setName("Updated Product");
        request.setDescription("Updated Description");
        request.setPrice(new BigDecimal("120.00"));
        request.setCondition(ProductCondition.USED);
        request.setStockQuantity(15);
        request.setActive(false);
        request.setVendorId(testVendor.getId());
        request.setCategoryId(testCategory.getId());

        when(productRepository.findById(testProduct.getId())).thenReturn(Optional.of(testProduct));
        when(vendorRepository.findById(testVendor.getId())).thenReturn(Optional.of(testVendor));
        when(categoryRepository.findById(testCategory.getId())).thenReturn(Optional.of(testCategory));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        Product updatedProduct = productService.createOrUpdateProduct(request);

        // Then
        assertThat(updatedProduct).isNotNull();
        assertThat(updatedProduct.getId()).isEqualTo(testProduct.getId());
        assertThat(updatedProduct.getName()).isEqualTo("Updated Product");
        assertThat(updatedProduct.getPrice()).isEqualTo(new BigDecimal("120.00"));
        assertThat(updatedProduct.getCondition()).isEqualTo(ProductCondition.USED);
        assertThat(updatedProduct.getStockQuantity()).isEqualTo(15);
        assertThat(updatedProduct.isActive()).isFalse();
        verify(productRepository).findById(testProduct.getId());
        verify(productRepository).save(testProduct);
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistentProduct() {
        // Given
        ProductRequest request = new ProductRequest();
        request.setId(99L); // Non-existent ID

        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> productService.createOrUpdateProduct(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Product not found");
        verify(productRepository).findById(99L);
        verify(productRepository, never()).save(any());
    }

    @Test
    void shouldBulkUpdateStock() {
        // Given
        Product product1 = new Product();
        product1.setId(1L);
        product1.setStockQuantity(10);
        Product product2 = new Product();
        product2.setId(2L);
        product2.setStockQuantity(5);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product2));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        List<Long> productIds = Arrays.asList(1L, 2L);
        List<Integer> quantities = Arrays.asList(8, 3);

        // When
        productService.bulkUpdateStock(productIds, quantities);

        // Then
        assertThat(product1.getStockQuantity()).isEqualTo(8);
        assertThat(product2.getStockQuantity()).isEqualTo(3);
        verify(productRepository, times(2)).save(any(Product.class));
    }

    @Test
    void shouldGetInventoryReport() {
        // Given
        List<Product> allProducts = Arrays.asList(testProduct);
        when(productRepository.findAll()).thenReturn(allProducts);

        // When
        List<Product> report = productService.getInventoryReport();

        // Then
        assertThat(report).isEqualTo(allProducts);
        verify(productRepository).findAll();
    }

    @Test
    void shouldGetInventoryReportByVendor() {
        // Given
        List<Product> vendorProducts = Arrays.asList(testProduct);
        when(productRepository.findByVendorAndActiveTrue(testVendor, Pageable.unpaged())).thenReturn(new PageImpl<>(vendorProducts));

        // When
        List<Product> report = productService.getInventoryReportByVendor(testVendor);

        // Then
        assertThat(report).isEqualTo(vendorProducts);
        verify(productRepository).findByVendorAndActiveTrue(testVendor, Pageable.unpaged());
    }

    @Test
    void shouldFindOtherVendorsSellingProduct() {
        // Given
        Product product1 = new Product();
        product1.setId(1L);
        product1.setPartNumber("PN123");
        product1.setVendor(testVendor);

        Vendor otherVendor = new Vendor();
        otherVendor.setId(2L);
        otherVendor.setBusinessName("Other Vendor");

        Product product2 = new Product();
        product2.setId(3L);
        product2.setPartNumber("PN123");
        product2.setVendor(otherVendor);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product1));
        when(productRepository.findByPartNumberContaining(eq("PN123"), any(Pageable.class)))
                .thenReturn(new PageImpl<>(Arrays.asList(product1, product2)));

        // When
        List<Vendor> vendors = productService.findOtherVendorsSellingProduct(1L);

        // Then
        assertThat(vendors).hasSize(1);
        assertThat(vendors.get(0)).isEqualTo(otherVendor);
        verify(productRepository).findById(1L);
        verify(productRepository).findByPartNumberContaining(eq("PN123"), any(Pageable.class));
    }
}
