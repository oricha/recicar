package com.recicar.marketplace.controller;

import com.recicar.marketplace.repository.UserRepository;
import com.recicar.marketplace.service.ProductService;
import com.recicar.marketplace.service.CategoryService;
import com.recicar.marketplace.service.WishlistService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PagesController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final WishlistService wishlistService;
    private final UserRepository userRepository;

    public PagesController(ProductService productService, CategoryService categoryService, WishlistService wishlistService,
            UserRepository userRepository) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.wishlistService = wishlistService;
        this.userRepository = userRepository;
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
    

     @GetMapping("/wishlist" )
     public String wishlist(
             Model model,
             jakarta.servlet.http.HttpSession session,
             @AuthenticationPrincipal UserDetails userDetails) {
         java.util.List<Long> idList;
         if (userDetails != null) {
             idList = new java.util.ArrayList<>(userRepository.findByEmailIgnoreCase(userDetails.getUsername())
                     .map(u -> wishlistService.productIdsForUser(u.getId()))
                     .orElse(java.util.Set.of()));
         } else {
             @SuppressWarnings("unchecked")
             java.util.Set<Long> ids = (java.util.Set<Long>) session.getAttribute("WISHLIST_PRODUCT_IDS");
             idList = (ids != null) ? new java.util.ArrayList<>(ids) : java.util.List.of();
         }
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
    @GetMapping("/coming-soon")
    public String comingSoon(Model model) { return "coming-soon";}
    @GetMapping("/my-account")
    public String account(Model model) { return "my-account";}
    @GetMapping("/404")
    public String error404(Model model) { return "404";}
    @GetMapping("/seller-dashboard")
    public String sellerDashboard(Model model) { return "seller-dashboard";}
}
