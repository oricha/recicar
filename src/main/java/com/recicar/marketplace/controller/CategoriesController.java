package com.recicar.marketplace.controller;

import com.recicar.marketplace.service.CategoryService;
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
        return "categories";
    }

    /**
     * Redirect category links to search results by category.
     */
    @GetMapping("/categories/{slug}")
    public String redirectCategoryToSearch(@PathVariable("slug") String slug) {
        return "redirect:/search?category=" + slug;
    }
}
