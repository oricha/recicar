package com.recicar.marketplace.controller;

import com.recicar.marketplace.dto.CategoryBreadcrumbItemDto;
import com.recicar.marketplace.dto.CategoryDetailDto;
import com.recicar.marketplace.dto.CategorySummaryDto;
import com.recicar.marketplace.entity.Category;
import com.recicar.marketplace.service.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryApiController {

    private final CategoryService categoryService;

    public CategoryApiController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping
    public List<CategorySummaryDto> listRoots() {
        return categoryService.findRootCategories().stream()
                .map(this::toSummary)
                .toList();
    }

    @GetMapping("/{slug}")
    public ResponseEntity<CategoryDetailDto> getBySlug(@PathVariable("slug") String slug) {
        return categoryService.findBySlug(slug)
                .map(c -> ResponseEntity.ok(toDetail(c)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{slug}/breadcrumb")
    public ResponseEntity<List<CategoryBreadcrumbItemDto>> breadcrumb(@PathVariable("slug") String slug) {
        return categoryService.findBySlug(slug)
                .map(c -> ResponseEntity.ok(toBreadcrumb(c)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private CategorySummaryDto toSummary(Category c) {
        List<Category> children = categoryService.findByParentId(c.getId());
        int n = children.size();
        return new CategorySummaryDto(
                c.getId(),
                c.getName(),
                c.getSlug(),
                c.getSortOrder(),
                n > 0,
                n
        );
    }

    private CategoryDetailDto toDetail(Category c) {
        List<CategorySummaryDto> children = categoryService.findByParentId(c.getId()).stream()
                .map(this::toSummary)
                .toList();
        List<CategoryBreadcrumbItemDto> crumb = toBreadcrumb(c);
        return new CategoryDetailDto(
                c.getId(),
                c.getName(),
                c.getSlug(),
                c.getDescription() == null ? "" : c.getDescription(),
                crumb,
                children
        );
    }

    private List<CategoryBreadcrumbItemDto> toBreadcrumb(Category c) {
        return categoryService.getCategoryHierarchy(c).stream()
                .map(x -> new CategoryBreadcrumbItemDto(x.getName(), x.getSlug()))
                .toList();
    }
}
