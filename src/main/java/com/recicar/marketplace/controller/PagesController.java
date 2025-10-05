package com.recicar.marketplace.controller;

import com.recicar.marketplace.service.ProductService;
import com.recicar.marketplace.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PagesController {

    private final ProductService productService;
    private final CategoryService categoryService;

    public PagesController(ProductService productService, CategoryService categoryService) {
        this.productService = productService;
        this.categoryService = categoryService;
    }

    @GetMapping("/index-0")
    public String index0(Model model) {
         // Get featured products (latest 8 products)
        var featuredProducts = productService.findActiveProducts(0, 12);
        model.addAttribute("featuredProducts", featuredProducts.getContent());
        
        // Get categories for navigation
        model.addAttribute("categories", categoryService.findRootCategories());
        return "home";
    }

    @GetMapping("/index-1")
    public String index1(Model model) {
         // Get featured products (latest 8 products)
        var featuredProducts = productService.findActiveProducts(0, 12);
        model.addAttribute("featuredProducts", featuredProducts.getContent());
        
        // Get categories for navigation
        model.addAttribute("categories", categoryService.findRootCategories());
        return "home/index-1";
    }

    @GetMapping("/index-2")
    public String index2(Model model) {
         // Get featured products (latest 8 products)
        var featuredProducts = productService.findActiveProducts(0, 12);
        model.addAttribute("featuredProducts", featuredProducts.getContent());
        
        // Get categories for navigation
        model.addAttribute("categories", categoryService.findRootCategories());
        return "index-2";
    }
    
    @GetMapping("/about")
    public String about(Model model) {
        return "about";
    }

    @GetMapping("/contact")
    public String contact(Model model) {
        return "contact";
    }
     @GetMapping("/faq")
     public String faq(Model model) { return "faq";}
     @GetMapping("/wishlist" )
     public String wishlist(Model model, jakarta.servlet.http.HttpSession session) {
         @SuppressWarnings("unchecked")
         java.util.Set<Long> ids = (java.util.Set<Long>) session.getAttribute("WISHLIST_PRODUCT_IDS");
         java.util.List<Long> idList = (ids != null) ? new java.util.ArrayList<>(ids) : java.util.List.of();
         var products = productService.findByIds(idList);
         model.addAttribute("wishlistProducts", products);
         return "wishlist";
     }
    @GetMapping("/my-account.html" )
    public String myAccount(Model model) { return "my-account";}
    @GetMapping("/compare" )
    public String compare(Model model) { return "compare";}
    @GetMapping("/services" )
    public String services(Model model) { return "services";}
    @GetMapping("/blog")
    public String blog(Model model) {return "blog";}
    @GetMapping("/coming-soon")
    public String comingSoon(Model model) { return "coming-soon";}
    @GetMapping("/privacy-policy")
    public String privacyPolicy(Model model) { return "privacy-policy";}
    @GetMapping("/blog-details")
    public String blogDetails(Model model) { return "blog-details";}
    @GetMapping("/blog-fullwidth")
    public String blogFullwidth(Model model) { return "blog-fullwidth";}
    @GetMapping("/blog-sidebar")
    public String blogSidebar(Model model) { return "blog-sidebar";}
    @GetMapping("/my-account")
    public String account(Model model) { return "my-account";}
    @GetMapping("/404")
    public String error404(Model model) { return "404";}
}
