package com.recicar.marketplace.controller;

import com.recicar.marketplace.service.ProductService;
import com.recicar.marketplace.service.CategoryService;
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
        var featuredProducts = productService.findActiveProducts(0);
        model.addAttribute("featuredProducts", featuredProducts.getContent());
        
        // Get categories for navigation
        model.addAttribute("categories", categoryService.findRootCategories());
        
        return "home-0";
    }

    
}