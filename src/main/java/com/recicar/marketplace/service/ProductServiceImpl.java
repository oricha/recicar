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
import java.util.ArrayList;
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
        return productRepository.findByPartNumberContaining(product.getPartNumber(), Pageable.unpaged()).stream()
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
    public Page<Product> findByPartNumberContaining(String partNumber, Pageable pageable) {
        return productRepository.findByPartNumberContaining(partNumber, pageable);
    }

    @Override
    public Page<Product> findByOemNumberContaining(String oemNumber, Pageable pageable) {
        return productRepository.findByOemNumberContaining(oemNumber, pageable);
    }

    @Override
    public Page<Product> findByProductName(String productName, Pageable pageable) {
        return productRepository.findByProductName(productName, pageable);
    }

    @Override
    public Page<Product> findByMakeModelEngineAndPartName(String make, String model, String engineType, String partName, Pageable pageable) {
        String part = (partName != null && !partName.isBlank()) ? partName.trim() : null;
        return productRepository.findByMakeModelEngineAndPartName(make, model, engineType, part, pageable);
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

    @Override
    public List<Product> findByPartNumber(String partNumber) {
        return productRepository.findByPartNumberIgnoreCase(partNumber);
    }

    @Override
    public List<Product> findByOemNumber(String oemNumber) {
        return productRepository.findByOemNumberIgnoreCase(oemNumber);
    }

    @Override
    public List<Product> findByIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return productRepository.findAllById(ids).stream().collect(Collectors.toList());
    }

    @Override
    public List<Product> findEnginePartsForHomePage() {
        // First try to find products with "driver" in name or description
        List<Product> driverResults = productRepository.searchByNameOrPartNumber("driver", Pageable.unpaged()).getContent();

        // If we have enough results, return the first 9
        if (driverResults.size() >= 9) {
            return driverResults.subList(0, 9);
        }

        // If not enough, search for "engine" related products
        List<Product> engineResults = productRepository.searchByNameOrPartNumber("engine", Pageable.unpaged()).getContent();

        // Combine results and remove duplicates
        List<Product> combinedResults = driverResults.stream()
            .collect(Collectors.toList());

        for (Product engineProduct : engineResults) {
            if (combinedResults.size() >= 9) break;
            if (!combinedResults.contains(engineProduct)) {
                combinedResults.add(engineProduct);
            }
        }

        // If still not enough, get some random active products to fill up to 9
        if (combinedResults.size() < 9) {
            List<Product> allActiveProducts = productRepository.findByActiveTrue(Pageable.unpaged()).getContent();
            for (Product product : allActiveProducts) {
                if (combinedResults.size() >= 9) break;
                if (!combinedResults.contains(product)) {
                    combinedResults.add(product);
                }
            }
        }

        return combinedResults.subList(0, Math.min(combinedResults.size(), 9));
    }

    @Override
    public List<Product> findBodyPartsForHomePage() {
        // First try to find products with "controller" in name or description
        List<Product> controllerResults = productRepository.searchByNameOrPartNumber("controller", Pageable.unpaged()).getContent();

        // If we have enough results, return the first 9
        if (controllerResults.size() >= 9) {
            return controllerResults.subList(0, 9);
        }

        // If not enough, search for "body" related products
        List<Product> bodyResults = productRepository.searchByNameOrPartNumber("body", Pageable.unpaged()).getContent();

        // Combine results and remove duplicates
        List<Product> combinedResults = controllerResults.stream()
            .collect(Collectors.toList());

        for (Product bodyProduct : bodyResults) {
            if (combinedResults.size() >= 9) break;
            if (!combinedResults.contains(bodyProduct)) {
                combinedResults.add(bodyProduct);
            }
        }

        // If still not enough, get some random active products to fill up to 9
        if (combinedResults.size() < 9) {
            List<Product> allActiveProducts = productRepository.findByActiveTrue(Pageable.unpaged()).getContent();
            for (Product product : allActiveProducts) {
                if (combinedResults.size() >= 9) break;
                if (!combinedResults.contains(product)) {
                    combinedResults.add(product);
                }
            }
        }

        return combinedResults.subList(0, Math.min(combinedResults.size(), 9));
    }

    @Override
    public List<Product> findRelatedProducts(Long productId) {
        Product currentProduct = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found"));

        List<Product> relatedProducts = new ArrayList<>();

        // First, try to find products from the same category
        List<Product> categoryProducts = productRepository.findByCategoryAndActiveTrue(currentProduct.getCategory(), Pageable.unpaged()).getContent();
        for (Product product : categoryProducts) {
            if (!product.getId().equals(productId) && relatedProducts.size() < 8) {
                relatedProducts.add(product);
            }
        }

        // If not enough products from same category, add products from same vendor
        if (relatedProducts.size() < 8) {
            List<Product> vendorProducts = productRepository.findByVendorAndActiveTrue(currentProduct.getVendor(), Pageable.unpaged()).getContent();
            for (Product product : vendorProducts) {
                if (!product.getId().equals(productId) && !relatedProducts.contains(product) && relatedProducts.size() < 8) {
                    relatedProducts.add(product);
                }
            }
        }

        // If still not enough, add random active products
        if (relatedProducts.size() < 8) {
            List<Product> allActiveProducts = productRepository.findByActiveTrue(Pageable.unpaged()).getContent();
            for (Product product : allActiveProducts) {
                if (!product.getId().equals(productId) && !relatedProducts.contains(product) && relatedProducts.size() < 8) {
                    relatedProducts.add(product);
                }
            }
        }

        return relatedProducts;
    }
}
