package com.recicar.marketplace.controller;

import com.recicar.marketplace.service.ProductDetailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
public class ProductDetailApiController {

    private final ProductDetailService productDetailService;

    public ProductDetailApiController(ProductDetailService productDetailService) {
        this.productDetailService = productDetailService;
    }

    @GetMapping("/{id}/detail")
    public ResponseEntity<?> getDetail(@PathVariable("id") Long id) {
        return productDetailService.getProductDetail(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/images")
    public ResponseEntity<?> getImages(@PathVariable("id") Long id) {
        return ResponseEntity.ok(productDetailService.getProductImages(id));
    }

    @GetMapping("/{id}/related-parts")
    public ResponseEntity<?> getRelatedParts(@PathVariable("id") Long id) {
        return ResponseEntity.ok(productDetailService.getRelatedParts(id));
    }
}
