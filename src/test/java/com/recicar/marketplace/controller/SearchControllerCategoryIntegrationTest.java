package com.recicar.marketplace.controller;

import com.recicar.marketplace.entity.Category;
import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.entity.ProductCondition;
import com.recicar.marketplace.entity.User;
import com.recicar.marketplace.entity.Vendor;
import com.recicar.marketplace.entity.VendorStatus;
import com.recicar.marketplace.entity.UserRole;
import com.recicar.marketplace.repository.CategoryRepository;
import com.recicar.marketplace.repository.ProductRepository;
import com.recicar.marketplace.repository.UserRepository;
import com.recicar.marketplace.repository.VendorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class SearchControllerCategoryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    private Category testCategory;
    private Category testSubcategory;
    private Product testProduct;
    private Vendor testVendor;

    @BeforeEach
    public void setup() {
        // Clean up previous test data
        productRepository.deleteAll();
        categoryRepository.deleteAll();

        // Use existing vendor or create one with minimal setup
        testVendor = vendorRepository.findAll().stream().findFirst().orElse(null);
        if (testVendor == null) {
            User vendorUser = new User();
            vendorUser.setEmail("vendor-test-" + System.currentTimeMillis() + "@example.com");
            vendorUser.setPasswordHash(passwordEncoder.encode("password123"));
            vendorUser.setFirstName("Test");
            vendorUser.setLastName("Vendor");
            vendorUser.setRole(UserRole.VENDOR);
            vendorUser.setActive(true);
            vendorUser = userRepository.save(vendorUser);

            testVendor = new Vendor();
            testVendor.setUser(vendorUser);
            testVendor.setBusinessName("Test Vendor");
            testVendor.setTaxId("TAX" + System.currentTimeMillis());
            testVendor.setStatus(VendorStatus.APPROVED);
            testVendor = vendorRepository.save(testVendor);
        }

        // Create test parent category
        testCategory = new Category();
        testCategory.setName("Test Category");
        testCategory.setSlug("test-category");
        testCategory.setActive(true);
        testCategory = categoryRepository.save(testCategory);

        // Create test subcategory
        testSubcategory = new Category();
        testSubcategory.setName("Test Subcategory");
        testSubcategory.setSlug("test-subcategory");
        testSubcategory.setParent(testCategory);
        testSubcategory.setActive(true);
        testSubcategory = categoryRepository.save(testSubcategory);

        // Create test product in parent category
        testProduct = new Product();
        testProduct.setName("Test Product");
        testProduct.setPartNumber("TEST-001");
        testProduct.setPrice(BigDecimal.valueOf(99.99));
        testProduct.setCondition(ProductCondition.USED);
        testProduct.setStockQuantity(10);
        testProduct.setActive(true);
        testProduct.setCategory(testCategory);
        testProduct.setVendor(testVendor);
        testProduct = productRepository.save(testProduct);

        // Create test product in subcategory
        Product subProduct = new Product();
        subProduct.setName("Test Subproduct");
        subProduct.setPartNumber("TEST-002");
        subProduct.setPrice(BigDecimal.valueOf(49.99));
        subProduct.setCondition(ProductCondition.USED);
        subProduct.setStockQuantity(5);
        subProduct.setActive(true);
        subProduct.setCategory(testSubcategory);
        subProduct.setVendor(testVendor);
        productRepository.save(subProduct);
    }

    @Test
    public void testSearchByCategory_IntegrationSuccess() throws Exception {
        mockMvc.perform(get("/search")
                        .param("category", testCategory.getSlug()))
                .andExpect(status().isOk())
                .andExpect(view().name("shop-list"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attributeExists("page"))
                .andExpect(model().attribute("categorySlug", testCategory.getSlug()))
                .andExpect(model().attribute("searchType", "category"));
    }

    @Test
    public void testSearchBySubcategory_IntegrationSuccess() throws Exception {
        mockMvc.perform(get("/search")
                        .param("category", testSubcategory.getSlug()))
                .andExpect(status().isOk())
                .andExpect(view().name("shop-list"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attributeExists("category"))
                .andExpect(model().attribute("searchType", "category"));
    }

    @Test
    public void testCategoriesPageRedirect_Integration() throws Exception {
        mockMvc.perform(get("/categories/" + testCategory.getSlug()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/search?category=" + testCategory.getSlug()));
    }

    @Test
    public void testSearchByCategory_WithPagination_Integration() throws Exception {
        mockMvc.perform(get("/search")
                        .param("category", testCategory.getSlug())
                        .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(view().name("shop-list"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attributeExists("page"));
    }

    @Test
    public void testSearchByCategory_InvalidSlug_Integration() throws Exception {
        mockMvc.perform(get("/search")
                        .param("category", "invalid-category-slug"))
                .andExpect(status().isOk())
                .andExpect(view().name("shop-list"))
                .andExpect(model().attributeExists("errorMessage"));
    }
}
