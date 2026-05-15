package com.recicar.marketplace.service;

import com.recicar.marketplace.entity.Category;
import com.recicar.marketplace.repository.CategoryRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Find category by ID
     */
    @Transactional(readOnly = true)
    public Optional<Category> findById(Long id) {
        return categoryRepository.findById(id);
    }

    /**
     * Find category by slug
     */
    @Transactional(readOnly = true)
    public Optional<Category> findBySlug(String slug) {
        return categoryRepository.findBySlug(slug);
    }

    /**
     * Get all active categories
     */
    @Transactional(readOnly = true)
    public List<Category> findAllActive() {
        return categoryRepository.findByActiveTrueOrderBySortOrderAsc();
    }

    /**
     * Get all root categories (no parent)
     */
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "navCategoryRoots", key = "'roots'", unless = "#result == null || #result.isEmpty()")
    public List<Category> findRootCategories() {
        List<Category> roots = categoryRepository.findByParentIsNullAndActiveTrueOrderBySortOrderAsc();
        roots.forEach(this::initializeNavigationBranch);
        return roots;
    }

    /**
     * Get child categories of a parent
     */
    @Transactional(readOnly = true)
    public List<Category> findChildCategories(Category parent) {
        return categoryRepository.findByParentAndActiveTrueOrderBySortOrderAsc(parent);
    }

    /**
     * Get child categories of a parent id.
     */
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "navCategoryChildren", key = "#parentId", unless = "#result == null || #result.isEmpty()")
    public List<Category> findByParentId(Long parentId) {
        return categoryRepository.findByParentIdAndActiveTrueOrderBySortOrderAsc(parentId);
    }

    /**
     * Search categories by name
     */
    @Transactional(readOnly = true)
    public List<Category> searchByName(String name) {
        return categoryRepository.findByNameContainingIgnoreCaseAndActiveTrue(name);
    }

    /**
     * Get categories with products
     */
    @Transactional(readOnly = true)
    public List<Category> findCategoriesWithProducts() {
        return categoryRepository.findCategoriesWithProducts();
    }

    /**
     * Get root categories with product counts
     */
    @Transactional(readOnly = true)
    public List<Object[]> findRootCategoriesWithProductCounts() {
        return categoryRepository.findRootCategoriesWithProductCounts();
    }

    /**
     * Count products in category
     */
    @Transactional(readOnly = true)
    public long countProductsInCategory(Long categoryId) {
        return categoryRepository.countProductsInCategory(categoryId);
    }

    /**
     * Save or update category
     */
    @Caching(evict = {
            @CacheEvict(cacheNames = "navCategoryBySlug", allEntries = true),
            @CacheEvict(cacheNames = "navCategoryRoots", allEntries = true),
            @CacheEvict(cacheNames = "navCategoryChildren", allEntries = true),
            @CacheEvict(cacheNames = "navCategoryAll", allEntries = true)
    })
    public Category save(Category category) {
        return categoryRepository.save(category);
    }

    /**
     * Create new category
     */
    @Caching(evict = {
            @CacheEvict(cacheNames = "navCategoryBySlug", allEntries = true),
            @CacheEvict(cacheNames = "navCategoryRoots", allEntries = true),
            @CacheEvict(cacheNames = "navCategoryChildren", allEntries = true),
            @CacheEvict(cacheNames = "navCategoryAll", allEntries = true)
    })
    public Category createCategory(String name, String description, Category parent) {
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setParent(parent);
        category.setSlug(generateSlug(name));
        category.setActive(true);
        
        return categoryRepository.save(category);
    }

    /**
     * Update category
     */
    @Caching(evict = {
            @CacheEvict(cacheNames = "navCategoryBySlug", allEntries = true),
            @CacheEvict(cacheNames = "navCategoryRoots", allEntries = true),
            @CacheEvict(cacheNames = "navCategoryChildren", allEntries = true),
            @CacheEvict(cacheNames = "navCategoryAll", allEntries = true)
    })
    public Category updateCategory(Long id, String name, String description, Category parent) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        
        category.setName(name);
        category.setDescription(description);
        category.setParent(parent);
        
        // Update slug if name changed
        if (!category.getName().equals(name)) {
            category.setSlug(generateSlug(name));
        }
        
        return categoryRepository.save(category);
    }

    /**
     * Deactivate category
     */
    @Caching(evict = {
            @CacheEvict(cacheNames = "navCategoryBySlug", allEntries = true),
            @CacheEvict(cacheNames = "navCategoryRoots", allEntries = true),
            @CacheEvict(cacheNames = "navCategoryChildren", allEntries = true),
            @CacheEvict(cacheNames = "navCategoryAll", allEntries = true)
    })
    public void deactivateCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        
        category.setActive(false);
        categoryRepository.save(category);
    }

    /**
     * Activate category
     */
    @Caching(evict = {
            @CacheEvict(cacheNames = "navCategoryBySlug", allEntries = true),
            @CacheEvict(cacheNames = "navCategoryRoots", allEntries = true),
            @CacheEvict(cacheNames = "navCategoryChildren", allEntries = true),
            @CacheEvict(cacheNames = "navCategoryAll", allEntries = true)
    })
    public void activateCategory(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Category not found"));
        
        category.setActive(true);
        categoryRepository.save(category);
    }

    /**
     * Check if slug exists
     */
    @Transactional(readOnly = true)
    public boolean slugExists(String slug) {
        return categoryRepository.existsBySlug(slug);
    }

    /**
     * Check if slug exists for different category (for updates)
     */
    @Transactional(readOnly = true)
    public boolean slugExistsForOther(String slug, Long categoryId) {
        return categoryRepository.existsBySlugAndIdNot(slug, categoryId);
    }

    /**
     * Generate URL-friendly slug from name
     */
    private String generateSlug(String name) {
        String baseSlug = name.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
        
        String slug = baseSlug;
        int counter = 1;
        
        while (slugExists(slug)) {
            slug = baseSlug + "-" + counter;
            counter++;
        }
        
        return slug;
    }

    /**
     * Get category hierarchy (breadcrumb)
     */
    @Transactional(readOnly = true)
    public List<Category> getCategoryHierarchy(Category category) {
        List<Category> hierarchy = new java.util.ArrayList<>();
        Category current = category;
        
        while (current != null) {
            hierarchy.add(0, current);
            current = current.getParent();
        }
        
        return hierarchy;
    }

    /**
     * Returns category ids in hierarchy from current node to root.
     */
    @Transactional(readOnly = true)
    public List<Long> findHierarchyBySlug(String slug) {
        return categoryRepository.findHierarchy(slug);
    }

    /**
     * Get all categories (for admin)
     */
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "navCategoryAll", key = "'all'", unless = "#result == null || #result.isEmpty()")
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    /**
     * Loads the full active subtree into {@link Category#getChildren()} for storefront navigation
     * (header/offcanvas). Uses explicit queries so depth is not limited by Hibernate batch size.
     */
    private static final int MAX_NAV_CATEGORY_DEPTH = 48;

    private void initializeNavigationBranch(Category node) {
        initializeNavigationBranch(node, 0);
    }

    private void initializeNavigationBranch(Category node, int depth) {
        if (depth > MAX_NAV_CATEGORY_DEPTH) {
            return;
        }
        List<Category> children = categoryRepository.findByParentIdAndActiveTrueOrderBySortOrderAsc(node.getId());
        node.setChildren(children);
        int next = depth + 1;
        children.forEach(child -> initializeNavigationBranch(child, next));
    }
}
