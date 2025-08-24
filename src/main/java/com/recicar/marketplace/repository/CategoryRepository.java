package com.recicar.marketplace.repository;

import com.recicar.marketplace.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

       /**
        * Find category by slug
        */
       Optional<Category> findBySlug(String slug);

       /**
        * Find all active categories
        */
       List<Category> findByActiveTrueOrderBySortOrderAsc();

       /**
        * Find all root categories (no parent)
        */
       List<Category> findByParentIsNullAndActiveTrueOrderBySortOrderAsc();

       /**
        * Find all child categories of a parent
        */
       List<Category> findByParentAndActiveTrueOrderBySortOrderAsc(Category parent);

       /**
        * Find categories by name (case insensitive)
        */
       List<Category> findByNameContainingIgnoreCaseAndActiveTrue(String name);

       /**
        * Check if slug exists
        */
       boolean existsBySlug(String slug);

       /**
        * Check if slug exists for different category (for updates)
        */
       @Query("SELECT COUNT(c) > 0 FROM Category c WHERE c.slug = :slug AND c.id != :id")
       boolean existsBySlugAndIdNot(@Param("slug") String slug, @Param("id") Long id);

       /**
        * Find all categories in hierarchy (parent and all descendants)
        */
       @Query(value = "WITH RECURSIVE category_tree AS (" +
                     "SELECT id, name, slug, parent_id, 0 as level FROM categories WHERE id = :categoryId " +
                     "UNION ALL " +
                     "SELECT c.id, c.name, c.slug, c.parent_id, ct.level + 1 " +
                     "FROM categories c JOIN category_tree ct ON c.parent_id = ct.id" +
                     ") SELECT * FROM category_tree", nativeQuery = true)
       List<Category> findCategoryHierarchy(@Param("categoryId") Long categoryId);

       /**
        * Count products in category (including subcategories)
        */
       @Query("SELECT COUNT(p) FROM Product p WHERE p.category.id IN " +
                     "(SELECT c.id FROM Category c WHERE c.id = :categoryId OR c.parent.id = :categoryId) " +
                     "AND p.active = true")
       long countProductsInCategory(@Param("categoryId") Long categoryId);

       /**
        * Find categories with products
        */
       @Query("SELECT DISTINCT c FROM Category c JOIN c.products p WHERE c.active = true AND p.active = true")
       List<Category> findCategoriesWithProducts();

       /**
        * Find top-level categories with product counts
        */
       @Query("SELECT c, COUNT(p) as productCount FROM Category c " +
                     "LEFT JOIN c.products p ON p.active = true " +
                     "WHERE c.parent IS NULL AND c.active = true " +
                     "GROUP BY c ORDER BY c.sortOrder ASC")
       List<Object[]> findRootCategoriesWithProductCounts();
}