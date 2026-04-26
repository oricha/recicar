package com.recicar.marketplace.controller;

import com.recicar.marketplace.dto.VendorOrderListItemDto;
import com.recicar.marketplace.entity.Vendor;
import com.recicar.marketplace.service.VendorContextService;
import com.recicar.marketplace.service.VendorOrderMetricsService;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/vendor/orders")
public class VendorOrderPageController {

    private final VendorContextService vendorContextService;
    private final VendorOrderMetricsService vendorOrderMetricsService;

    public VendorOrderPageController(
            VendorContextService vendorContextService,
            VendorOrderMetricsService vendorOrderMetricsService) {
        this.vendorContextService = vendorContextService;
        this.vendorOrderMetricsService = vendorOrderMetricsService;
    }

    @GetMapping
    public String orders(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Vendor vendor = vendorContextService.requireCurrentVendor(userDetails);
        Page<VendorOrderListItemDto> orders = vendorOrderMetricsService.getOrderListItemPage(vendor, page, size);

        model.addAttribute("vendor", vendor);
        model.addAttribute("orders", orders);
        model.addAttribute("pageTitle", "My Orders");
        return "vendor/orders";
    }
}
