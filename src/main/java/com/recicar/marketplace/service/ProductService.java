package com.recicar.marketplace.service;

import com.recicar.marketplace.dto.ProductRequest;
import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.entity.ProductCondition;
import com.recicar.marketplace.entity.Category;
import com.recicar.marketplace.entity.Vendor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface ProductService {

    /**
     * Create or update product
     */
    Product createOrUpdateProduct(ProductRequest request);

    /**
     * Get all active products with default pagination (12 per page, sorted by creation date)
     */
    Page<Product> findActiveProducts(int page);

    /**
     * Find product by ID
     */
    Optional<Product> findById(Long id);

    /**
     * Find active product by ID
     */
    Optional<Product> findActiveById(Long id);

    /**
     * Search products by name or part number
     */
    Page<Product> searchProducts(String searchTerm, Pageable pageable);

    /**
     * Search products with default pagination
     */
    Page<Product> searchProducts(String searchTerm, int page);

    /**
     * Find products by category
     */
    Page<Product> findByCategory(Category category, Pageable pageable);

    /**
     * Find products by category with default pagination
     */
    Page<Product> findByCategory(Category category, int page);

    /**
     * Find products by vendor
     */
    Page<Product> findByVendor(Vendor vendor, Pageable pageable);

    /**
     * Find products by condition
     */
    Page<Product> findByCondition(ProductCondition condition, Pageable pageable);

    /**
     * Find products by price range
     */
    Page<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

    /**
     * Advanced search with multiple filters
     */
    Page<Product> findWithFilters(String searchTerm, Category category, ProductCondition condition,
                                        BigDecimal minPrice, BigDecimal maxPrice, Vendor vendor,
                                        Pageable pageable);

    /**
     * Advanced search with comprehensive filtering including stock and availability
     */
    Page<Product> findWithAdvancedFilters(String searchTerm, Category category, ProductCondition condition,
                                                BigDecimal minPrice, BigDecimal maxPrice, Boolean inStock,
                                                Boolean lowStock, Vendor vendor, int page, String sortBy, String sortDir);

    /**
     * Advanced search with filters and default pagination
     */
    Page<Product> findWithFilters(String searchTerm, Category category, ProductCondition condition,
                                        BigDecimal minPrice, BigDecimal maxPrice, Vendor vendor,
                                        int page, String sortBy, String sortDir);

    /**
     * Find products compatible with specific vehicle
     */
    Page<Product> findByVehicleCompatibility(String make, String model, String engine, Integer year, Pageable pageable);

    /**
     * Find products by part number
     */
    List<Product> findByPartNumber(String partNumber);

    /**
     * Find products by OEM number
     */
    List<Product> findByOemNumber(String oemNumber);

    /**
     * Find products by vehicle compatibility with default pagination
     */
    Page<Product> findByVehicleCompatibility(String make, String model, String engine, Integer year, int page);



    /**
     * Decrease product stock
     */
    void decreaseStock(Long productId, Integer quantity);

    /**
     * Increase product stock
     */
    void increaseStock(Long productId, Integer quantity);

    /**
     * Update product stock
     */
    void updateStock(Long productId, Integer quantity);

    /**
     * Bulk update stock for multiple products
     */
    void bulkUpdateStock(List<Long> productIds, List<Integer> quantities);

    /**
     * Get inventory report
     */
    List<Product> getInventoryReport();

    /**
     * Get inventory report by vendor
     */
    List<Product> getInventoryReportByVendor(Vendor vendor);

    /**
     * Find other vendors selling the same product
     */
    List<Vendor> findOtherVendorsSellingProduct(Long productId);

    /**
     * Find products with low stock (less than 5 items)
     */
    List<Product> findLowStockProducts();

    /**
     * Find products with low stock for a specific vendor
     */
    List<Product> findLowStockProductsByVendor(Vendor vendor);

    /**
     * Count active products by vendor
     */
    long countActiveByVendor(Vendor vendor);
}