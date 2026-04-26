package com.recicar.marketplace.controller;

import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.entity.Vendor;
import com.recicar.marketplace.service.ProductService;
import com.recicar.marketplace.service.VendorContextService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/vendor/inventory")
public class VendorInventoryController {

    private final VendorContextService vendorContextService;
    private final ProductService productService;

    public VendorInventoryController(VendorContextService vendorContextService, ProductService productService) {
        this.vendorContextService = vendorContextService;
        this.productService = productService;
    }

    @GetMapping
    public String inventory(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Vendor vendor = vendorContextService.requireCurrentVendor(userDetails);
        if (size > 100) {
            size = 100;
        }
        Page<Product> products = productService.findAllProductsByVendorForManagement(
                vendor,
                PageRequest.of(page, size, Sort.by("name").ascending()));

        model.addAttribute("vendor", vendor);
        model.addAttribute("products", products);
        model.addAttribute("pageTitle", "Inventory");
        return "vendor/inventory";
    }
}
