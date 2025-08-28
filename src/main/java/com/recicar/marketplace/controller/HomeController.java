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

     @GetMapping("/index-1")
    public String index1(Model model) {
         // Get featured products (latest 8 products)
        var featuredProducts = productService.findActiveProducts(0);
        model.addAttribute("featuredProducts", featuredProducts.getContent());
        
        // Get categories for navigation
        model.addAttribute("categories", categoryService.findRootCategories());
        return "home/index-1";
    }

    @GetMapping("/index-2")
    public String index2(Model model) {
         // Get featured products (latest 8 products)
        var featuredProducts = productService.findActiveProducts(0);
        model.addAttribute("featuredProducts", featuredProducts.getContent());
        
        // Get categories for navigation
        model.addAttribute("categories", categoryService.findRootCategories());
        return "home/index-2";
    }

    @GetMapping("/index-3")
    public String index3(Model model) {
         // Get featured products (latest 8 products)
        var featuredProducts = productService.findActiveProducts(0);
        model.addAttribute("featuredProducts", featuredProducts.getContent());
        
        // Get categories for navigation
        model.addAttribute("categories", categoryService.findRootCategories());
        return "home/index-3";
    }

    @GetMapping("/index-4")
    public String index4(Model model) {
         // Get featured products (latest 8 products)
        var featuredProducts = productService.findActiveProducts(0);
        model.addAttribute("featuredProducts", featuredProducts.getContent());
        
        // Get categories for navigation
        model.addAttribute("categories", categoryService.findRootCategories());
        return "home/index-4";
    }

    @GetMapping("/index-5")
    public String index5(Model model) {
         // Get featured products (latest 8 products)
        var featuredProducts = productService.findActiveProducts(0);
        model.addAttribute("featuredProducts", featuredProducts.getContent());
        
        // Get categories for navigation
        model.addAttribute("categories", categoryService.findRootCategories());
        return "home/index-5";
    }

    @GetMapping("/index-6")
    public String index6(Model model) {
         // Get featured products (latest 8 products)
        var featuredProducts = productService.findActiveProducts(0);
        model.addAttribute("featuredProducts", featuredProducts.getContent());
        
        // Get categories for navigation
        model.addAttribute("categories", categoryService.findRootCategories());
        return "home/index-6";
    }

    @GetMapping("/index-7")
    public String index7(Model model) {
         // Get featured products (latest 8 products)
        var featuredProducts = productService.findActiveProducts(0);
        model.addAttribute("featuredProducts", featuredProducts.getContent());
        
        // Get categories for navigation
        model.addAttribute("categories", categoryService.findRootCategories());
        return "home/index-7";
    }

    @GetMapping("/index-list")
    public String indexList(Model model) {
         // Get featured products (latest 8 products)
        var featuredProducts = productService.findActiveProducts(0);
        model.addAttribute("featuredProducts", featuredProducts.getContent());
        
        // Get categories for navigation
        model.addAttribute("categories", categoryService.findRootCategories());
        return "shop-list/shop-right-sidebar-list";
    }
}