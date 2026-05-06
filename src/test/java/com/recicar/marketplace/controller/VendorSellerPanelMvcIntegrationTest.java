package com.recicar.marketplace.controller;

import com.recicar.marketplace.entity.User;
import com.recicar.marketplace.entity.UserRole;
import com.recicar.marketplace.entity.Vendor;
import com.recicar.marketplace.entity.VendorStatus;
import com.recicar.marketplace.repository.UserRepository;
import com.recicar.marketplace.repository.VendorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Smoke tests for authenticated vendor REST + HTML analytics (seller panel).
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class VendorSellerPanelMvcIntegrationTest {

    private static final String JSON_PATH_ACTIVE = "$.activeProductCount";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VendorRepository vendorRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private User vendorUser;

    private String uniqueEmailSuffix() {
        return UUID.randomUUID().toString().substring(0, 8);
    }

    @BeforeEach
    void setUpSeller() {
        String uid = uniqueEmailSuffix();
        vendorUser = new User();
        vendorUser.setEmail("panel-vendor-" + uid + "@example.com");
        vendorUser.setPasswordHash(passwordEncoder.encode("password"));
        vendorUser.setFirstName("Seller");
        vendorUser.setLastName("Integration");
        vendorUser.setRole(UserRole.VENDOR);
        vendorUser.setActive(true);
        vendorUser.setEmailVerified(true);
        vendorUser = userRepository.save(vendorUser);

        Vendor vendor = new Vendor();
        vendor.setUser(vendorUser);
        vendor.setBusinessName("Integration Store " + uid);
        vendor.setTaxId("TAX-" + uid);
        vendor.setStatus(VendorStatus.APPROVED);
        vendorRepository.save(vendor);
    }

    @Test
    void sellerSummary_dashboardAndAnalyticsApis_ok() throws Exception {
        mockMvc.perform(get("/api/v1/vendor/summary")
                        .with(user(vendorUser.getEmail()).roles("VENDOR")))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_ACTIVE).exists());

        mockMvc.perform(get("/api/v1/vendor/dashboard")
                        .with(user(vendorUser.getEmail()).roles("VENDOR")))
                .andExpect(status().isOk())
                .andExpect(jsonPath(JSON_PATH_ACTIVE).exists());

        mockMvc.perform(get("/api/v1/vendor/analytics/sales")
                        .with(user(vendorUser.getEmail()).roles("VENDOR")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.grossLineRevenue").exists());
    }

    @Test
    void sellerAnalyticsHtmlPage_ok() throws Exception {
        mockMvc.perform(get("/vendor/analytics")
                        .with(user(vendorUser.getEmail()).roles("VENDOR")))
                .andExpect(status().isOk())
                .andExpect(view().name("vendor/analytics"));
    }

    @Test
    void customerRoleCannotAccessSellerApi() throws Exception {
        String uid = uniqueEmailSuffix();
        User customer = new User();
        customer.setEmail("cust-" + uid + "@example.com");
        customer.setPasswordHash(passwordEncoder.encode("password"));
        customer.setFirstName("C");
        customer.setLastName("X");
        customer.setRole(UserRole.CUSTOMER);
        customer.setActive(true);
        customer.setEmailVerified(true);
        customer = userRepository.save(customer);

        mockMvc.perform(get("/api/v1/vendor/summary").with(user(customer.getEmail()).roles("CUSTOMER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void unauthenticatedSellerApi_blocked() throws Exception {
        mockMvc.perform(get("/api/v1/vendor/summary").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
