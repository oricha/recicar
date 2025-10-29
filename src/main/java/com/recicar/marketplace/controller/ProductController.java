package com.recicar.marketplace.controller;

import com.recicar.marketplace.entity.Category;
import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.service.CategoryService;
import com.recicar.marketplace.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;

    @GetMapping("/shop-list")
    public String productList(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "category", required = false) String categorySlug,
            Model model) {
        
        Page<Product> productPage;
        Category selectedCategory = null;
        
        if (categorySlug != null && !categorySlug.trim().isEmpty()) {
            // Filter by category
            Optional<Category> categoryOptional = categoryService.findBySlug(categorySlug);
            if (categoryOptional.isPresent()) {
                selectedCategory = categoryOptional.get();
                productPage = productService.findByCategory(selectedCategory, page);
                model.addAttribute("selectedCategory", selectedCategory);
                model.addAttribute("categorySlug", categorySlug);
            } else {
                // Category not found, show all products
                productPage = productService.findActiveProducts(page, 12);
                model.addAttribute("errorMessage", "Category not found");
            }
        } else {
            // Show all products
            productPage = productService.findActiveProducts(page, 12);
        }
        
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("page", productPage);
        
        // Add categories for sidebar
        List<Category> categories = categoryService.findAllActive();
        model.addAttribute("categories", categories);
        
        return "shop-list";
    }

    @GetMapping("/product-details")
    public String productDetails(@RequestParam("id") Long id, Model model) {
        Optional<Product> productOptional = productService.findById(id);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            model.addAttribute("product", product);

            // Get related products for the product details page
            List<Product> relatedProducts = productService.findRelatedProducts(id);
            model.addAttribute("relatedProducts", relatedProducts);

            return "product-details";
        }
        return "redirect:/";
    }
}
