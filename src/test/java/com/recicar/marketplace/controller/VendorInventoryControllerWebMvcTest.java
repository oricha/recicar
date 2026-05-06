package com.recicar.marketplace.controller;

import com.recicar.marketplace.config.MvcSliceTestConfig;
import com.recicar.marketplace.entity.Vendor;
import com.recicar.marketplace.service.CategoryService;
import com.recicar.marketplace.service.ProductService;
import com.recicar.marketplace.service.VendorContextService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = VendorInventoryController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(MvcSliceTestConfig.class)
class VendorInventoryControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VendorContextService vendorContextService;

    @MockBean
    private ProductService productService;

    @MockBean
    private CategoryService categoryService;

    private Vendor vendor;

    @BeforeEach
    void setUp() {
        vendor = new Vendor();
        vendor.setId(88L);
        vendor.setBusinessName("Panel Store");
        when(vendorContextService.requireCurrentVendor(any())).thenReturn(vendor);
        when(productService.findAllProductsByVendorForManagement(eq(vendor), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of()));
    }

    @Test
    @WithMockUser(username = "vendor@test.com", roles = {"VENDOR"})
    void inventory_returnsView() throws Exception {
        mockMvc.perform(get("/vendor/inventory"))
                .andExpect(status().isOk())
                .andExpect(view().name("vendor/inventory"));
    }
}
