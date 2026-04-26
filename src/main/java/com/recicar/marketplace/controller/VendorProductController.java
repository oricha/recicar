package com.recicar.marketplace.controller;

import com.recicar.marketplace.dto.ProductRequest;
import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.entity.Vendor;
import com.recicar.marketplace.service.ProductService;
import com.recicar.marketplace.service.VendorContextService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestController
@RequestMapping("/api/vendor/products")
@PreAuthorize("hasAnyRole('VENDOR', 'ADMIN')")
public class VendorProductController {

    private final ProductService productService;
    private final VendorContextService vendorContextService;

    public VendorProductController(ProductService productService, VendorContextService vendorContextService) {
        this.productService = productService;
        this.vendorContextService = vendorContextService;
    }

    @PostMapping
    public ResponseEntity<?> createProduct(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ProductRequest request) {
        Vendor vendor = requireVendorForApi(userDetails);
        request.setVendorId(vendor.getId());
        if (request.getId() != null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Do not send id on create"));
        }
        Product product = productService.createOrUpdateProduct(request);
        return ResponseEntity.ok(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @RequestBody ProductRequest request) {
        Vendor vendor = requireVendorForApi(userDetails);
        Product existing = productService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Product not found"));
        if (!existing.getVendor().getId().equals(vendor.getId())) {
            throw new ResponseStatusException(FORBIDDEN, "Not allowed to update this product");
        }
        request.setId(id);
        request.setVendorId(vendor.getId());
        Product product = productService.createOrUpdateProduct(request);
        return ResponseEntity.ok(product);
    }

    private Vendor requireVendorForApi(UserDetails userDetails) {
        if (userDetails == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authentication required");
        }
        return vendorContextService.findVendorForUserDetails(userDetails)
                .orElseThrow(() -> new ResponseStatusException(
                        FORBIDDEN, "No vendor account linked to this user"));
    }
}
