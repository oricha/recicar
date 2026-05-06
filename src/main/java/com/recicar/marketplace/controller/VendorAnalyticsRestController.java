package com.recicar.marketplace.controller;

import com.recicar.marketplace.dto.VendorSalesAnalyticsDto;
import com.recicar.marketplace.entity.Vendor;
import com.recicar.marketplace.service.VendorAnalyticsService;
import com.recicar.marketplace.service.VendorContextService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.Map;

/**
 * Vendor analytics REST API (seller panel clients).
 */
@RestController
@RequestMapping("/api/v1/vendor/analytics")
@PreAuthorize("hasAnyRole('VENDOR','ADMIN')")
public class VendorAnalyticsRestController {

    private final VendorContextService vendorContextService;
    private final VendorAnalyticsService vendorAnalyticsService;

    public VendorAnalyticsRestController(
            VendorContextService vendorContextService,
            VendorAnalyticsService vendorAnalyticsService) {
        this.vendorContextService = vendorContextService;
        this.vendorAnalyticsService = vendorAnalyticsService;
    }

    /**
     * Sales aggregates for vendor lines within a date range (inclusive from/to calendar days).
     */
    @GetMapping("/sales")
    public ResponseEntity<?> salesForRange(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Authentication required"));
        }
        Vendor vendor = vendorContextService.findVendorForUserDetails(userDetails).orElse(null);
        if (vendor == null) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("message", "No vendor account linked to this user"));
        }
        VendorSalesAnalyticsDto body = vendorAnalyticsService.salesBetween(vendor, from, to);
        return ResponseEntity.ok(body);
    }
}
