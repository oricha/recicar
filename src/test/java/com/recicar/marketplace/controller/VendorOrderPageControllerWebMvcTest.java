package com.recicar.marketplace.controller;

import com.recicar.marketplace.config.MvcSliceTestConfig;
import com.recicar.marketplace.entity.Vendor;
import com.recicar.marketplace.service.CategoryService;
import com.recicar.marketplace.service.VendorContextService;
import com.recicar.marketplace.service.VendorOrderMetricsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = VendorOrderPageController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(MvcSliceTestConfig.class)
class VendorOrderPageControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VendorContextService vendorContextService;

    @MockBean
    private VendorOrderMetricsService vendorOrderMetricsService;

    @MockBean
    private CategoryService categoryService;

    private Vendor vendor;

    @BeforeEach
    void setUp() {
        vendor = new Vendor();
        vendor.setId(12L);
        vendor.setBusinessName("Orders Co");
        when(vendorContextService.requireCurrentVendor(any())).thenReturn(vendor);
        when(vendorOrderMetricsService.getOrderListItemPage(any(), anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(List.of()));
    }

    @Test
    @WithMockUser(username = "seller@co.com", roles = {"VENDOR"})
    void orders_returnsView() throws Exception {
        mockMvc.perform(get("/vendor/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("vendor/orders"));
    }
}
