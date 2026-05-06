package com.recicar.marketplace.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recicar.marketplace.dto.ProductRequest;
import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.entity.Vendor;
import com.recicar.marketplace.service.ProductService;
import com.recicar.marketplace.service.VendorContextService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class VendorProductControllerStandaloneTest {

    @Mock
    private ProductService productService;

    @Mock
    private VendorContextService vendorContextService;

    private MockMvc mockMvc;

    private final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
        mockMvc = MockMvcBuilders.standaloneSetup(new VendorProductController(productService, vendorContextService))
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
    }

    @Test
    void updateProduct_forbiddenWhenProductBelongsToAnotherVendor() throws Exception {
        UserDetails principal = org.springframework.security.core.userdetails.User
                .withUsername("me@vendor.test")
                .password("x")
                .roles("VENDOR")
                .build();

        Vendor myVendor = new Vendor();
        myVendor.setId(10L);

        Vendor otherVendor = new Vendor();
        otherVendor.setId(99L);

        Product existing = new Product();
        existing.setId(555L);
        existing.setVendor(otherVendor);

        when(vendorContextService.findVendorForUserDetails(any())).thenReturn(Optional.of(myVendor));
        when(productService.findById(555L)).thenReturn(Optional.of(existing));

        ProductRequest body = new ProductRequest();
        body.setName("x");
        body.setPrice(BigDecimal.TEN);
        body.setStockQuantity(1);
        body.setVendorId(10L);

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
        try {
            mockMvc.perform(put("/api/vendor/products/555")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(body)))
                    .andExpect(status().isForbidden());
        } finally {
            SecurityContextHolder.clearContext();
        }
    }
}
