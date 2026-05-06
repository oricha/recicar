package com.recicar.marketplace.controller;

import com.recicar.marketplace.entity.Category;
import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.service.CategoryService;
import com.recicar.marketplace.service.ProductDetailService;
import com.recicar.marketplace.service.ProductService;
import com.recicar.marketplace.web.ProductDetailSeoHelper;
import com.recicar.marketplace.web.ShopListingConstants;
import com.recicar.marketplace.web.ShopListingModelHelper;
import jakarta.servlet.http.HttpServletRequest;
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
    private final ShopListingModelHelper shopListingModelHelper;
    private final ProductDetailService productDetailService;
    private final ProductDetailSeoHelper productDetailSeoHelper;

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
                productPage = productService.findActiveProducts(page, ShopListingConstants.PAGE_SIZE);
                model.addAttribute("errorMessage", "Category not found");
            }
        } else {
            // Show all products
            productPage = productService.findActiveProducts(page, ShopListingConstants.PAGE_SIZE);
        }

        shopListingModelHelper.putPagedListing(model, productPage);
        
        // Add categories for sidebar
        List<Category> categories = categoryService.findAllActive();
        model.addAttribute("categories", categories);
        
        return "shop-list";
    }

    @GetMapping("/product-details")
    public String productDetails(@RequestParam("id") Long id, HttpServletRequest request, Model model) {
        return productDetailService.getProductDetail(id)
                .map(detail -> {
                    String origin = productDetailSeoHelper.resolvePublicOrigin(request);
                    String canonicalUrl = origin + "/product-details?id=" + id;
                    String ogImage = detail.getProductImageUrls() != null && !detail.getProductImageUrls().isEmpty()
                            ? productDetailSeoHelper.toAbsoluteUrl(origin, detail.getProductImageUrls().getFirst())
                            : "";

                    model.addAttribute("detail", detail);
                    model.addAttribute("productSeoDescription", productDetailSeoHelper.metaDescription(detail));
                    model.addAttribute("productJsonLd", productDetailSeoHelper.productJsonLd(detail, canonicalUrl));
                    model.addAttribute("breadcrumbJsonLd",
                            productDetailSeoHelper.breadcrumbJsonLd(detail.getCategoryBreadcrumb(),
                                    detail.getTitle(), canonicalUrl, origin));
                    model.addAttribute("canonicalProductUrl", canonicalUrl);
                    model.addAttribute("ogImageUrl", ogImage);
                    model.addAttribute("isUsedPart",
                            detail.getCondition() != null && "USED".equalsIgnoreCase(detail.getCondition()));

                    model.addAttribute("pageTitle",
                            detail.getTitle() != null ? detail.getTitle() + " | ReciCar" : "ReciCar — Detalle");

                    return "product-details";
                })
                .orElse("redirect:/");
    }
}
