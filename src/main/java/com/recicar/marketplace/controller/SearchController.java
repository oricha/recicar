package com.recicar.marketplace.controller;

import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.service.CategoryService;
import com.recicar.marketplace.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final ProductService productService;
    private final CategoryService categoryService;

    /**
     * Main search endpoint - handles general search, part number, and OEM number searches
     * Supports both 'query' and 'q' parameters for backward compatibility
     */
    @GetMapping
    public String search(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        // Use 'query' parameter if available, otherwise fall back to 'q'
        String searchTerm = query != null ? query : q;
        
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return "redirect:/products";
        }

        if (searchTerm.length() < 2) {
            model.addAttribute("errorMessage", "Search term must be at least 2 characters long");
            model.addAttribute("categories", categoryService.findRootCategories());
            return "shop-list";
        }

        // Try exact part number search first
        List<Product> partNumberResults = productService.findByPartNumber(searchTerm);
        if (!partNumberResults.isEmpty()) {
            model.addAttribute("products", partNumberResults);
            model.addAttribute("searchQuery", searchTerm);
            model.addAttribute("searchType", "partNumber");
            model.addAttribute("categories", categoryService.findRootCategories());
            return "shop-list";
        }

        // Try exact OEM number search
        List<Product> oemNumberResults = productService.findByOemNumber(searchTerm);
        if (!oemNumberResults.isEmpty()) {
            model.addAttribute("products", oemNumberResults);
            model.addAttribute("searchQuery", searchTerm);
            model.addAttribute("searchType", "oemNumber");
            model.addAttribute("categories", categoryService.findRootCategories());
            return "shop-list";
        }

        // Check if it looks like a part/OEM number (alphanumeric with dashes, 5+ chars)
        if (searchTerm.matches("[a-zA-Z0-9-]{5,}")) {
            Page<Product> partNumberPage = productService.findByPartNumberContaining(searchTerm, PageRequest.of(page, 12));
            if (partNumberPage.hasContent()) {
                model.addAttribute("products", partNumberPage.getContent());
                model.addAttribute("page", partNumberPage);
                model.addAttribute("searchQuery", searchTerm);
                model.addAttribute("searchType", "partNumberContaining");
                model.addAttribute("categories", categoryService.findRootCategories());
                return "shop-list";
            }
            
            Page<Product> oemNumberPage = productService.findByOemNumberContaining(searchTerm, PageRequest.of(page, 12));
            if (oemNumberPage.hasContent()) {
                model.addAttribute("products", oemNumberPage.getContent());
                model.addAttribute("page", oemNumberPage);
                model.addAttribute("searchQuery", searchTerm);
                model.addAttribute("searchType", "oemNumberContaining");
                model.addAttribute("categories", categoryService.findRootCategories());
                return "shop-list";
            }
        }

        // Fall back to general search
        Page<Product> productPage = productService.searchProducts(searchTerm, PageRequest.of(page, 12));
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("page", productPage);
        model.addAttribute("searchQuery", searchTerm);
        model.addAttribute("searchType", "general");
        model.addAttribute("categories", categoryService.findRootCategories());
        return "shop-list";
    }

    /**
     * Search by part name specifically
     */
    @GetMapping("/part-name")
    public String searchByPartName(
            @RequestParam("partName") String partName,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        if (partName == null || partName.trim().isEmpty()) {
            model.addAttribute("errorMessage", "Part name is required");
            model.addAttribute("categories", categoryService.findRootCategories());
            return "shop-list";
        }

        if (partName.length() < 2) {
            model.addAttribute("errorMessage", "Part name must be at least 2 characters long");
            model.addAttribute("categories", categoryService.findRootCategories());
            return "shop-list";
        }

        Page<Product> productPage = productService.findByProductName(partName, PageRequest.of(page, 12));
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("page", productPage);
        model.addAttribute("partName", partName);
        model.addAttribute("searchType", "partName");
        model.addAttribute("totalElements", productPage.getTotalElements());
        model.addAttribute("categories", categoryService.findRootCategories());
        return "shop-list";
    }

    /**
     * Search by vehicle compatibility (Make/Model/Engine Type)
     */
    @GetMapping("/vehicle")
    public String searchByVehicle(
            @RequestParam("make") String make,
            @RequestParam("model") String model,
            @RequestParam("engineType") String engineType,
            @RequestParam(value = "partName", required = false) String partName,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model modelAttr
    ) {
        if (make == null || make.isBlank() || model == null || model.isBlank() || engineType == null || engineType.isBlank()) {
            modelAttr.addAttribute("errorMessage", "Make, Model and Engine Type are required");
            modelAttr.addAttribute("categories", categoryService.findRootCategories());
            return "shop-list";
        }
        
        Page<Product> productPage = productService.findByMakeModelEngineAndPartName(
            make.trim(), model.trim(), engineType.trim(), partName, PageRequest.of(page, 12));
        
        modelAttr.addAttribute("products", productPage.getContent());
        modelAttr.addAttribute("page", productPage);
        modelAttr.addAttribute("vehicleMake", make);
        modelAttr.addAttribute("vehicleModel", model);
        modelAttr.addAttribute("vehicleEngine", engineType);
        modelAttr.addAttribute("partName", partName);
        modelAttr.addAttribute("searchType", "vehicle");
        modelAttr.addAttribute("totalElements", productPage.getTotalElements());
        modelAttr.addAttribute("categories", categoryService.findRootCategories());
        return "shop-list";
    }

    /**
     * Search by category slug
     */
    @GetMapping("/category")
    public String searchByCategory(
            @RequestParam("slug") String slug,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        if (slug == null || slug.trim().isEmpty()) {
            model.addAttribute("errorMessage", "Category is required");
            model.addAttribute("products", Collections.emptyList());
            model.addAttribute("categories", categoryService.findRootCategories());
            return "shop-list";
        }

        return categoryService.findBySlug(slug)
                .map(category -> {
                    Page<Product> productPage = productService.findByCategory(category, PageRequest.of(page, 12));
                    model.addAttribute("products", productPage.getContent());
                    model.addAttribute("page", productPage);
                    model.addAttribute("category", category);
                    model.addAttribute("categorySlug", slug);
                    model.addAttribute("searchType", "category");
                    model.addAttribute("totalElements", productPage.getTotalElements());
                    model.addAttribute("categories", categoryService.findRootCategories());
                    return "shop-list";
                })
                .orElseGet(() -> {
                    model.addAttribute("errorMessage", "Category not found");
                    model.addAttribute("products", Collections.emptyList());
                    model.addAttribute("categories", categoryService.findRootCategories());
                    return "shop-list";
                });
    }
}