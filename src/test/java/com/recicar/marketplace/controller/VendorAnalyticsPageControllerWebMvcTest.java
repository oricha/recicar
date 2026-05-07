package com.recicar.marketplace.controller;

import com.recicar.marketplace.config.MvcSliceTestConfig;
import com.recicar.marketplace.dto.VendorSalesAnalyticsDto;
import com.recicar.marketplace.entity.Vendor;
import com.recicar.marketplace.service.CategoryService;
import com.recicar.marketplace.service.VendorAnalyticsService;
import com.recicar.marketplace.service.VendorContextService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = VendorAnalyticsPageController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(MvcSliceTestConfig.class)
class VendorAnalyticsPageControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VendorContextService vendorContextService;

    @MockBean
    private VendorAnalyticsService vendorAnalyticsService;

    @MockBean
    private CategoryService categoryService;

    private Vendor vendor;

    @BeforeEach
    void setUp() {
        vendor = new Vendor();
        vendor.setId(33L);
        vendor.setBusinessName("Charts Inc");
        when(vendorContextService.requireCurrentVendor(any())).thenReturn(vendor);
        when(vendorAnalyticsService.salesBetween(eq(vendor), any(), any()))
                .thenReturn(new VendorSalesAnalyticsDto(
                        LocalDate.now().minusDays(7),
                        LocalDate.now(),
                        new BigDecimal("12.34"),
                        2,
                        4
                ));
    }

    @Test
    @WithMockUser(username = "analytics@vendor.test", roles = {"VENDOR"})
    void analytics_returnsView() throws Exception {
        mockMvc.perform(get("/vendor/analytics"))
                .andExpect(status().isOk())
                .andExpect(view().name("vendor/analytics"));
    }
}
