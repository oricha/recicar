package com.recicar.marketplace.controller;

import com.recicar.marketplace.entity.User;
import com.recicar.marketplace.entity.Vendor;
import com.recicar.marketplace.service.CategoryService;
import com.recicar.marketplace.service.ProductService;
import com.recicar.marketplace.service.VendorContextService;
import com.recicar.marketplace.service.VendorOrderMetricsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = VendorDashboardController.class)
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
public class VendorDashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VendorContextService vendorContextService;

    @MockBean
    private VendorOrderMetricsService vendorOrderMetricsService;

    @MockBean
    private ProductService productService;

    @MockBean
    private CategoryService categoryService;

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

        when(vendorContextService.requireCurrentVendor(any())).thenReturn(mockVendor);
        when(productService.countActiveByVendor(any(Vendor.class))).thenReturn(10L);
        when(productService.countProductsByVendor(any(Vendor.class))).thenReturn(12L);
        when(productService.findLowStockProductsByVendor(any(Vendor.class))).thenReturn(Collections.emptyList());
        when(vendorOrderMetricsService.sumMonthRevenueExcludingCanceled(any(Vendor.class))).thenReturn(BigDecimal.ZERO);
        when(vendorOrderMetricsService.countPendingOrders(any(Vendor.class))).thenReturn(0L);
        when(vendorOrderMetricsService.getRecentOrdersForVendor(any(Vendor.class), anyInt()))
                .thenReturn(Collections.emptyList());
    }

    @Test
    @WithMockUser(username = "vendor@example.com", roles = {"VENDOR"})
    void testGetDashboard() throws Exception {
        mockMvc.perform(get("/vendor/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("vendor/dashboard"));
    }
}
