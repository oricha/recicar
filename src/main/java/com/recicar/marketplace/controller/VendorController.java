package com.recicar.marketplace.controller;

import com.recicar.marketplace.dto.VendorRegistrationRequest;
import com.recicar.marketplace.entity.Vendor;
import com.recicar.marketplace.service.VendorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vendors")
public class VendorController {

    private final VendorService vendorService;

    public VendorController(VendorService vendorService) {
        this.vendorService = vendorService;
    }

    @PostMapping("/register")
    public ResponseEntity<Vendor> registerVendor(@RequestBody VendorRegistrationRequest request) {
        Vendor vendor = vendorService.registerVendor(request);
        return ResponseEntity.ok(vendor);
    }
}
