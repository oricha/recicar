package com.recicar.marketplace.service;

import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.entity.Category;
import com.recicar.marketplace.entity.ProductCondition;
import com.recicar.marketplace.entity.Vendor;
import com.recicar.marketplace.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class SearchService {

    private final ProductRepository productRepository;

    public SearchService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Search products by name or part number
     */
    @Transactional(readOnly = true)
    public Page<Product> searchProducts(String searchTerm, Pageable pageable) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            throw new IllegalArgumentException("Search term cannot be empty");
        }
        return productRepository.searchByNameOrPartNumber(searchTerm.trim(), pageable);
    }

    /**
     * Search products by part number (exact match, case insensitive)
     */
    @Transactional(readOnly = true)
    public List<Product> findByPartNumber(String partNumber) {
        return productRepository.findByPartNumberIgnoreCase(partNumber);
    }

    /**
     * Search products by OEM number (exact match, case insensitive)
     */
    @Transactional(readOnly = true)
    public List<Product> findByOemNumber(String oemNumber) {
        return productRepository.findByOemNumberIgnoreCase(oemNumber);
    }

    /**
     * Search by part number (alias for findByPartNumber)
     */
    @Transactional(readOnly = true)
    public List<Product> searchByPartNumber(String partNumber) {
        return findByPartNumber(partNumber);
    }

    /**
     * Search by OEM number (alias for findByOemNumber)
     */
    @Transactional(readOnly = true)
    public List<Product> searchByOemNumber(String oemNumber) {
        return findByOemNumber(oemNumber);
    }

    /**
     * Search by vehicle compatibility
     */
    @Transactional(readOnly = true)
    public Page<Product> searchByVehicleCompatibility(String make, String model, String engine, Integer year, Pageable pageable) {
        return productRepository.findByVehicleCompatibility(make, model, engine, year, pageable);
    }

    /**
     * Search with filters
     */
    @Transactional(readOnly = true)
    public Page<Product> searchWithFilters(String searchTerm, Category category, ProductCondition condition,
                                         BigDecimal minPrice, BigDecimal maxPrice, Vendor vendor, Pageable pageable) {
        return productRepository.findWithFilters(searchTerm, category, condition, minPrice, maxPrice, vendor, pageable);
    }

    /**
     * Get search suggestions
     */
    @Transactional(readOnly = true)
    public List<String> getSearchSuggestions(String partialTerm) {
        if (partialTerm == null || partialTerm.trim().length() < 2) {
            return List.of();
        }
        // This is a simplified implementation - in a real application you might want to use
        // a more sophisticated search suggestion system
        return List.of(partialTerm + " suggestion 1", partialTerm + " suggestion 2");
    }

    /**
     * Get search statistics for a given search term
     */
    @Transactional(readOnly = true)
    public SearchStatistics getSearchStatistics(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return new SearchStatistics(0, 0, 0, 0, 0);
        }

        String trimmedTerm = searchTerm.trim();
        
        // Count products by different search criteria
        long byName = productRepository.countByNameContainingIgnoreCase(trimmedTerm);
        long byPartNumber = productRepository.countByPartNumberContainingIgnoreCase(trimmedTerm);
        long byOemNumber = productRepository.countByOemNumberContainingIgnoreCase(trimmedTerm);

        return new SearchStatistics(byName, byPartNumber, byOemNumber, byName, byPartNumber);
    }

    /**
     * Inner class for search statistics
     */
    public static class SearchStatistics {
        private final long byNameCount;
        private final long byPartNumberCount;
        private final long byOemNumberCount;
        private final long inStockResults;
        private final long outOfStockResults;

        public SearchStatistics(long byNameCount, long byPartNumberCount, long byOemNumberCount, 
                              long inStockResults, long outOfStockResults) {
            this.byNameCount = byNameCount;
            this.byPartNumberCount = byPartNumberCount;
            this.byOemNumberCount = byOemNumberCount;
            this.inStockResults = inStockResults;
            this.outOfStockResults = outOfStockResults;
        }

        public long getByNameCount() {
            return byNameCount;
        }

        public long getByPartNumberCount() {
            return byPartNumberCount;
        }

        public long getByOemNumberCount() {
            return byOemNumberCount;
        }

        public long getTotalCount() {
            return byNameCount + byPartNumberCount + byOemNumberCount;
        }

        public long getTotalResults() {
            return getTotalCount();
        }

        public long getInStockResults() {
            return inStockResults;
        }

        public long getOutOfStockResults() {
            return outOfStockResults;
        }
    }
}
