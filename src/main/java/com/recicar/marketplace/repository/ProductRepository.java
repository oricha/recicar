package com.recicar.marketplace.repository;

import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.entity.Category;
import com.recicar.marketplace.entity.ProductCondition;
import com.recicar.marketplace.entity.Vendor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(p.partNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Product> searchByNameOrPartNumber(@Param("searchTerm") String searchTerm, Pageable pageable);

    Page<Product> findByPartNumberContaining(String partNumber, Pageable pageable);

    Page<Product> findByOemNumberContaining(String oemNumber, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :productName, '%'))")
    Page<Product> findByProductName(@Param("productName") String productName, Pageable pageable);

    @Query("SELECT p FROM Product p JOIN p.compatibilities c WHERE c.make = :make AND c.model = :model AND c.engine = :engine AND c.yearFrom <= :year AND c.yearTo >= :year")
    Page<Product> findByVehicleCompatibility(@Param("make") String make, @Param("model") String model, @Param("engine") String engine, @Param("year") Integer year, Pageable pageable);

    @Query("SELECT p FROM Product p JOIN p.compatibilities c " +
           "WHERE c.make = :make AND c.model = :model AND c.engine = :engine " +
           "AND (:partName IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :partName, '%')))")
    Page<Product> findByMakeModelEngineAndPartName(@Param("make") String make,
                                                   @Param("model") String model,
                                                   @Param("engine") String engine,
                                                   @Param("partName") String partName,
                                                   Pageable pageable);

    @Query("SELECT p FROM Product p WHERE (:searchTerm IS NULL OR (LOWER(p.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(p.partNumber) LIKE LOWER(CONCAT('%', :searchTerm, '%')))) AND (:category IS NULL OR p.category = :category) AND (:condition IS NULL OR p.condition = :condition) AND (:minPrice IS NULL OR p.price >= :minPrice) AND (:maxPrice IS NULL OR p.price <= :maxPrice) AND (:vendor IS NULL OR p.vendor = :vendor)")
    Page<Product> findWithFilters(@Param("searchTerm") String searchTerm, @Param("category") Category category, @Param("condition") ProductCondition condition, @Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice, @Param("vendor") Vendor vendor, Pageable pageable);

    Page<Product> findByVendorAndActiveTrue(Vendor vendor, Pageable pageable);

    Page<Product> findByActiveTrue(Pageable pageable);

    Page<Product> findByCategoryAndActiveTrue(Category category, Pageable pageable);

    Page<Product> findByCategoryIdInAndActiveTrue(List<Long> categoryIds, Pageable pageable);

    Page<Product> findByConditionAndActiveTrue(ProductCondition condition, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice")
    Page<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice, Pageable pageable);

    @Query("SELECT p FROM Product p JOIN FETCH p.vendor v JOIN FETCH v.user WHERE p.stockQuantity < 5")
    List<Product> findLowStockProducts();

    @Query("SELECT p FROM Product p JOIN FETCH p.vendor v JOIN FETCH v.user WHERE p.vendor = :vendor AND p.stockQuantity < 5")
    List<Product> findLowStockProductsByVendor(@Param("vendor") Vendor vendor);

    long countByVendorAndActiveTrue(Vendor vendor);

    // Methods for exact match searches (case insensitive)
    List<Product> findByPartNumberIgnoreCase(String partNumber);
    
    List<Product> findByOemNumberIgnoreCase(String oemNumber);
    
    // Count methods for search statistics
    long countByNameContainingIgnoreCase(String name);
    
    long countByPartNumberContainingIgnoreCase(String partNumber);
    
    long countByOemNumberContainingIgnoreCase(String oemNumber);
}
