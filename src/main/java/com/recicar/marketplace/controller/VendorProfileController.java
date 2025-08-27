package com.recicar.marketplace.controller;

import com.recicar.marketplace.entity.Vendor;
import com.recicar.marketplace.service.VendorService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/vendors")
public class VendorProfileController {

    private final VendorService vendorService;

    public VendorProfileController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    @GetMapping("/{id}")
    public String getVendorProfile(@PathVariable Long id, Model model) {
        Vendor vendor = vendorService.findById(id)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
        model.addAttribute("vendor", vendor);
        // TODO: Add vendor products, reviews, etc.
        return "vendor/profile";
    }
}
