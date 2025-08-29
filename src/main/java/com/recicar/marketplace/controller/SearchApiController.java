package com.recicar.marketplace.controller;

import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.service.SearchService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST API controller for search-related operations
 */
@RestController
@RequestMapping("/api/search")
public class SearchApiController {

    private final SearchService searchService;

    public SearchApiController(SearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * Get distinct car makes.
     */
    @GetMapping("/vehicles/makes")
    public ResponseEntity<List<String>> getVehicleMakes() {
        try {
            List<String> makes = searchService.getDistinctMakes();
            return ResponseEntity.ok(makes);
        } catch (Exception e) {
            // Log the error
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get distinct car models for a given make.
     */
    @GetMapping("/vehicles/models")
    public ResponseEntity<List<String>> getVehicleModels(@RequestParam String make) {
        try {
            List<String> models = searchService.getModelsByMake(make);
            return ResponseEntity.ok(models);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(List.of(e.getMessage()));
        } catch (Exception e) {
            // Log the error
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get distinct engine types for a given make and model.
     */
    @GetMapping("/vehicles/engines")
    public ResponseEntity<List<String>> getVehicleEngines(@RequestParam String make, @RequestParam String model) {
        try {
            List<String> engines = searchService.getEnginesByMakeAndModel(make, model);
            return ResponseEntity.ok(engines);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(List.of(e.getMessage()));
        } catch (Exception e) {
            // Log the error
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get distinct year ranges for a given make, model, and engine.
     */
    @GetMapping("/vehicles/years")
    public ResponseEntity<List<String>> getVehicleYears(@RequestParam String make, @RequestParam String model, @RequestParam String engine) {
        try {
            List<String> years = searchService.getYearsByMakeModelAndEngine(make, model, engine);
            return ResponseEntity.ok(years);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(List.of(e.getMessage()));
        } catch (Exception e) {
            // Log the error
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Search products by vehicle compatibility.
     */
    @GetMapping("/products")
    public ResponseEntity<Page<Product>> searchProductsByVehicle(
            @RequestParam String make,
            @RequestParam String model,
            @RequestParam String engine,
            @RequestParam Integer year,
            Pageable pageable) {
        try {
            Page<Product> products = searchService.searchByVehicleCompatibility(make, model, engine, year, pageable);
            return ResponseEntity.ok(products);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            // Log the error
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get search suggestions for autocomplete
     */
    @GetMapping("/suggestions")
    public ResponseEntity<List<String>> getSearchSuggestions(@RequestParam String q) {
        try {
            List<String> suggestions = searchService.getSearchSuggestions(q);
            return ResponseEntity.ok(suggestions);
        } catch (Exception e) {
            // Log the error for debugging
            // logger.error("Error getting search suggestions: {}", e.getMessage(), e);
            return ResponseEntity.ok(List.of()); // Return empty list on error
        }
    }

    /**
     * Get search statistics for analytics
     */
    @GetMapping("/statistics")
    public ResponseEntity<SearchService.SearchStatistics> getSearchStatistics(@RequestParam String q) {
        try {
            SearchService.SearchStatistics statistics = searchService.getSearchStatistics(q);
            return ResponseEntity.ok(statistics);
        } catch (Exception e) {
            // Log the error for debugging
            // logger.error("Error getting search statistics: {}", e.getMessage(), e);
            return ResponseEntity.ok(new SearchService.SearchStatistics(0, 0, 0)); // Return zero stats on error
        }
    }

    /**
     * Quick search for header search bar
     */
    @GetMapping("/quick")
    public ResponseEntity<List<QuickSearchResult>> quickSearch(@RequestParam String q, @RequestParam(defaultValue = "5") int limit) {
        try {
            if (q == null || q.trim().isEmpty() || q.trim().length() < 2) {
                return ResponseEntity.ok(List.of());
            }
            
            // For now, return empty list - this can be enhanced with actual quick search results
            // In a real implementation, you might want to return product names, categories, etc.
            return ResponseEntity.ok(List.of());
        } catch (Exception e) {
            // Log the error for debugging
            // logger.error("Error during quick search: {}", e.getMessage(), e);
            return ResponseEntity.ok(List.of());
        }
    }

    /**
     * Get popular search terms
     */
    @GetMapping("/popular")
    public ResponseEntity<List<String>> getPopularSearches() {
        try {
            // For now, return some common search terms
            // In a real implementation, this would come from analytics data
            List<String> popularSearches = List.of(
                "brake pads",
                "oil filter",
                "headlight",
                "tire",
                "battery",
                "spark plug",
                "air filter",
                "windshield wiper"
            );
            return ResponseEntity.ok(popularSearches);
        } catch (Exception e) {
            // Log the error for debugging
            // logger.error("Error getting popular searches: {}", e.getMessage(), e);
            return ResponseEntity.ok(List.of());
        }
    }

    /**
     * Get search filters metadata
     */
    @GetMapping("/filters")
    public ResponseEntity<SearchFiltersMetadata> getSearchFiltersMetadata() {
        try {
            // This could return available filter options, ranges, etc.
            SearchFiltersMetadata metadata = new SearchFiltersMetadata();
            return ResponseEntity.ok(metadata);
        } catch (Exception e) {
            // Log the error for debugging
            // logger.error("Error getting search filters metadata: {}", e.getMessage(), e);
            return ResponseEntity.ok(new SearchFiltersMetadata());
        }
    }

    /**
     * Inner class for quick search results
     */
    public static class QuickSearchResult {
        private String type; // "product", "category", "part_number"
        private String value;
        private String displayText;
        private String url;

        public QuickSearchResult() {}

        public QuickSearchResult(String type, String value, String displayText, String url) {
            this.type = type;
            this.value = value;
            this.displayText = displayText;
            this.url = url;
        }

        // Getters and Setters
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }
        
        public String getDisplayText() { return displayText; }
        public void setDisplayText(String displayText) { this.displayText = displayText; }
        
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
    }

    /**
     * Inner class for search filters metadata
     */
    public static class SearchFiltersMetadata {
        private List<String> categories;
        private List<String> conditions;
        private PriceRange priceRange;
        private List<String> vendors;

        public SearchFiltersMetadata() {
            // Initialize with default values
            this.categories = List.of();
            this.conditions = List.of();
            this.priceRange = new PriceRange(0.0, 10000.0);
            this.vendors = List.of();
        }

        // Getters and Setters
        public List<String> getCategories() { return categories; }
        public void setCategories(List<String> categories) { this.categories = categories; }
        
        public List<String> getConditions() { return conditions; }
        public void setConditions(List<String> conditions) { this.conditions = conditions; }
        
        public PriceRange getPriceRange() { return priceRange; }
        public void setPriceRange(PriceRange priceRange) { this.priceRange = priceRange; }
        
        public List<String> getVendors() { return vendors; }
        public void setVendors(List<String> vendors) { this.vendors = vendors; }

        /**
         * Inner class for price range
         */
        public static class PriceRange {
            private Double min;
            private Double max;

            public PriceRange() {}

            public PriceRange(Double min, Double max) {
                this.min = min;
                this.max = max;
            }

            // Getters and Setters
            public Double getMin() { return min; }
            public void setMin(Double min) { this.min = min; }
            
            public Double getMax() { return max; }
            public void setMax(Double max) { this.max = max; }
        }
    }
}

