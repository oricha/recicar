package com.recicar.marketplace.controller;

import com.recicar.marketplace.service.ProductService;
import com.recicar.marketplace.service.CategoryService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.Authentication;
import com.recicar.marketplace.service.CustomUserDetailsService;

@Controller
public class HomeController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public HomeController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping("/")
    public String home(Model model, Authentication authentication) {
        // Get featured products (latest 8 products)
        var featuredProducts = productService.findActiveProducts(0, 12);
        model.addAttribute("products", featuredProducts.getContent());
        
        // Get categories for navigation
        model.addAttribute("categories", categoryService.findRootCategories());

        // Load Body Parts and Engine Parts sections using new search methods
        var bodyParts = productService.findBodyPartsForHomePage();
        model.addAttribute("bodyParts", bodyParts);

        var engineParts = productService.findEnginePartsForHomePage();
        model.addAttribute("engineParts", engineParts);
        // Add authentication information to the model
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated();
        model.addAttribute("isAuthenticated", isAuthenticated);

        if (isAuthenticated && authentication.getPrincipal() instanceof CustomUserDetailsService.CustomUserPrincipal userPrincipal) {
            model.addAttribute("currentUser", userPrincipal.getUser());
        }

        return "index";
    }
    
}
