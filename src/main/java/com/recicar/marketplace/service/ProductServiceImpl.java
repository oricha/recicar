package com.recicar.marketplace.service;

import com.recicar.marketplace.dto.ProductRequest;
import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.entity.ProductCondition;
import com.recicar.marketplace.entity.Category;
import com.recicar.marketplace.entity.Vendor;
import com.recicar.marketplace.repository.ProductRepository;
import com.recicar.marketplace.repository.CategoryRepository;
import com.recicar.marketplace.repository.VendorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final VendorRepository vendorRepository;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository, VendorRepository vendorRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.vendorRepository = vendorRepository;
    }

    @Override
    public Product createOrUpdateProduct(ProductRequest request) {
        Product product;
        if (request.getId() != null) {
            product = productRepository.findById(request.getId())
                    .orElseThrow(() -> new RuntimeException("Product not found"));
        } else {
            product = new Product();
            product.setCreatedAt(LocalDateTime.now());
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));
        Vendor vendor = vendorRepository.findById(request.getVendorId())
                .orElseThrow(() -> new RuntimeException("Vendor not found"));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setPartNumber(request.getPartNumber());
        product.setOemNumber(request.getOemNumber());
        product.setCondition(request.getCondition());
        product.setStockQuantity(request.getStockQuantity());
        product.setWeightKg(request.getWeightKg());
        product.setActive(request.isActive());
        product.setCategory(category);
        product.setVendor(vendor);
        product.setUpdatedAt(LocalDateTime.now());

        // TODO: Handle images and vehicle compatibilities

        return productRepository.save(product);
    }

    @Override
    public void decreaseStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        
        if (product.getStockQuantity() < quantity) {
            throw new IllegalArgumentException("Insufficient stock");
        }
        
        product.setStockQuantity(product.getStockQuantity() - quantity);

        if (product.getStockQuantity() == 0) {
            product.setActive(false);
        }

        productRepository.save(product);
    }

    @Override
    public void increaseStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        
        product.setStockQuantity(product.getStockQuantity() + quantity);
        productRepository.save(product);
    }

    @Override
    public void bulkUpdateStock(List<Long> productIds, List<Integer> quantities) {
        if (productIds.size() != quantities.size()) {
            throw new IllegalArgumentException("Product IDs and quantities lists must be of the same size.");
        }

        for (int i = 0; i < productIds.size(); i++) {
            Long productId = productIds.get(i);
            Integer quantity = quantities.get(i);
            updateStock(productId, quantity);
        }
    }

    @Override
    public List<Product> getInventoryReport() {
        return productRepository.findAll();
    }

    @Override
    public List<Product> getInventoryReportByVendor(Vendor vendor) {
        return productRepository.findByVendorAndActiveTrue(vendor, Pageable.unpaged()).getContent();
    }

    @Override
    public List<Vendor> findOtherVendorsSellingProduct(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        return productRepository.findByPartNumberIgnoreCase(product.getPartNumber()).stream()
                .filter(p -> !p.getVendor().getId().equals(product.getVendor().getId()))
                .map(Product::getVendor)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public Page<Product> findActiveProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        return productRepository.findByActiveTrue(pageable);
    }

    @Override
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    @Override
    public Optional<Product> findActiveById(Long id) {
        return productRepository.findById(id)
                .filter(Product::isActive);
    }

    @Override
    public Page<Product> searchProducts(String searchTerm, Pageable pageable) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return productRepository.findByActiveTrue(pageable);
        }
        return productRepository.searchByNameOrPartNumber(searchTerm.trim(), pageable);
    }

    @Override
    public Page<Product> searchProducts(String searchTerm, int page) {
        Pageable pageable = PageRequest.of(page, 12, Sort.by(Sort.Direction.DESC, "createdAt"));
        return searchProducts(searchTerm, pageable);
    }

    @Override
    public Page<Product> findByCategory(Category category, Pageable pageable) {
        return productRepository.findByCategoryAndActiveTrue(category, pageable);
    }

    @Override
    public Page<Product> findByCategory(Category category, int page) {
        Pageable pageable = PageRequest.of(page, 12, Sort.by(Sort.Direction.DESC, "createdAt"));
        return findByCategory(category, pageable);
    }

    @Override
    public Page<Product> findByVendor(Vendor vendor, Pageable pageable) {
        return productRepository.findByVendorAndActiveTrue(vendor, pageable);
    }

    @Override
    public Page<Product> findByCondition(ProductCondition condition, Pageable pageable) {
        return productRepository.findByConditionAndActiveTrue(condition, pageable);
    }

    @Override
    public Page<Product> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable) {
        return productRepository.findByPriceRange(minPrice, maxPrice, pageable);
    }

    @Override
    public Page<Product> findWithFilters(String searchTerm, Category category, ProductCondition condition,
                                        BigDecimal minPrice, BigDecimal maxPrice, Vendor vendor,
                                        Pageable pageable) {
        return productRepository.findWithFilters(searchTerm, category, condition, minPrice, maxPrice, vendor, pageable);
    }

    @Override
    public Page<Product> findWithAdvancedFilters(String searchTerm, Category category, ProductCondition condition,
                                                BigDecimal minPrice, BigDecimal maxPrice, Boolean inStock,
                                                Boolean lowStock, Vendor vendor, int page, String sortBy, String sortDir) {
        
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        String sortField = getSortField(sortBy);
        
        Pageable pageable = PageRequest.of(page, 12, Sort.by(direction, sortField));
        
        // Build dynamic query based on filters
        if (searchTerm == null && category == null && condition == null && 
            minPrice == null && maxPrice == null && inStock != null && 
            lowStock == null && vendor == null) {
            // No filters, return all active products
            return productRepository.findByActiveTrue(pageable);
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

    @Override
    public Page<Product> findWithFilters(String searchTerm, Category category, ProductCondition condition,
                                        BigDecimal minPrice, BigDecimal maxPrice, Vendor vendor,
                                        int page, String sortBy, String sortDir) {
        
        Sort.Direction direction = "desc".equalsIgnoreCase(sortDir) ? Sort.Direction.DESC : Sort.Direction.ASC;
        String sortField = getSortField(sortBy);
        
        Pageable pageable = PageRequest.of(page, 12, Sort.by(direction, sortField));
        return productRepository.findWithFilters(searchTerm, category, condition, minPrice, maxPrice, vendor, pageable);
    }

    @Override
    public Page<Product> findByVehicleCompatibility(String make, String model, String engine, Integer year, Pageable pageable) {
        return productRepository.findByVehicleCompatibility(make, model, engine, year, pageable);
    }

    @Override
    public Page<Product> findByVehicleCompatibility(String make, String model, String engine, Integer year, int page) {
        Pageable pageable = PageRequest.of(page, 12, Sort.by(Sort.Direction.DESC, "createdAt"));
        return findByVehicleCompatibility(make, model, engine, year, pageable);
    }

    @Override
    public List<Product> findByPartNumber(String partNumber) {
        return productRepository.findByPartNumberIgnoreCase(partNumber);
    }

    @Override
    public List<Product> findByOemNumber(String oemNumber) {
        return productRepository.findByOemNumberIgnoreCase(oemNumber);
    }

    @Override
    public void updateStock(Long productId, Integer quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));
        product.setStockQuantity(quantity);
        productRepository.save(product);
    }

    private String getSortField(String sortBy) {
        return switch (sortBy) {
            case "name" -> "name";
            case "price" -> "price";
            case "created" -> "createdAt";
            case "updated" -> "updatedAt";
            default -> "createdAt";
        };
    }

    @Override
    public List<Product> findLowStockProducts() {
        return productRepository.findLowStockProducts();
    }

    @Override
    public List<Product> findLowStockProductsByVendor(Vendor vendor) {
        return productRepository.findLowStockProductsByVendor(vendor);
    }

    @Override
    public long countActiveByVendor(Vendor vendor) {
        return productRepository.countByVendorAndActiveTrue(vendor);
    }
}
