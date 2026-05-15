package com.recicar.marketplace.controller;

import com.recicar.marketplace.dto.BrandListItemDto;
import com.recicar.marketplace.dto.ProductCardDto;
import com.recicar.marketplace.entity.Brand;
import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.service.BrandService;
import com.recicar.marketplace.service.ProductService;
import com.recicar.marketplace.service.SearchService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/products")
public class ProductListingApiController {

    private final ProductService productService;
    private final SearchService searchService;
    private final BrandService brandService;

    public ProductListingApiController(
            ProductService productService,
            SearchService searchService,
            BrandService brandService) {
        this.productService = productService;
        this.searchService = searchService;
        this.brandService = brandService;
    }

    /**
     * Paginated catalog (optional filters align with Phase 1 checklist).
     */
    @GetMapping({"", "/"})
    public ResponseEntity<Map<String, Object>> listRoot(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) Long category,
            @RequestParam(required = false) BigDecimal priceMin,
            @RequestParam(required = false) BigDecimal priceMax,
            @RequestParam(required = false) String condition,
            @RequestParam(required = false) Boolean available
    ) {
        return ResponseEntity.ok(toPageMap(resolveCardPage(page, size, category, priceMin, priceMax, condition, available)));
    }

    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return ResponseEntity.ok(toPageMap(resolveCardPage(page, size, null, null, null, null, null)));
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchByQuery(
            @RequestParam("q") String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products = productService.searchProducts(q, pageable);
        Page<ProductCardDto> cards = productService.mapToProductCardPage(products);
        return ResponseEntity.ok(toPageMap(cards));
    }

    /**
     * Brand directory for catalog home / parity with {@code GET /api/v1/brands}.
     */
    @GetMapping("/brands")
    public ResponseEntity<List<BrandListItemDto>> brands() {
        List<BrandListItemDto> list = brandService.findAll().stream()
                .map(this::toBrandListItem)
                .sorted(Comparator.comparing(BrandListItemDto::name, String.CASE_INSENSITIVE_ORDER))
                .toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> count() {
        return ResponseEntity.ok(Map.of("count", productService.countActiveProducts()));
    }

    @GetMapping("/{id}/seller-info")
    public ResponseEntity<?> sellerInfo(@PathVariable("id") Long id) {
        return productService.getSellerInfoByProductId(id)
                .<ResponseEntity<?>>map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private Page<ProductCardDto> resolveCardPage(
            int page,
            int size,
            Long categoryId,
            BigDecimal priceMin,
            BigDecimal priceMax,
            String condition,
            Boolean available
    ) {
        Pageable pageable = PageRequest.of(page, size);
        boolean filtered = categoryId != null || priceMin != null || priceMax != null
                || (condition != null && !condition.isBlank()) || available != null;
        if (!filtered) {
            return productService.getProductCards(page, size);
        }
        String cond = condition == null || condition.isBlank() ? null : condition;
        Page<Product> products = searchService.searchAdvanced(
                null, null, null, null, cond, available, priceMin, priceMax, categoryId, pageable);
        return productService.mapToProductCardPage(products);
    }

    private static Map<String, Object> toPageMap(Page<ProductCardDto> result) {
        return Map.of(
                "content", result.getContent(),
                "page", result.getNumber(),
                "size", result.getSize(),
                "totalElements", result.getTotalElements(),
                "totalPages", result.getTotalPages()
        );
    }

    private BrandListItemDto toBrandListItem(Brand b) {
        return new BrandListItemDto(
                b.getId(),
                b.getName(),
                b.getSlug(),
                b.getCountry() == null ? "" : b.getCountry()
        );
    }
}
