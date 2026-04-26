package com.recicar.marketplace.controller;

import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.entity.SavedSearch;
import com.recicar.marketplace.entity.User;
import com.recicar.marketplace.repository.UserRepository;
import com.recicar.marketplace.service.SearchService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class SearchApiController {

    private final SearchService searchService;
    private final UserRepository userRepository;

    public SearchApiController(SearchService searchService, UserRepository userRepository) {
        this.searchService = searchService;
        this.userRepository = userRepository;
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> search(
            @RequestParam("q") String query,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> results = searchService.searchProducts(query, pageable);
        return ResponseEntity.ok(toPageResponse(results));
    }

    @GetMapping("/search/advanced")
    public ResponseEntity<Map<String, Object>> searchAdvanced(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String model,
            @RequestParam(required = false) String modification,
            @RequestParam(required = false) String condition,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "relevance") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        Pageable pageable = toPageable(page, size, sort);
        Page<Product> results = searchService.searchAdvanced(
                query, brand, model, modification, condition, inStock, minPrice, maxPrice, pageable
        );
        return ResponseEntity.ok(toPageResponse(results));
    }

    @GetMapping("/search/suggestions")
    public ResponseEntity<List<String>> suggestions(@RequestParam("q") String query) {
        return ResponseEntity.ok(searchService.getSearchSuggestions(query));
    }

    @PostMapping("/user/saved-searches")
    public ResponseEntity<Map<String, Object>> saveSearch(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, String> payload
    ) {
        User user = resolveUser(userDetails);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Authentication required"));
        }

        String query = payload.getOrDefault("query", "");
        String filtersJson = payload.getOrDefault("filtersJson", "{}");
        SavedSearch savedSearch = searchService.saveSearch(user.getId(), query, filtersJson);

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "id", savedSearch.getId(),
                "query", savedSearch.getSearchQuery(),
                "filtersJson", savedSearch.getFiltersJson(),
                "createdAt", savedSearch.getCreatedAt()
        ));
    }

    @GetMapping("/user/saved-searches")
    public ResponseEntity<?> getSavedSearches(@AuthenticationPrincipal UserDetails userDetails) {
        User user = resolveUser(userDetails);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Authentication required"));
        }

        List<Map<String, Object>> savedSearches = searchService.getSavedSearches(user.getId()).stream()
                .map(search -> Map.<String, Object>of(
                        "id", search.getId(),
                        "query", search.getSearchQuery(),
                        "filtersJson", search.getFiltersJson() == null ? "{}" : search.getFiltersJson(),
                        "createdAt", search.getCreatedAt()
                ))
                .toList();

        return ResponseEntity.ok(savedSearches);
    }

    private Pageable toPageable(int page, int size, String sort) {
        return switch (sort) {
            case "price_asc" -> PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "price"));
            case "price_desc" -> PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "price"));
            default -> PageRequest.of(page, size);
        };
    }

    private Map<String, Object> toPageResponse(Page<Product> page) {
        List<Map<String, Object>> items = page.getContent().stream()
                .map(product -> Map.<String, Object>of(
                        "id", product.getId(),
                        "name", product.getName(),
                        "partNumber", product.getPartNumber() == null ? "" : product.getPartNumber(),
                        "oemNumber", product.getOemNumber() == null ? "" : product.getOemNumber(),
                        "price", product.getPrice(),
                        "condition", product.getCondition() == null ? "" : product.getCondition().name(),
                        "inStock", product.isInStock()
                ))
                .toList();

        return Map.of(
                "content", items,
                "page", page.getNumber(),
                "size", page.getSize(),
                "totalElements", page.getTotalElements(),
                "totalPages", page.getTotalPages()
        );
    }

    private User resolveUser(UserDetails userDetails) {
        if (userDetails == null) {
            return null;
        }
        return userRepository.findByEmailIgnoreCase(userDetails.getUsername()).orElse(null);
    }
}
