package com.recicar.marketplace.service;

import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.entity.Category;
import com.recicar.marketplace.entity.ProductCondition;
import com.recicar.marketplace.entity.Vendor;
import com.recicar.marketplace.repository.ProductRepository;
import com.recicar.marketplace.repository.VehicleCompatibilityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Service for handling product search operations with validation and business logic
 */
@Service
@Transactional
public class SearchService {

    private final ProductRepository productRepository;
    private final VehicleCompatibilityRepository vehicleCompatibilityRepository;

    public SearchService(ProductRepository productRepository, VehicleCompatibilityRepository vehicleCompatibilityRepository) {
        this.productRepository = productRepository;
        this.vehicleCompatibilityRepository = vehicleCompatibilityRepository;
    }

    /**
     * Search products by text query with validation
     */
    @Transactional(readOnly = true)
    public Page<Product> searchProducts(String searchTerm, Pageable pageable) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            throw new IllegalArgumentException("Search term cannot be empty");
        }
        
        String sanitizedTerm = sanitizeSearchTerm(searchTerm);
        validateSearchTerm(sanitizedTerm);
        
        return productRepository.searchByNameOrPartNumber(sanitizedTerm, pageable);
    }

    /**
     * Search products by part number with validation
     */
    @Transactional(readOnly = true)
    public List<Product> searchByPartNumber(String partNumber) {
        if (partNumber == null || partNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Part number cannot be empty");
        }
        
        String sanitizedPartNumber = sanitizeSearchTerm(partNumber);
        validateSearchTerm(sanitizedPartNumber);
        
        return productRepository.findByPartNumberIgnoreCase(sanitizedPartNumber);
    }

    /**
     * Search products by OEM number with validation
     */
    @Transactional(readOnly = true)
    public List<Product> searchByOemNumber(String oemNumber) {
        if (oemNumber == null || oemNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("OEM number cannot be empty");
        }
        
        String sanitizedOemNumber = sanitizeSearchTerm(oemNumber);
        validateSearchTerm(sanitizedOemNumber);
        
        return productRepository.findByOemNumberIgnoreCase(sanitizedOemNumber);
    }

    /**
     * Search products by vehicle compatibility with validation
     */
    @Transactional(readOnly = true)
    public Page<Product> searchByVehicleCompatibility(String make, String model, String engine, Integer year, Pageable pageable) {
        validateVehicleParameters(make, model, engine, year);
        
        String sanitizedMake = sanitizeSearchTerm(make);
        String sanitizedModel = sanitizeSearchTerm(model);
        String sanitizedEngine = sanitizeSearchTerm(engine);
        
        return productRepository.findByVehicleCompatibility(sanitizedMake, sanitizedModel, sanitizedEngine, year, pageable);
    }

    /**
     * Advanced search with multiple filters and validation
     */
    @Transactional(readOnly = true)
    public Page<Product> searchWithFilters(String searchTerm, Category category, ProductCondition condition,
                                          BigDecimal minPrice, BigDecimal maxPrice, Vendor vendor,
                                          Pageable pageable) {
        // Validate price range if provided
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new IllegalArgumentException("Minimum price cannot be greater than maximum price");
        }
        
        // Sanitize search term if provided
        String sanitizedSearchTerm = null;
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            sanitizedSearchTerm = sanitizeSearchTerm(searchTerm);
            validateSearchTerm(sanitizedSearchTerm);
        }
        
        return productRepository.findWithFilters(sanitizedSearchTerm, category, condition, minPrice, maxPrice, vendor, pageable);
    }

    /**
     * Sanitize search term by trimming and limiting length
     */
    private String sanitizeSearchTerm(String searchTerm) {
        if (searchTerm == null) {
            return null;
        }
        
        String sanitized = searchTerm.trim();
        
        // Limit length to prevent extremely long queries
        if (sanitized.length() > 100) {
            sanitized = sanitized.substring(0, 100);
        }
        
        return sanitized;
    }

    /**
     * Validate search term meets minimum requirements
     */
    private void validateSearchTerm(String searchTerm) {
        if (searchTerm == null || searchTerm.length() < 2) {
            throw new IllegalArgumentException("Search term must be at least 2 characters long");
        }
        
        // Check for potentially malicious patterns (basic XSS prevention)
        if (searchTerm.contains("<") || searchTerm.contains(">") || searchTerm.contains("script")) {
            throw new IllegalArgumentException("Search term contains invalid characters");
        }
    }

    /**
     * Validate vehicle parameters
     */
    private void validateVehicleParameters(String make, String model, String engine, Integer year) {
        if (make == null || make.trim().isEmpty()) {
            throw new IllegalArgumentException("Vehicle make is required");
        }
        
        if (model == null || model.trim().isEmpty()) {
            throw new IllegalArgumentException("Vehicle model is required");
        }

        if (engine == null || engine.trim().isEmpty()) {
            throw new IllegalArgumentException("Vehicle engine is required");
        }
        
        if (year == null) {
            throw new IllegalArgumentException("Vehicle year is required");
        }
        
        if (year < 1900 || year > 2030) {
            throw new IllegalArgumentException("Vehicle year must be between 1900 and 2030");
        }
        
        // Validate make and model length
        if (make.trim().length() < 2) {
            throw new IllegalArgumentException("Vehicle make must be at least 2 characters long");
        }
        
        if (model.trim().length() < 2) {
            throw new IllegalArgumentException("Vehicle model must be at least 2 characters long");
        }

        if (engine.trim().length() < 2) {
            throw new IllegalArgumentException("Vehicle engine must be at least 2 characters long");
        }
    }

    /**
     * Get search suggestions based on partial input
     */
    @Transactional(readOnly = true)
    public List<String> getSearchSuggestions(String partialTerm) {
        if (partialTerm == null || partialTerm.trim().isEmpty() || partialTerm.trim().length() < 2) {
            return List.of();
        }
        
        String sanitizedTerm = sanitizeSearchTerm(partialTerm);
        
        // Get suggestions from product names, part numbers, and categories
        List<String> suggestions = new ArrayList<>();
        
        try {
            // Get product name suggestions
            Page<Product> nameSuggestions = productRepository.searchByNameOrPartNumber(sanitizedTerm, 
                    org.springframework.data.domain.PageRequest.of(0, 5));
            
            suggestions.addAll(nameSuggestions.getContent().stream()
                    .map(Product::getName)
                    .filter(name -> name.toLowerCase().contains(sanitizedTerm.toLowerCase()))
                    .limit(3)
                    .toList());
            
            // Get part number suggestions
            List<Product> partNumberSuggestions = productRepository.findByPartNumberIgnoreCase(sanitizedTerm);
            suggestions.addAll(partNumberSuggestions.stream()
                    .map(Product::getPartNumber)
                    .filter(Objects::nonNull)
                    .limit(2)
                    .toList());
            
            // Get OEM number suggestions
            List<Product> oemSuggestions = productRepository.findByOemNumberIgnoreCase(sanitizedTerm);
            suggestions.addAll(oemSuggestions.stream()
                    .map(Product::getOemNumber)
                    .filter(Objects::nonNull)
                    .limit(2)
                    .toList());
            
            // Remove duplicates and limit total suggestions
            return suggestions.stream()
                    .distinct()
                    .limit(8)
                    .toList();
            
        } catch (Exception e) {
            // Log the error for debugging
            // logger.error("Error getting search suggestions: {}", e.getMessage(), e);
            return List.of();
        }
    }

    /**
     * Get search statistics for analytics
     */
    @Transactional(readOnly = true)
    public SearchStatistics getSearchStatistics(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return new SearchStatistics(0, 0, 0);
        }
        
        String sanitizedTerm = sanitizeSearchTerm(searchTerm);
        
        // Count total results
        long totalResults = productRepository.searchByNameOrPartNumber(sanitizedTerm, 
                org.springframework.data.domain.PageRequest.of(0, 1)).getTotalElements();
        
        // Count in-stock results
        long inStockResults = productRepository.searchByNameOrPartNumber(sanitizedTerm, 
                org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE))
                .getContent().stream()
                .filter(Product::isInStock)
                .count();
        
        // Count out-of-stock results
        long outOfStockResults = totalResults - inStockResults;
        
        return new SearchStatistics(totalResults, inStockResults, outOfStockResults);
    }

    /**
     * Get distinct car makes.
     */
    @Transactional(readOnly = true)
    public List<String> getDistinctMakes() {
        return vehicleCompatibilityRepository.findDistinctMakes();
    }

    /**
     * Get distinct car models for a given make.
     */
    @Transactional(readOnly = true)
    public List<String> getModelsByMake(String make) {
        if (make == null || make.trim().isEmpty()) {
            throw new IllegalArgumentException("Make cannot be empty");
        }
        return vehicleCompatibilityRepository.findDistinctModelsByMake(make);
    }

    /**
     * Get distinct engine types for a given make and model.
     */
    @Transactional(readOnly = true)
    public List<String> getEnginesByMakeAndModel(String make, String model) {
        if (make == null || make.trim().isEmpty() || model == null || model.trim().isEmpty()) {
            throw new IllegalArgumentException("Make and model cannot be empty");
        }
        return vehicleCompatibilityRepository.findDistinctEnginesByMakeAndModel(make, model);
    }

    /**
     * Get distinct year ranges for a given make, model, and engine.
     */
    @Transactional(readOnly = true)
    public List<String> getYearsByMakeModelAndEngine(String make, String model, String engine) {
        if (make == null || make.trim().isEmpty() || model == null || model.trim().isEmpty() || engine == null || engine.trim().isEmpty()) {
            throw new IllegalArgumentException("Make, model, and engine cannot be empty");
        }
        List<Object[]> yearRanges = vehicleCompatibilityRepository.findDistinctYearRangesByMakeModelAndEngine(make, model, engine);
        return yearRanges.stream()
                .map(range -> {
                    Integer yearFrom = (Integer) range[0];
                    Integer yearTo = (Integer) range[1];
                    if (yearFrom.equals(yearTo)) {
                        return String.valueOf(yearFrom);
                    } else {
                        return yearFrom + "-" + yearTo;
                    }
                })
                .distinct()
                .collect(Collectors.toList());
    }

    

    /**
     * Inner class to hold search statistics
     */
    public static class SearchStatistics {
        private final long totalResults;
        private final long inStockResults;
        private final long outOfStockResults;

        public SearchStatistics(long totalResults, long inStockResults, long outOfStockResults) {
            this.totalResults = totalResults;
            this.inStockResults = inStockResults;
            this.outOfStockResults = outOfStockResults;
        }

        // Getters
        public long getTotalResults() { return totalResults; }
        public long getInStockResults() { return inStockResults; }
        public long getOutOfStockResults() { return outOfStockResults; }
    }
}
