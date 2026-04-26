package com.recicar.marketplace.controller;

import com.recicar.marketplace.entity.Vendor;
import com.recicar.marketplace.service.ProductService;
import com.recicar.marketplace.service.VendorContextService;
import com.recicar.marketplace.service.VendorOrderMetricsService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/vendor/dashboard")
public class VendorDashboardController {

    private final VendorContextService vendorContextService;
    private final ProductService productService;
    private final VendorOrderMetricsService vendorOrderMetricsService;

    public VendorDashboardController(
            VendorContextService vendorContextService,
            ProductService productService,
            VendorOrderMetricsService vendorOrderMetricsService) {
        this.vendorContextService = vendorContextService;
        this.productService = productService;
        this.vendorOrderMetricsService = vendorOrderMetricsService;
    }

    @GetMapping
    public String getDashboard(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {
        Vendor vendor = vendorContextService.requireCurrentVendor(userDetails);

        model.addAttribute("vendor", vendor);
        model.addAttribute("activeProductCount", productService.countActiveByVendor(vendor));
        model.addAttribute("totalProductCount", productService.countProductsByVendor(vendor));
        model.addAttribute("lowStockProducts", productService.findLowStockProductsByVendor(vendor));
        model.addAttribute("monthSales", vendorOrderMetricsService.sumMonthRevenueExcludingCanceled(vendor));
        model.addAttribute("pendingOrderCount", vendorOrderMetricsService.countPendingOrders(vendor));
        model.addAttribute("recentOrders", vendorOrderMetricsService.getRecentOrdersForVendor(vendor, 5));
        model.addAttribute("pageTitle", "Vendor Dashboard");

        return "vendor/dashboard";
    }
}
