package com.recicar.marketplace.controller;

import com.recicar.marketplace.service.ProductService;
import com.recicar.marketplace.service.CategoryService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public HomeController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping("/")
    public String home(Model model) {
        // Get featured products (latest 8 products)
        var featuredProducts = productService.findActiveProducts(0, 12);
        model.addAttribute("products", featuredProducts.getContent());
        
        // Get categories for navigation
        model.addAttribute("categories", categoryService.findRootCategories());

        // Load Body Parts and Engine Parts sections
        var bodyCategories = categoryService.searchByName("Body");
        if (bodyCategories != null && !bodyCategories.isEmpty()) {
            var bodyPage = productService.findByCategory(bodyCategories.get(0), PageRequest.of(0, 9));
            model.addAttribute("bodyParts", bodyPage.getContent());
        } else {
            model.addAttribute("bodyParts", java.util.List.of());
        }

        var engineCategories = categoryService.searchByName("Engine");
        if (engineCategories != null && !engineCategories.isEmpty()) {
            var enginePage = productService.findByCategory(engineCategories.get(0), PageRequest.of(0, 9));
            model.addAttribute("engineParts", enginePage.getContent());
        } else {
            model.addAttribute("engineParts", java.util.List.of());
        }
        
        return "index";
    }
    
}
