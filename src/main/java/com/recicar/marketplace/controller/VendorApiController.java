package com.recicar.marketplace.controller;

import com.recicar.marketplace.dto.VendorOrderListItemDto;
import com.recicar.marketplace.dto.VendorPanelSummaryDto;
import com.recicar.marketplace.entity.Vendor;
import com.recicar.marketplace.service.ProductService;
import com.recicar.marketplace.service.VendorContextService;
import com.recicar.marketplace.service.VendorOrderMetricsService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * JSON API for the seller panel (mobile clients or future SPA).
 */
@RestController
@RequestMapping("/api/v1/vendor")
public class VendorApiController {

    private final VendorContextService vendorContextService;
    private final ProductService productService;
    private final VendorOrderMetricsService vendorOrderMetricsService;

    public VendorApiController(
            VendorContextService vendorContextService,
            ProductService productService,
            VendorOrderMetricsService vendorOrderMetricsService) {
        this.vendorContextService = vendorContextService;
        this.productService = productService;
        this.vendorOrderMetricsService = vendorOrderMetricsService;
    }

    @GetMapping("/summary")
    public ResponseEntity<?> getSummary(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Authentication required"));
        }
        return vendorContextService.findVendorForUserDetails(userDetails)
                .<ResponseEntity<?>>map(this::okSummary)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(Map.of("message", "No vendor account linked to this user")));
    }

    private ResponseEntity<?> okSummary(Vendor vendor) {
        VendorPanelSummaryDto body = new VendorPanelSummaryDto(
                productService.countActiveByVendor(vendor),
                productService.countProductsByVendor(vendor),
                vendorOrderMetricsService.sumMonthRevenueExcludingCanceled(vendor),
                vendorOrderMetricsService.countPendingOrders(vendor)
        );
        return ResponseEntity.ok(body);
    }

    @GetMapping("/orders")
    public ResponseEntity<?> listOrders(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Authentication required"));
        }
        Vendor vendor = vendorContextService.findVendorForUserDetails(userDetails).orElse(null);
        if (vendor == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", "No vendor account linked to this user"));
        }
        if (size > 100) {
            size = 100;
        }
        Page<VendorOrderListItemDto> orders = vendorOrderMetricsService.getOrderListItemPage(vendor, page, size);
        return ResponseEntity.ok(orders.getContent());
    }
}
