package com.recicar.marketplace.controller;

import com.recicar.marketplace.service.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class CategoriesController {

    private final CategoryService categoryService;

    public CategoriesController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Display categories explorer page
     */
    @GetMapping("/categories")
    public String categories(Model model) {
        model.addAttribute("categories", categoryService.findRootCategories());
        model.addAttribute("pageTitle", "Categorías — ReciCar");
        return "categories";
    }

    /**
     * Hierarchical category browser: subcategories, breadcrumb, and link to product search.
     */
    @GetMapping("/categories/view/{slug}")
    public String viewCategory(@PathVariable("slug") String slug, Model model, HttpServletRequest request) {
        return categoryService.findBySlug(slug)
                .map(category -> {
                    model.addAttribute("category", category);
                    model.addAttribute("subcategories", categoryService.findByParentId(category.getId()));
                    model.addAttribute("categoryHierarchy", categoryService.getCategoryHierarchy(category));
                    String listTitle = category.getMetaTitle() != null && !category.getMetaTitle().isBlank()
                            ? category.getMetaTitle()
                            : (category.getName() + " — Categorías — ReciCar");
                    model.addAttribute("pageTitle", listTitle);
                    if (category.getMetaDescription() != null && !category.getMetaDescription().isBlank()) {
                        model.addAttribute("metaDescription", category.getMetaDescription());
                    } else if (category.getDescription() != null) {
                        String d = category.getDescription();
                        if (d.length() > 300) {
                            d = d.substring(0, 297) + "…";
                        }
                        model.addAttribute("metaDescription", d);
                    }
                    model.addAttribute("canonicalUrl", request.getRequestURL().toString());
                    return "category-browse";
                })
                .orElse("redirect:/categories");
    }

    /**
     * Redirect category links to search results by category.
     */
    @GetMapping("/categories/{slug}")
    public String redirectCategoryToSearch(@PathVariable("slug") String slug) {
        return "redirect:/search?category=" + slug;
    }
}
