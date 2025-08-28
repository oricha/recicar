package com.recicar.marketplace.controller;

import com.recicar.marketplace.entity.User;
import com.recicar.marketplace.entity.Vendor;
import com.recicar.marketplace.service.ProductService;
import com.recicar.marketplace.service.VendorService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@Disabled
@WebMvcTest(VendorDashboardController.class)
public class VendorDashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VendorService vendorService;

    @MockBean
    private ProductService productService;

    private Vendor mockVendor;
    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("vendor@example.com");

        mockVendor = new Vendor();
        mockVendor.setId(1L);
        mockVendor.setBusinessName("Test Vendor");
        mockVendor.setUser(mockUser);

        when(vendorService.findById(anyLong())).thenReturn(Optional.of(mockVendor));
        when(productService.countActiveByVendor(any(Vendor.class))).thenReturn(10L);
        when(productService.findLowStockProductsByVendor(any(Vendor.class))).thenReturn(java.util.Collections.emptyList());
    }

    @Test
    @WithMockUser(username = "vendor@example.com", roles = {"VENDOR"})
    void testGetDashboard() throws Exception {
        mockMvc.perform(get("/vendor/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("vendor/dashboard"));
    }
}
