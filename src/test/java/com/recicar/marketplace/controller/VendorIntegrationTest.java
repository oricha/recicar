package com.recicar.marketplace.controller;

import com.recicar.marketplace.dto.ProductRequest;
import com.recicar.marketplace.dto.VendorRegistrationRequest;
import com.recicar.marketplace.entity.Category;
import com.recicar.marketplace.entity.ProductCondition;
import com.recicar.marketplace.entity.User;
import com.recicar.marketplace.entity.Vendor;
import com.recicar.marketplace.entity.User.UserRole;
import com.recicar.marketplace.entity.Vendor.VendorStatus;
import com.recicar.marketplace.repository.CategoryRepository;
import com.recicar.marketplace.repository.UserRepository;
import com.recicar.marketplace.repository.VendorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class VendorIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User vendorUser;
    private Vendor vendor;
    private Category category;

    @BeforeEach
    void setUp() {
        // Create a user for the vendor
        vendorUser = new User();
        vendorUser.setEmail("vendor@example.com");
        vendorUser.setPasswordHash(passwordEncoder.encode("password"));
        vendorUser.setFirstName("Vendor");
        vendorUser.setLastName("User");
        vendorUser.setRole(UserRole.VENDOR);
        vendorUser.setActive(true);
        userRepository.save(vendorUser);

        // Create a vendor
        vendor = new Vendor();
        vendor.setUser(vendorUser);
        vendor.setBusinessName("Test Vendor Inc.");
        vendor.setTaxId("TAX123");
        vendor.setStatus(VendorStatus.APPROVED);
        vendorRepository.save(vendor);

        // Create a category
        category = new Category();
        category.setName("Engine Parts");
        category.setSlug("engine-parts");
        categoryRepository.save(category);
    }

    @Test
    void testVendorRegistration() throws Exception {
        VendorRegistrationRequest request = new VendorRegistrationRequest();
        request.setEmail("newvendor@example.com");
        request.setPassword("newpassword");
        request.setFirstName("New");
        request.setLastName("Vendor");
        request.setBusinessName("New Vendor Co.");
        request.setTaxId("NEWTAX");

        mockMvc.perform(post("/api/vendors/register")
                        .contentType("application/json")
                        .content(asJsonString(request)))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "vendor@example.com", roles = {"VENDOR"})
    void testAddProductAsVendor() throws Exception {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setName("Vendor Product");
        productRequest.setDescription("Description");
        productRequest.setPrice(new BigDecimal("25.00"));
        productRequest.setCondition(ProductCondition.NEW);
        productRequest.setStockQuantity(50);
        productRequest.setVendorId(vendor.getId());
        productRequest.setCategoryId(category.getId());

        mockMvc.perform(post("/api/vendor/products")
                        .contentType("application/json")
                        .content(asJsonString(productRequest)))
                .andExpect(status().isOk());
    }

    // Helper method to convert object to JSON string
    private static String asJsonString(final Object obj) {
        try {
            return new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}