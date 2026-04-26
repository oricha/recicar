package com.recicar.marketplace.repository;

import com.recicar.marketplace.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

@org.springframework.stereotype.Repository
public interface SearchRepository extends Repository<Product, Long> {

    @Query(value = """
            SELECT p.* FROM products p
            WHERE p.active = true
              AND (
                to_tsvector('simple', coalesce(p.name, '') || ' ' || coalesce(p.part_number, '') || ' ' || coalesce(p.oem_number, ''))
                @@ plainto_tsquery('simple', :query)
                OR lower(p.name) LIKE lower(concat('%', :query, '%'))
                OR lower(p.part_number) LIKE lower(concat('%', :query, '%'))
                OR lower(p.oem_number) LIKE lower(concat('%', :query, '%'))
              )
            """,
            countQuery = """
            SELECT count(1) FROM products p
            WHERE p.active = true
              AND (
                to_tsvector('simple', coalesce(p.name, '') || ' ' || coalesce(p.part_number, '') || ' ' || coalesce(p.oem_number, ''))
                @@ plainto_tsquery('simple', :query)
                OR lower(p.name) LIKE lower(concat('%', :query, '%'))
                OR lower(p.part_number) LIKE lower(concat('%', :query, '%'))
                OR lower(p.oem_number) LIKE lower(concat('%', :query, '%'))
              )
            """,
            nativeQuery = true)
    Page<Product> searchSimple(@Param("query") String query, Pageable pageable);

    @Query(value = """
            SELECT DISTINCT p.* FROM products p
            LEFT JOIN vehicle_compatibility vc ON vc.product_id = p.id
            WHERE p.active = true
              AND (:query IS NULL OR :query = '' OR lower(p.name) LIKE lower(concat('%', :query, '%')) OR lower(p.part_number) LIKE lower(concat('%', :query, '%')))
              AND (:brand IS NULL OR :brand = '' OR lower(vc.make) = lower(:brand))
              AND (:model IS NULL OR :model = '' OR lower(vc.model) = lower(:model))
              AND (:modification IS NULL OR :modification = '' OR lower(vc.engine) LIKE lower(concat('%', :modification, '%')))
              AND (:condition IS NULL OR :condition = '' OR p.condition = cast(:condition as varchar))
              AND (:inStock IS NULL OR (:inStock = true AND p.stock_quantity > 0) OR (:inStock = false))
              AND (:minPrice IS NULL OR p.price >= :minPrice)
              AND (:maxPrice IS NULL OR p.price <= :maxPrice)
            """,
            countQuery = """
            SELECT count(DISTINCT p.id) FROM products p
            LEFT JOIN vehicle_compatibility vc ON vc.product_id = p.id
            WHERE p.active = true
              AND (:query IS NULL OR :query = '' OR lower(p.name) LIKE lower(concat('%', :query, '%')) OR lower(p.part_number) LIKE lower(concat('%', :query, '%')))
              AND (:brand IS NULL OR :brand = '' OR lower(vc.make) = lower(:brand))
              AND (:model IS NULL OR :model = '' OR lower(vc.model) = lower(:model))
              AND (:modification IS NULL OR :modification = '' OR lower(vc.engine) LIKE lower(concat('%', :modification, '%')))
              AND (:condition IS NULL OR :condition = '' OR p.condition = cast(:condition as varchar))
              AND (:inStock IS NULL OR (:inStock = true AND p.stock_quantity > 0) OR (:inStock = false))
              AND (:minPrice IS NULL OR p.price >= :minPrice)
              AND (:maxPrice IS NULL OR p.price <= :maxPrice)
            """,
            nativeQuery = true)
    Page<Product> searchAdvanced(
            @Param("query") String query,
            @Param("brand") String brand,
            @Param("model") String model,
            @Param("modification") String modification,
            @Param("condition") String condition,
            @Param("inStock") Boolean inStock,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice,
            Pageable pageable
    );

    @Query(value = """
            SELECT DISTINCT s.suggestion FROM (
              SELECT p.name AS suggestion FROM products p WHERE lower(p.name) LIKE lower(concat(:query, '%'))
              UNION
              SELECT p.part_number AS suggestion FROM products p WHERE p.part_number IS NOT NULL AND lower(p.part_number) LIKE lower(concat(:query, '%'))
              UNION
              SELECT p.oem_number AS suggestion FROM products p WHERE p.oem_number IS NOT NULL AND lower(p.oem_number) LIKE lower(concat(:query, '%'))
            ) s
            WHERE s.suggestion IS NOT NULL
            ORDER BY s.suggestion
            LIMIT 10
            """, nativeQuery = true)
    List<String> fetchSuggestions(@Param("query") String query);
}
