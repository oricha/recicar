package com.recicar.marketplace.controller;

import com.recicar.marketplace.entity.Vendor;
import com.recicar.marketplace.service.ProductService;
import com.recicar.marketplace.service.VendorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/vendor/dashboard")
public class VendorDashboardController {

    private final VendorService vendorService;
    private final ProductService productService;

    public VendorDashboardController(VendorService vendorService, ProductService productService) {
        this.vendorService = vendorService;
        this.productService = productService;
    }

    @GetMapping
    public String getDashboard(Model model) {
        // TODO: Get vendor from authenticated user
        Long vendorId = 1L; // Placeholder
        Vendor vendor = vendorService.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        model.addAttribute("vendor", vendor);
        model.addAttribute("activeProductCount", productService.countActiveByVendor(vendor));
        model.addAttribute("lowStockProducts", productService.findLowStockProductsByVendor(vendor));

        // TODO: Add sales metrics and other analytics

        return "vendor/dashboard";
    }
}
