package com.recicar.marketplace.repository;

import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.entity.ProductCondition;
import com.recicar.marketplace.entity.Vendor;
import com.recicar.marketplace.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Find all active products with pagination
     */
    Page<Product> findByActiveTrue(Pageable pageable);

    /**
     * Find products by vendor
     */
    Page<Product> findByVendor(Vendor vendor, Pageable pageable);

    /**
     * Find active products by vendor
     */
    Page<Product> findByVendorAndActiveTrue(Vendor vendor, Pageable pageable);

    /**
     * Find products by category
     */
    Page<Product> findByCategory(Category category, Pageable pageable);

    /**
     * Find active products by category
     */
    Page<Product> findByCategoryAndActiveTrue(Category category, Pageable pageable);

    /**
     * Find products by condition
     */
    Page<Product> findByCondition(ProductCondition condition, Pageable pageable);

    /**
     * Find active products by condition
     */
    Page<Product> findByConditionAndActiveTrue(ProductCondition condition, Pageable pageable);

    /**
     * Find products by price range
     */
    @Query("SELECT p FROM Product p WHERE p.active = true AND p.price BETWEEN :minPrice AND :maxPrice")
    Page<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice, 
                                   @Param("maxPrice") BigDecimal maxPrice, 
                                   Pageable pageable);

    /**
     * Search products by name (case insensitive)
     */
    @Query("SELECT p FROM Product p WHERE p.active = true AND LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Product> findByNameContainingIgnoreCase(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Search products by name or part number
     */
    @Query("SELECT p FROM Product p WHERE p.active = true AND " +
           "(LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.partNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(p.oemNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    Page<Product> searchByNameOrPartNumber(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Find products with low stock (less than 5 items)
     */
    @Query("SELECT p FROM Product p WHERE p.active = true AND p.stockQuantity < 5 AND p.stockQuantity > 0")
    List<Product> findLowStockProducts();

    /**
     * Find out of stock products
     */
    @Query("SELECT p FROM Product p WHERE p.active = true AND p.stockQuantity = 0")
    List<Product> findOutOfStockProducts();

    /**
     * Find products by vendor with low stock
     */
    @Query("SELECT p FROM Product p WHERE p.vendor = :vendor AND p.active = true AND p.stockQuantity < 5 AND p.stockQuantity > 0")
    List<Product> findLowStockProductsByVendor(@Param("vendor") Vendor vendor);

    /**
     * Find products compatible with specific vehicle
     */
    @Query("SELECT DISTINCT p FROM Product p JOIN p.compatibilities vc WHERE " +
           "p.active = true AND LOWER(vc.make) = LOWER(:make) AND LOWER(vc.model) = LOWER(:model) " +
           "AND LOWER(vc.engine) = LOWER(:engine) AND :year BETWEEN vc.yearFrom AND vc.yearTo")
    Page<Product> findByVehicleCompatibility(@Param("make") String make, 
                                           @Param("model") String model, 
                                           @Param("engine") String engine,
                                           @Param("year") Integer year, 
                                           Pageable pageable);

    /**
     * Advanced search with multiple criteria
     */
    @Query("SELECT p FROM Product p WHERE p.active = true " +
           "AND (:searchTerm IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(p.partNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "AND (:category IS NULL OR p.category = :category) " +
           "AND (:condition IS NULL OR p.condition = :condition) " +
           "AND (:minPrice IS NULL OR p.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.price <= :maxPrice) " +
           "AND (:vendor IS NULL OR p.vendor = :vendor)")
    Page<Product> findWithFilters(@Param("searchTerm") String searchTerm,
                                  @Param("category") Category category,
                                  @Param("condition") ProductCondition condition,
                                  @Param("minPrice") BigDecimal minPrice,
                                  @Param("maxPrice") BigDecimal maxPrice,
                                  @Param("vendor") Vendor vendor,
                                  Pageable pageable);

    /**
     * Count products by vendor
     */
    long countByVendor(Vendor vendor);

    /**
     * Count active products by vendor
     */
    long countByVendorAndActiveTrue(Vendor vendor);

    /**
     * Find products by part number (exact match)
     */
    List<Product> findByPartNumberIgnoreCase(String partNumber);

    /**
     * Find products by OEM number (exact match)
     */
    List<Product> findByOemNumberIgnoreCase(String oemNumber);
}