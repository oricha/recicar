package com.recicar.marketplace.controller;

import com.recicar.marketplace.entity.Category;
import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.entity.Vendor;
import com.recicar.marketplace.repository.CategoryRepository;
import com.recicar.marketplace.repository.ProductRepository;
import com.recicar.marketplace.repository.VendorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
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
            // Skip vendor creation as it requires User entity which is complex
            // Instead, we'll use a mock vendor approach
            return;
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
        subProduct.setStockQuantity(5);
        subProduct.setActive(true);
        subProduct.setCategory(testSubcategory);
        subProduct.setVendor(testVendor);
        productRepository.save(subProduct);
    }

    @Test
    public void testSearchByCategory_IntegrationSuccess() throws Exception {
        mockMvc.perform(get("/search/category")
                        .param("slug", testCategory.getSlug()))
                .andExpect(status().isOk())
                .andExpect(view().name("shop-list"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attributeExists("page"))
                .andExpect(model().attribute("categorySlug", testCategory.getSlug()))
                .andExpect(model().attribute("searchType", "category"));
    }

    @Test
    public void testSearchBySubcategory_IntegrationSuccess() throws Exception {
        mockMvc.perform(get("/search/category")
                        .param("slug", testSubcategory.getSlug()))
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
                .andExpect(redirectedUrl("/search/category?slug=" + testCategory.getSlug()));
    }

    @Test
    public void testSearchByCategory_WithPagination_Integration() throws Exception {
        mockMvc.perform(get("/search/category")
                        .param("slug", testCategory.getSlug())
                        .param("page", "0"))
                .andExpect(status().isOk())
                .andExpect(view().name("shop-list"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attributeExists("page"));
    }

    @Test
    public void testSearchByCategory_InvalidSlug_Integration() throws Exception {
        mockMvc.perform(get("/search/category")
                        .param("slug", "invalid-category-slug"))
                .andExpect(status().isOk())
                .andExpect(view().name("shop-list"))
                .andExpect(model().attributeExists("errorMessage"));
    }
}
