package com.recicar.marketplace.controller;

import com.recicar.marketplace.dto.VendorSalesAnalyticsDto;
import com.recicar.marketplace.entity.Vendor;
import com.recicar.marketplace.service.VendorAnalyticsService;
import com.recicar.marketplace.service.VendorContextService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
@RequestMapping("/vendor/analytics")
public class VendorAnalyticsPageController {

    private final VendorContextService vendorContextService;
    private final VendorAnalyticsService vendorAnalyticsService;

    public VendorAnalyticsPageController(
            VendorContextService vendorContextService,
            VendorAnalyticsService vendorAnalyticsService) {
        this.vendorContextService = vendorContextService;
        this.vendorAnalyticsService = vendorAnalyticsService;
    }

    @GetMapping
    public String analytics(
            Model model,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        Vendor vendor = vendorContextService.requireCurrentVendor(userDetails);
        VendorSalesAnalyticsDto sales = vendorAnalyticsService.salesBetween(vendor, from, to);
        model.addAttribute("vendor", vendor);
        model.addAttribute("sales", sales);
        model.addAttribute("chartRevenue", sales.grossLineRevenue());
        model.addAttribute("filterFrom", sales.periodFromInclusive());
        model.addAttribute("filterTo", sales.periodToInclusive());
        model.addAttribute("pageTitle", "Vendor analytics — sales");
        return "vendor/analytics";
    }
}
