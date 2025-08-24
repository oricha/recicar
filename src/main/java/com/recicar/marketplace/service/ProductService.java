package com.recicar.marketplace.service;

import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.entity.ProductCondition;
import com.recicar.marketplace.entity.Category;
import com.recicar.marketplace.entity.Vendor;
import com.recicar.marketplace.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Get all active products with pagination
     */
    @Transactional(readOnly = true)
    public Page<Product> findActiveProducts(Pageable pageable) {
        return productRepository.findByActiveTrue(pageable);
    }

    /**
     * Get all active products with default pagination (12 per page, sorted by creation date)
     */
    @Transactional(readOnly = true)
    public Page<Product> findActiveProducts(int page) {
        Pageable pageable = PageRequest.of(page, 12, Sort.by(Sort.Direction.DESC, "createdAt"));
        return productRepository.findByActiveTrue(pageable);
    }

    /**
     * Find product by ID
     */
    @Transactional(readOnly = true)
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    /**
     * Find active product by ID
     */
    @Transactional(readOnly = true)
    public Optional<Product> findActiveById(Long id) {
        return productRepository.findById(id)
                .filter(Product::isActive);
    }

    /**
     * Search products by name or part number
     */
    @Transactional(readOnly = true)
    public Page<Product> searchProducts(String searchTerm, Pageable pageable) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return findActiveProducts(pageable);
        }
        return productRepository.searchByNameOrPartNumber(searchTerm.trim(), pageable);
    }

    /**
     * Search products with default pagination
     */
    @Transactional(readOnly = true)
    public Page<Product> searchProducts(String searchTerm, int page) {
        Pageable pageable = PageRequest.of(page, 12, Sort.by(Sort.Direction.DESC, "createdAt"));
        return searchProducts(searchTerm, pageable);
    }

    /**
     * Find products by category
     */
    @Transactional(readOnly = true)
    public Page<Product> findByCategory(Category category, Pageable pageable) {
        return productRepository.findByCategoryAndActiveTrue(category, pageable);
    }

    /**
     * Find products by category with default pagination
     */
    @Transactional(readOnly = true)
    public Page<Product> findByCategory(Category category, int page) {
        Pageable pageable = PageRequest.of(page, 12, Sort.by(Sort.Direction.DESC, "createdAt"));
        return findByCategory(category, pageable);
    }

    /**
     * Find products by vendor
     */
    @Transactional(readOnly = true)
    public Page<Product> findByVendor(Vendor vendor, Pageable pageable) {
        return productRepository.findByVendorAndActiveTrue(vendor, pageable);
    }

    /**
     * Find products by condition
     */
    @Transactional(readOnly = true)
    public Page<Product> findByCondition(ProductCondition condition, Pageable pageable) {
        return productRepository.findByConditionAndActiveTrue(condition, pageable);
    }

    /**
     * Find products by price range
     */
    @Transactional(readOnly = true)
    public Page<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return productRepository.findByPriceRange(minPrice, maxPrice, pageable);
    }

    /**
     * Advanced search with multiple filters
     */
    @Transactional(readOnly = true)
    public Page<Product> findWithFilters(String searchTerm, Category category, ProductCondition condition,
                                        BigDecimal minPrice, BigDecimal maxPrice, Vendor vendor,
                                        Pageable pageable) {
        return productRepository.findWithFilters(searchTerm, category, condition, minPrice, maxPrice, vendor, pageable);
    }

    /**
     * Advanced search with comprehensive filtering including stock and availability
     */
    @Transactional(readOnly = true)
    public Page<Product> findWithAdvancedFilters(String searchTerm, Category category, ProductCondition condition,
                                                BigDecimal minPrice, BigDecimal maxPrice, Boolean inStock,
                                                Boolean lowStock, Vendor vendor, int page, String sortBy, String sortDir) {
        
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        String sortField = getSortField(sortBy);
        
        Pageable pageable = PageRequest.of(page, 12, Sort.by(direction, sortField));
        
        // Build dynamic query based on filters
        if (searchTerm == null && category == null && condition == null && 
            minPrice == null && maxPrice == null && inStock == null && 
            lowStock == null && vendor == null) {
            // No filters, return all active products
            return findActiveProducts(pageable);
        }
        
        // Use the existing findWithFilters method for basic filters
        Page<Product> products = findWithFilters(searchTerm, category, condition, minPrice, maxPrice, vendor, pageable);
        
        // Apply additional stock filters if needed
        if (inStock != null || lowStock != null) {
            List<Product> filteredProducts = products.getContent().stream()
                .filter(product -> {
                    if (inStock != null && inStock && !product.isInStock()) {
                        return false;
                    }
                    if (lowStock != null && lowStock && !product.isLowStock()) {
                        return false;
                    }
                    return true;
                })
                .toList();
            
            // Create a new page with filtered content
            return new PageImpl<>(filteredProducts, pageable, filteredProducts.size());
        }
        
        return products;
    }

    /**
     * Advanced search with filters and default pagination
     */
    @Transactional(readOnly = true)
    public Page<Product> findWithFilters(String searchTerm, Category category, ProductCondition condition,
                                        BigDecimal minPrice, BigDecimal maxPrice, Vendor vendor,
                                        int page, String sortBy, String sortDir) {
        
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        String sortField = getSortField(sortBy);
        
        Pageable pageable = PageRequest.of(page, 12, Sort.by(direction, sortField));
        return findWithFilters(searchTerm, category, condition, minPrice, maxPrice, vendor, pageable);
    }

    /**
     * Find products compatible with specific vehicle
     */
    @Transactional(readOnly = true)
    public Page<Product> findByVehicleCompatibility(String make, String model, Integer year, Pageable pageable) {
        return productRepository.findByVehicleCompatibility(make, model, year, pageable);
    }

    /**
     * Find products by part number (exact match)
     */
    @Transactional(readOnly = true)
    public List<Product> findByPartNumber(String partNumber) {
        return productRepository.findByPartNumberIgnoreCase(partNumber);
    }

    /**
     * Find products by OEM number (exact match)
     */
    @Transactional(readOnly = true)
    public List<Product> findByOemNumber(String oemNumber) {
        return productRepository.findByOemNumberIgnoreCase(oemNumber);
    }

    /**
     * Get low stock products
     */
    @Transactional(readOnly = true)
    public List<Product> findLowStockProducts() {
        return productRepository.findLowStockProducts();
    }

    /**
     * Get out of stock products
     */
    @Transactional(readOnly = true)
    public List<Product> findOutOfStockProducts() {
        return productRepository.findOutOfStockProducts();
    }

    /**
     * Get low stock products for a specific vendor
     */
    @Transactional(readOnly = true)
    public List<Product> findLowStockProductsByVendor(Vendor vendor) {
        return productRepository.findLowStockProductsByVendor(vendor);
    }

    /**
     * Save or update product
     */
    public Product save(Product product) {
        return productRepository.save(product);
    }

    /**
     * Delete product (soft delete by setting active = false)
     */
    public void deactivateProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        product.setActive(false);
        productRepository.save(product);
    }

    /**
     * Activate product
     */
    public void activateProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        product.setActive(true);
        productRepository.save(product);
    }

    /**
     * Update product stock quantity
     */
    public void updateStock(Long productId, Integer newQuantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        product.setStockQuantity(newQuantity);
        productRepository.save(product);
    }

    /**
     * Decrease stock quantity (for orders)
     */
    public void decreaseStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        
        if (product.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient stock");
        }
        
        product.setStockQuantity(product.getStockQuantity() - quantity);
        productRepository.save(product);
    }

    /**
     * Increase stock quantity (for returns/restocking)
     */
    public void increaseStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        
        product.setStockQuantity(product.getStockQuantity() + quantity);
        productRepository.save(product);
    }

    /**
     * Count products by vendor
     */
    @Transactional(readOnly = true)
    public long countByVendor(Vendor vendor) {
        return productRepository.countByVendor(vendor);
    }

    /**
     * Count active products by vendor
     */
    @Transactional(readOnly = true)
    public long countActiveByVendor(Vendor vendor) {
        return productRepository.countByVendorAndActiveTrue(vendor);
    }

    /**
     * Get valid sort field for queries
     */
    private String getSortField(String sortBy) {
        return switch (sortBy) {
            case "name" -> "name";
            case "price" -> "price";
            case "created" -> "createdAt";
            case "updated" -> "updatedAt";
            default -> "createdAt";
        };
    }

    /**
     * Check if product is available for purchase
     */
    @Transactional(readOnly = true)
    public boolean isAvailable(Long productId) {
        return productRepository.findById(productId)
                .map(Product::isAvailable)
                .orElse(false);
    }

    /**
     * Check stock availability for quantity
     */
    @Transactional(readOnly = true)
    public boolean hasStock(Long productId, Integer requiredQuantity) {
        return productRepository.findById(productId)
                .map(product -> product.getStockQuantity() >= requiredQuantity)
                .orElse(false);
    }
}