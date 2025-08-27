package com.recicar.marketplace.controller;

import com.recicar.marketplace.dto.ProductRequest;
import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vendor/products")
public class VendorProductController {

    private final ProductService productService;

    public VendorProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody ProductRequest request) {
        Product product = productService.createOrUpdateProduct(request);
        return ResponseEntity.ok(product);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody ProductRequest request) {
        request.setId(id);
        Product product = productService.createOrUpdateProduct(request);
        return ResponseEntity.ok(product);
    }
}
