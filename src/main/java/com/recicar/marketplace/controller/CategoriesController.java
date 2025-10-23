package com.recicar.marketplace.controller;

import com.recicar.marketplace.entity.Category;
import com.recicar.marketplace.service.CategoryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class CategoriesController {

    private final CategoryService categoryService;

    public CategoriesController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Display the categories page
     */
    @GetMapping("/categories")
    public String categories(Model model) {
        List<Category> categories = categoryService.findAllActive();
        model.addAttribute("categories", categories);
        return "categories";
    }

    /**
     * Redirect to search page for specific category
     */
    @GetMapping("/categories/{slug}")
    public String categoryProducts(@PathVariable String slug) {
        // Redirect to search controller with category parameter
        return "redirect:/search/category?slug=" + slug;
    }

}
