package com.recicar.marketplace.controller;

import com.recicar.marketplace.entity.Category;
import com.recicar.marketplace.service.CategoryService;
import com.recicar.marketplace.service.ProductService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Controller
public class CategoriesController {

    private final CategoryService categoryService;
    private final ProductService productService;

    public CategoriesController(CategoryService categoryService, ProductService productService) {
        this.categoryService = categoryService;
        this.productService = productService;
    }

    /**
     * Display the categories page with all categories listed
     * If a category parameter is provided, redirect to search
     */
    @GetMapping("/categories")
    public String categories(
            @RequestParam(value = "category", required = false) String categorySlug,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model) {
        
        // If a specific category is selected, redirect to search endpoint
        if (categorySlug != null && !categorySlug.trim().isEmpty()) {
            UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/search/category")
                    .queryParam("slug", categorySlug);
            
            if (page > 0) {
                builder.queryParam("page", page);
            }
            
            String redirectUrl = builder.build().toUriString();
            return "redirect:" + redirectUrl;
        }
        
        // Otherwise, show the categories listing page
        List<Category> categories = categoryService.findAllActive();
        model.addAttribute("categories", categories);
        
        return "categories";
    }

    /**
     * Handle /categories/{slug} URL pattern - redirect to search
     * This supports direct category URL access and redirects to the search endpoint
     */
    @GetMapping("/categories/{slug}")
    public String categoryBySlug(
            @PathVariable("slug") String slug,
            @RequestParam(value = "page", defaultValue = "0") int page) {
        
        // Redirect to the search controller for category-based product search
        UriComponentsBuilder builder = UriComponentsBuilder.fromPath("/search/category")
                .queryParam("slug", slug);
        
        if (page > 0) {
            builder.queryParam("page", page);
        }
        
        String redirectUrl = builder.build().toUriString();
        return "redirect:" + redirectUrl;
    }

}
