package com.recicar.marketplace.controller;

import com.recicar.marketplace.entity.Category;
import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.service.CategoryService;
import com.recicar.marketplace.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class CategoriesController {

    private final CategoryService categoryService;
    private final ProductService productService;

    public CategoriesController(CategoryService categoryService, ProductService productService) {
        this.categoryService = categoryService;
        this.productService = productService;
    }

    /**
     * Display the categories page with optional category filtering
     */
    @GetMapping("/categories")
    public String categories(
            @RequestParam(value = "category", required = false) String categorySlug,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model) {
        
        List<Category> categories = categoryService.findAllActive();
        model.addAttribute("categories", categories);
        
        // If a specific category is selected, show products for that category
        if (categorySlug != null && !categorySlug.trim().isEmpty()) {
            Optional<Category> categoryOptional = categoryService.findBySlug(categorySlug);
            if (categoryOptional.isPresent()) {
                Category selectedCategory = categoryOptional.get();
                Page<Product> productPage = productService.findByCategory(selectedCategory, page);
                
                model.addAttribute("products", productPage.getContent());
                model.addAttribute("page", productPage);
                model.addAttribute("selectedCategory", selectedCategory);
                model.addAttribute("categorySlug", categorySlug);
                model.addAttribute("showProducts", true);
            } else {
                model.addAttribute("errorMessage", "Category not found");
            }
        }
        
        return "categories";
    }

}
