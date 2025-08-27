package com.recicar.marketplace.controller;

import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.entity.ProductCondition;
import com.recicar.marketplace.entity.Category;
import com.recicar.marketplace.entity.Vendor;
import com.recicar.marketplace.service.ProductService;
import com.recicar.marketplace.service.CategoryService;
import com.recicar.marketplace.service.VendorService;
import com.recicar.marketplace.client.VehicleApiClient;
import com.recicar.marketplace.dto.VehicleInfo;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final VendorService vendorService;
    private final VehicleApiClient vehicleApiClient;

    public ProductController(ProductService productService, CategoryService categoryService, VendorService vendorService, VehicleApiClient vehicleApiClient) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.vendorService = vendorService;
        this.vehicleApiClient = vehicleApiClient;
    }

    /**
     * Display product catalog with pagination
     */
    @GetMapping
    public String showProductCatalog(@RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "created") String sortBy,
                                   @RequestParam(defaultValue = "desc") String sortDir,
                                   @RequestParam(required = false) String search,
                                   @RequestParam(required = false) Long categoryId,
                                   @RequestParam(required = false) ProductCondition condition,
                                   @RequestParam(required = false) BigDecimal minPrice,
                                   @RequestParam(required = false) BigDecimal maxPrice,
                                   @RequestParam(required = false) Long vendorId,
                                   Model model) {

        // Get category if specified
        Category category = null;
        if (categoryId != null) {
            category = categoryService.findById(categoryId).orElse(null);
        }

        // Get vendor if specified
        Vendor vendor = null;
        if (vendorId != null) {
            vendor = vendorService.findById(vendorId).orElse(null);
        }

        // Search products with filters
        Page<Product> products = productService.findWithFilters(
            search, category, condition, minPrice, maxPrice, vendor, page, sortBy, sortDir
        );

        // Add attributes to model
        model.addAttribute("products", products);
        model.addAttribute("categories", categoryService.findAllActive());
        model.addAttribute("conditions", ProductCondition.values());
        model.addAttribute("vendors", vendorService.findAllApproved());
        
        // Current filter values
        model.addAttribute("currentSearch", search);
        model.addAttribute("currentCategoryId", categoryId);
        model.addAttribute("currentCondition", condition);
        model.addAttribute("currentMinPrice", minPrice);
        model.addAttribute("currentMaxPrice", maxPrice);
        model.addAttribute("currentVendorId", vendorId);
        model.addAttribute("currentSortBy", sortBy);
        model.addAttribute("currentSortDir", sortDir);
        
        // Pagination info
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", products.getTotalPages());
        model.addAttribute("totalElements", products.getTotalElements());
        
        return "products/catalog";
    }

    /**
     * Display product detail page
     */
    @GetMapping("/{id}")
    public String showProductDetail(@PathVariable Long id, Model model) {
        Optional<Product> productOpt = productService.findActiveById(id);
        
        if (productOpt.isEmpty()) {
            return "error/404";
        }
        
        Product product = productOpt.get();
        model.addAttribute("product", product);
        
        // Get related products from same category
        Page<Product> relatedProducts = productService.findByCategory(product.getCategory(), 0);
        model.addAttribute("relatedProducts", relatedProducts.getContent().stream()
                .filter(p -> !p.getId().equals(id))
                .limit(4)
                .toList());

        // Get other vendors selling this product
        model.addAttribute("otherVendors", productService.findOtherVendorsSellingProduct(id));
        
        return "products/detail";
    }

    /**
     * Display products by category
     */
    @GetMapping("/category/{categoryId}")
    public String showProductsByCategory(@PathVariable Long categoryId,
                                       @RequestParam(defaultValue = "0") int page,
                                       @RequestParam(defaultValue = "created") String sortBy,
                                       @RequestParam(defaultValue = "desc") String sortDir,
                                       Model model) {
        
        Optional<Category> categoryOpt = categoryService.findById(categoryId);
        if (categoryOpt.isEmpty()) {
            return "error/404";
        }
        
        Category category = categoryOpt.get();
        Page<Product> products = productService.findByCategory(category, page);
        
        model.addAttribute("products", products);
        model.addAttribute("category", category);
        model.addAttribute("categories", categoryService.findAllActive());
        model.addAttribute("conditions", ProductCondition.values());
        
        // Pagination info
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", products.getTotalPages());
        model.addAttribute("totalElements", products.getTotalElements());
        model.addAttribute("currentSortBy", sortBy);
        model.addAttribute("currentSortDir", sortDir);
        
        return "products/category";
    }

    /**
     * Search products
     */
    @GetMapping("/search")
    public String searchProducts(@RequestParam(required = false) String q,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "created") String sortBy,
                               @RequestParam(defaultValue = "desc") String sortDir,
                               Model model) {
        
        // Validate search query
        if (q == null || q.trim().isEmpty()) {
            return "redirect:/products";
        }
        
        // Sanitize and validate search input
        String sanitizedQuery = q.trim();
        if (sanitizedQuery.length() < 2) {
            model.addAttribute("errorMessage", "Search term must be at least 2 characters long");
            model.addAttribute("searchQuery", q);
            model.addAttribute("categories", categoryService.findAllActive());
            model.addAttribute("conditions", ProductCondition.values());
            model.addAttribute("products", org.springframework.data.domain.Page.empty());
            model.addAttribute("totalElements", 0L);
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", 0);
            model.addAttribute("currentSortBy", sortBy);
            model.addAttribute("currentSortDir", sortDir);
            return "products/search-results";
        }
        
        // Limit search query length
        if (sanitizedQuery.length() > 100) {
            sanitizedQuery = sanitizedQuery.substring(0, 100);
        }
        
        try {
            Page<Product> products = productService.searchProducts(sanitizedQuery, page);
            
            model.addAttribute("products", products);
            model.addAttribute("searchQuery", sanitizedQuery);
            model.addAttribute("categories", categoryService.findAllActive());
            model.addAttribute("conditions", ProductCondition.values());
            
            // Pagination info
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", products.getTotalPages());
            model.addAttribute("totalElements", products.getTotalElements());
            model.addAttribute("currentSortBy", sortBy);
            model.addAttribute("currentSortDir", sortDir);
            
            return "products/search-results";
        } catch (Exception e) {
            // Log the error for debugging
            // logger.error("Error during product search: {}", e.getMessage(), e);
            
            model.addAttribute("errorMessage", "An error occurred while searching. Please try again.");
            model.addAttribute("searchQuery", sanitizedQuery);
            model.addAttribute("categories", categoryService.findAllActive());
            model.addAttribute("conditions", ProductCondition.values());
            
            // Return empty results
            model.addAttribute("products", Page.empty());
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("totalElements", 0);
            model.addAttribute("currentSortBy", sortBy);
            model.addAttribute("currentSortDir", sortDir);
            
            return "products/search-results";
        }
    }

    /**
     * Find products by part number
     */
    @GetMapping("/part/{partNumber}")
    public String findByPartNumber(@PathVariable String partNumber, Model model) {
        // Validate part number
        if (partNumber == null || partNumber.trim().isEmpty()) {
            return "redirect:/products";
        }
        
        String sanitizedPartNumber = partNumber.trim();
        if (sanitizedPartNumber.length() < 2) {
            model.addAttribute("errorMessage", "Part number must be at least 2 characters long");
            model.addAttribute("partNumber", partNumber);
            model.addAttribute("searchType", "Part Number");
            model.addAttribute("products", List.of());
            return "products/part-search";
        }
        
        try {
            var products = productService.findByPartNumber(sanitizedPartNumber);
            
            model.addAttribute("products", products);
            model.addAttribute("partNumber", sanitizedPartNumber);
            model.addAttribute("searchType", "Part Number");
            
            return "products/part-search";
        } catch (Exception e) {
            // Log the error for debugging
            // logger.error("Error during part number search: {}", e.getMessage(), e);
            
            model.addAttribute("errorMessage", "An error occurred while searching. Please try again.");
            model.addAttribute("partNumber", sanitizedPartNumber);
            model.addAttribute("searchType", "Part Number");
            model.addAttribute("products", List.of());
            
            return "products/part-search";
        }
    }

    /**
     * Find products by OEM number
     */
    @GetMapping("/oem/{oemNumber}")
    public String findByOemNumber(@PathVariable String oemNumber, Model model) {
        // Validate OEM number
        if (oemNumber == null || oemNumber.trim().isEmpty()) {
            return "redirect:/products";
        }
        
        String sanitizedOemNumber = oemNumber.trim();
        if (sanitizedOemNumber.length() < 2) {
            model.addAttribute("errorMessage", "OEM number must be at least 2 characters long");
            model.addAttribute("oemNumber", oemNumber);
            model.addAttribute("searchType", "OEM Number");
            model.addAttribute("products", List.of());
            return "products/part-search";
        }
        
        try {
            var products = productService.findByOemNumber(sanitizedOemNumber);
            
            model.addAttribute("products", products);
            model.addAttribute("oemNumber", sanitizedOemNumber);
            model.addAttribute("searchType", "OEM Number");
            
            return "products/part-search";
        } catch (Exception e) {
            // Log the error for debugging
            // logger.error("Error during OEM number search: {}", e.getMessage(), e);
            
            model.addAttribute("errorMessage", "An error occurred while searching. Please try again.");
            model.addAttribute("oemNumber", sanitizedOemNumber);
            model.addAttribute("searchType", "OEM Number");
            model.addAttribute("products", List.of());
            
            return "products/part-search";
        }
    }

    /**
     * Find products by vehicle compatibility
     */
    @GetMapping("/vehicle")
    public String findByVehicle(@RequestParam(required = false) String licensePlate,
                               @RequestParam(required = false) String make,
                               @RequestParam(required = false) String model,
                               @RequestParam(required = false) Integer year,
                               @RequestParam(defaultValue = "0") int page,
                               Model modelAttr) {

        // If license plate is provided, try to lookup vehicle info
        if (licensePlate != null && !licensePlate.trim().isEmpty()) {
            VehicleInfo vehicleInfo = vehicleApiClient.lookupLicensePlate(licensePlate.trim());
            if (vehicleInfo != null) {
                make = vehicleInfo.getMake();
                model = vehicleInfo.getModel();
                year = vehicleInfo.getYear();
                modelAttr.addAttribute("licensePlate", licensePlate);
            } else {
                modelAttr.addAttribute("errorMessage", "Could not find vehicle information for the provided license plate.");
                modelAttr.addAttribute("licensePlate", licensePlate);
                modelAttr.addAttribute("products", Page.empty());
                modelAttr.addAttribute("currentPage", 0);
                modelAttr.addAttribute("totalPages", 0);
                modelAttr.addAttribute("totalElements", 0);
                return "products/vehicle-compatibility";
            }
        }
        
        // Validate vehicle parameters
        if (make == null || make.trim().isEmpty() || 
            model == null || model.trim().isEmpty() || 
            year == null) {
            modelAttr.addAttribute("errorMessage", "Vehicle make, model, and year are required");
            modelAttr.addAttribute("products", Page.empty());
            modelAttr.addAttribute("currentPage", 0);
            modelAttr.addAttribute("totalPages", 0);
            modelAttr.addAttribute("totalElements", 0);
            return "products/vehicle-compatibility";
        }
        
        // Sanitize and validate inputs
        String sanitizedMake = make.trim();
        String sanitizedModel = model.trim();
        
        if (sanitizedMake.length() < 2 || sanitizedModel.length() < 2) {
            modelAttr.addAttribute("errorMessage", "Vehicle make and model must be at least 2 characters long");
            modelAttr.addAttribute("products", Page.empty());
            modelAttr.addAttribute("currentPage", 0);
            modelAttr.addAttribute("totalPages", 0);
            modelAttr.addAttribute("totalElements", 0);
            return "products/vehicle-compatibility";
        }
        
        if (year < 1900 || year > 2030) {
            modelAttr.addAttribute("errorMessage", "Vehicle year must be between 1900 and 2030");
            modelAttr.addAttribute("products", Page.empty());
            modelAttr.addAttribute("currentPage", 0);
            modelAttr.addAttribute("totalPages", 0);
            modelAttr.addAttribute("totalElements", 0);
            return "products/vehicle-compatibility";
        }
        
        try {
            Page<Product> products = productService.findByVehicleCompatibility(sanitizedMake, sanitizedModel, year, 
                    org.springframework.data.domain.PageRequest.of(page, 12));
            
            modelAttr.addAttribute("products", products);
            modelAttr.addAttribute("vehicleMake", sanitizedMake);
            modelAttr.addAttribute("vehicleModel", sanitizedModel);
            modelAttr.addAttribute("vehicleYear", year);
            
            // Pagination info
            modelAttr.addAttribute("currentPage", page);
            modelAttr.addAttribute("totalPages", products.getTotalPages());
            modelAttr.addAttribute("totalElements", products.getTotalElements());
            
            return "products/vehicle-compatibility";
        } catch (Exception e) {
            // Log the error for debugging
            // logger.error("Error during vehicle compatibility search: {}", e.getMessage(), e);
            
            modelAttr.addAttribute("errorMessage", "An error occurred while searching. Please try again.");
            modelAttr.addAttribute("vehicleMake", sanitizedMake);
            modelAttr.addAttribute("vehicleModel", sanitizedModel);
            modelAttr.addAttribute("vehicleYear", year);
            modelAttr.addAttribute("products", Page.empty());
            modelAttr.addAttribute("currentPage", 0);
            modelAttr.addAttribute("totalPages", 0);
            modelAttr.addAttribute("totalElements", 0);
            
            return "products/vehicle-compatibility";
        }
    }

    /**
     * Advanced search with comprehensive filtering
     */
    @GetMapping("/advanced-search")
    public String advancedSearch(@RequestParam(required = false) String q,
                                @RequestParam(required = false) Long categoryId,
                                @RequestParam(required = false) ProductCondition condition,
                                @RequestParam(required = false) BigDecimal minPrice,
                                @RequestParam(required = false) BigDecimal maxPrice,
                                @RequestParam(required = false) Boolean inStock,
                                @RequestParam(required = false) Boolean lowStock,
                                @RequestParam(required = false) Long vendorId,
                                @RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "created") String sortBy,
                                @RequestParam(defaultValue = "desc") String sortDir,
                                Model model) {
        
        try {
            // Get category if specified
            Category category = null;
            if (categoryId != null) {
                category = categoryService.findById(categoryId).orElse(null);
            }

            // Get vendor if specified
            Vendor vendor = null;
            if (vendorId != null) {
                vendor = vendorService.findById(vendorId).orElse(null);
            }

            // Search products with advanced filters
            Page<Product> products = productService.findWithAdvancedFilters(
                q, category, condition, minPrice, maxPrice, inStock, lowStock, vendor, page, sortBy, sortDir
            );

            // Add attributes to model
            model.addAttribute("products", products);
            model.addAttribute("categories", categoryService.findAllActive());
            model.addAttribute("conditions", ProductCondition.values());
            model.addAttribute("vendors", vendorService.findAllApproved());
            
            // Current filter values
            model.addAttribute("currentSearch", q);
            model.addAttribute("currentCategoryId", categoryId);
            model.addAttribute("currentCategoryName", category != null ? category.getName() : null);
            model.addAttribute("currentCondition", condition);
            model.addAttribute("currentMinPrice", minPrice);
            model.addAttribute("currentMaxPrice", maxPrice);
            model.addAttribute("currentInStock", inStock);
            model.addAttribute("currentLowStock", lowStock);
            model.addAttribute("currentVendorId", vendorId);
            model.addAttribute("currentSortBy", sortBy);
            model.addAttribute("currentSortDir", sortDir);
            
            // Check if any filters are active
            boolean hasActiveFilters = (q != null && !q.trim().isEmpty()) ||
                                    categoryId != null ||
                                    condition != null ||
                                    minPrice != null ||
                                    maxPrice != null ||
                                    inStock != null ||
                                    lowStock != null ||
                                    vendorId != null;
            model.addAttribute("hasActiveFilters", hasActiveFilters);
            
            // Pagination info
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", products.getTotalPages());
            model.addAttribute("totalElements", products.getTotalElements());
            
            return "products/advanced-search";
        } catch (Exception e) {
            // Log the error for debugging
            // logger.error("Error during advanced search: {}", e.getMessage(), e);
            
            model.addAttribute("errorMessage", "An error occurred during advanced search. Please try again.");
            model.addAttribute("products", Page.empty());
            model.addAttribute("categories", categoryService.findAllActive());
            model.addAttribute("conditions", ProductCondition.values());
            model.addAttribute("vendors", List.of());
            model.addAttribute("hasActiveFilters", false);
            model.addAttribute("currentPage", 0);
            model.addAttribute("totalPages", 0);
            model.addAttribute("totalElements", 0);
            
            return "products/advanced-search";
        }
    }
}