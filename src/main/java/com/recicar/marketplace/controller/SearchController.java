package com.recicar.marketplace.controller;

import com.recicar.marketplace.entity.Category;
import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.entity.ProductCondition;
import com.recicar.marketplace.service.CategoryService;
import com.recicar.marketplace.service.ProductService;
import com.recicar.marketplace.service.SearchFilterOptionsService;
import com.recicar.marketplace.service.SearchService;
import com.recicar.marketplace.web.search.AdvancedSearchPageLinks;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.IntFunction;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/search")
@RequiredArgsConstructor
public class SearchController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final SearchService searchService;
    private final SearchFilterOptionsService searchFilterOptionsService;

    /**
     * Main search endpoint - handles general search, part number, and OEM number searches
     * Supports both 'query' and 'q' parameters for backward compatibility
     */
    @GetMapping
    public String search(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "category", required = false) String categorySlug,
            @RequestParam(value = "brand", required = false) String brand,
            @RequestParam(value = "model", required = false) String vehicleModel,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        if (categorySlug != null && !categorySlug.trim().isEmpty()) {
            return searchByCategorySlug(categorySlug, page, model);
        }

        String brandTerm = brand != null ? brand.trim() : "";
        String modelTerm = vehicleModel != null ? vehicleModel.trim() : "";
        if (!brandTerm.isEmpty() || !modelTerm.isEmpty()) {
            String searchTerm = query != null && !query.isBlank() ? query : (q != null ? q : "");
            Page<Product> productPage = searchService.searchAdvanced(
                    searchTerm == null ? "" : searchTerm,
                    brandTerm,
                    modelTerm,
                    null,
                    null,
                    null,
                    null,
                    null,
                    PageRequest.of(page, 12)
            );
            model.addAttribute("products", productPage.getContent());
            model.addAttribute("page", productPage);
            model.addAttribute("searchQuery", searchTerm);
            model.addAttribute("searchType", "advancedVehicle");
            model.addAttribute("vehicleMake", brandTerm.isEmpty() ? null : brandTerm);
            model.addAttribute("vehicleModel", modelTerm.isEmpty() ? null : modelTerm);
            model.addAttribute("totalElements", productPage.getTotalElements());
            model.addAttribute("categories", categoryService.findRootCategories());
            return "shop-list";
        }

        // Use 'query' parameter if available, otherwise fall back to 'q'
        String searchTerm = query != null ? query : q;
        
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return "redirect:/products";
        }

        if (searchTerm.length() < 2) {
            model.addAttribute("errorMessage", "Search term must be at least 2 characters long");
            model.addAttribute("categories", categoryService.findRootCategories());
            return "shop-list";
        }

        // Try exact part number search first
        List<Product> partNumberResults = productService.findByPartNumber(searchTerm);
        if (!partNumberResults.isEmpty()) {
            model.addAttribute("products", partNumberResults);
            model.addAttribute("searchQuery", searchTerm);
            model.addAttribute("searchType", "partNumber");
            model.addAttribute("categories", categoryService.findRootCategories());
            return "shop-list";
        }

        // Try exact OEM number search
        List<Product> oemNumberResults = productService.findByOemNumber(searchTerm);
        if (!oemNumberResults.isEmpty()) {
            model.addAttribute("products", oemNumberResults);
            model.addAttribute("searchQuery", searchTerm);
            model.addAttribute("searchType", "oemNumber");
            model.addAttribute("categories", categoryService.findRootCategories());
            return "shop-list";
        }

        // Check if it looks like a part/OEM number (alphanumeric with dashes, 5+ chars)
        if (searchTerm.matches("[a-zA-Z0-9-]{5,}")) {
            Page<Product> partNumberPage = productService.findByPartNumberContaining(searchTerm, PageRequest.of(page, 12));
            if (partNumberPage.hasContent()) {
                model.addAttribute("products", partNumberPage.getContent());
                model.addAttribute("page", partNumberPage);
                model.addAttribute("searchQuery", searchTerm);
                model.addAttribute("searchType", "partNumberContaining");
                model.addAttribute("categories", categoryService.findRootCategories());
                return "shop-list";
            }
            
            Page<Product> oemNumberPage = productService.findByOemNumberContaining(searchTerm, PageRequest.of(page, 12));
            if (oemNumberPage.hasContent()) {
                model.addAttribute("products", oemNumberPage.getContent());
                model.addAttribute("page", oemNumberPage);
                model.addAttribute("searchQuery", searchTerm);
                model.addAttribute("searchType", "oemNumberContaining");
                model.addAttribute("categories", categoryService.findRootCategories());
                return "shop-list";
            }
        }

        // Fall back to general search
        Page<Product> productPage = productService.searchProducts(searchTerm, PageRequest.of(page, 12));
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("page", productPage);
        model.addAttribute("searchQuery", searchTerm);
        model.addAttribute("searchType", "general");
        model.addAttribute("categories", categoryService.findRootCategories());
        return "shop-list";
    }

    /**
     * Search by part name specifically
     */
    @GetMapping("/part-name")
    public String searchByPartName(
            @RequestParam("partName") String partName,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        if (partName == null || partName.trim().isEmpty()) {
            model.addAttribute("errorMessage", "Part name is required");
            model.addAttribute("categories", categoryService.findRootCategories());
            return "shop-list";
        }

        if (partName.length() < 2) {
            model.addAttribute("errorMessage", "Part name must be at least 2 characters long");
            model.addAttribute("categories", categoryService.findRootCategories());
            return "shop-list";
        }

        Page<Product> productPage = productService.findByProductName(partName, PageRequest.of(page, 12));
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("page", productPage);
        model.addAttribute("partName", partName);
        model.addAttribute("searchType", "partName");
        model.addAttribute("totalElements", productPage.getTotalElements());
        model.addAttribute("categories", categoryService.findRootCategories());
        return "shop-list";
    }

    /**
     * Search by vehicle compatibility (Make/Model/Engine Type)
     */
    @GetMapping("/vehicle")
    public String searchByVehicle(
            @RequestParam("make") String make,
            @RequestParam("model") String model,
            @RequestParam("engineType") String engineType,
            @RequestParam(value = "partName", required = false) String partName,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model modelAttr
    ) {
        if (make == null || make.isBlank() || model == null || model.isBlank() || engineType == null || engineType.isBlank()) {
            modelAttr.addAttribute("errorMessage", "Make, Model and Engine Type are required");
            modelAttr.addAttribute("categories", categoryService.findRootCategories());
            return "shop-list";
        }
        
        Page<Product> productPage = productService.findByMakeModelEngineAndPartName(
            make.trim(), model.trim(), engineType.trim(), partName, PageRequest.of(page, 12));
        
        modelAttr.addAttribute("products", productPage.getContent());
        modelAttr.addAttribute("page", productPage);
        modelAttr.addAttribute("vehicleMake", make);
        modelAttr.addAttribute("vehicleModel", model);
        modelAttr.addAttribute("vehicleEngine", engineType);
        modelAttr.addAttribute("partName", partName);
        modelAttr.addAttribute("searchType", "vehicle");
        modelAttr.addAttribute("totalElements", productPage.getTotalElements());
        modelAttr.addAttribute("categories", categoryService.findRootCategories());
        return "shop-list";
    }

    /**
     * Search by category slug
     */
    @GetMapping("/category")
    public String searchByCategory(
            @RequestParam("slug") String slug,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        return searchByCategorySlug(slug, page, model);
    }

    private String searchByCategorySlug(String slug, int page, Model model) {
        if (slug == null || slug.trim().isEmpty()) {
            model.addAttribute("errorMessage", "Category is required");
            model.addAttribute("products", Collections.emptyList());
            model.addAttribute("categories", categoryService.findRootCategories());
            return "shop-list";
        }

        return categoryService.findBySlug(slug)
                .map(category -> {
                    Page<Product> productPage;

                    if (!category.getChildren().isEmpty()) {
                        List<Long> categoryIds = new ArrayList<>();
                        collectCategoryAndChildrenIds(category, categoryIds);
                        productPage = productService.findByCategoryIds(categoryIds, PageRequest.of(page, 12));
                        model.addAttribute("selectedCategoryIds", categoryIds);
                    } else {
                        productPage = productService.findByCategory(category, PageRequest.of(page, 12));
                    }

                    model.addAttribute("products", productPage.getContent());
                    model.addAttribute("page", productPage);
                    model.addAttribute("category", category);
                    model.addAttribute("categorySlug", slug);
                    model.addAttribute("categoryHierarchy", categoryService.getCategoryHierarchy(category));
                    model.addAttribute("searchType", "category");
                    model.addAttribute("totalElements", productPage.getTotalElements());
                    model.addAttribute("categories", categoryService.findRootCategories());
                    return "shop-list";
                })
                .orElseGet(() -> {
                    model.addAttribute("errorMessage", "Category not found");
                    model.addAttribute("products", Collections.emptyList());
                    model.addAttribute("categories", categoryService.findRootCategories());
                    return "shop-list";
                });
    }

    private void collectCategoryAndChildrenIds(Category category, List<Long> categoryIds) {
        categoryIds.add(category.getId());
        for (Category child : category.getChildren()) {
            collectCategoryAndChildrenIds(child, categoryIds);
        }
    }

    /**
     * Search by multiple categories (for advanced filtering)
     */
    @GetMapping("/categories")
    public String searchByCategories(
            @RequestParam("category") String categoryIds,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ) {
        if (categoryIds == null || categoryIds.trim().isEmpty()) {
            model.addAttribute("errorMessage", "At least one category must be selected");
            model.addAttribute("products", Collections.emptyList());
            model.addAttribute("categories", categoryService.findRootCategories());
            return "shop-list";
        }

        try {
            List<Long> categoryIdList = Arrays.stream(categoryIds.split(","))
                    .map(String::trim)
                    .map(Long::parseLong)
                    .collect(Collectors.toList());

            Page<Product> productPage = productService.findByCategoryIds(categoryIdList, PageRequest.of(page, 12));
            
            model.addAttribute("products", productPage.getContent());
            model.addAttribute("page", productPage);
            model.addAttribute("selectedCategoryIds", categoryIdList);
            model.addAttribute("searchType", "categories");
            model.addAttribute("totalElements", productPage.getTotalElements());
            model.addAttribute("categories", categoryService.findRootCategories());
            
            return "shop-list";
        } catch (NumberFormatException e) {
            model.addAttribute("errorMessage", "Invalid category selection");
            model.addAttribute("products", Collections.emptyList());
            model.addAttribute("categories", categoryService.findRootCategories());
            return "shop-list";
        }
    }

    /**
     * Advanced search UI (seven filters) backed by {@link SearchService#searchAdvanced}
     * and brand list from {@link SearchFilterOptionsService} / {@link com.recicar.marketplace.service.BrandService}.
     */
    @GetMapping("/advanced")
    public String advanced(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(required = false) String brand,
            @RequestParam(value = "vehicleModel", required = false) String vehicleModel,
            @RequestParam(required = false) String modification,
            @RequestParam(required = false) String condition,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(defaultValue = "relevance") String sort,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "false") boolean submitted,
            Model model
    ) {
        model.addAttribute("pageTitle", "Búsqueda avanzada — ReciCar");
        model.addAttribute("brandOptions", searchFilterOptionsService.listBrandOptions());
        model.addAttribute("conditions", ProductCondition.values());
        model.addAttribute("categories", categoryService.findRootCategories());

        model.addAttribute("q", q != null ? q : "");
        model.addAttribute("advBrand", brand != null ? brand : "");
        model.addAttribute("advModel", vehicleModel != null ? vehicleModel : "");
        model.addAttribute("advModification", modification != null ? modification : "");
        model.addAttribute("advCondition", condition != null ? condition : "");
        model.addAttribute("advInStock", inStock);
        model.addAttribute("advMinPrice", minPrice);
        model.addAttribute("advMaxPrice", maxPrice);
        model.addAttribute("sort", sort);

        if (!submitted) {
            model.addAttribute("products", Collections.emptyList());
            model.addAttribute("page", null);
            model.addAttribute("searchExecuted", false);
            return "search-advanced";
        }

        Page<Product> productPage = searchService.searchAdvanced(
                emptyToNull(q),
                emptyToNull(brand),
                emptyToNull(vehicleModel),
                emptyToNull(modification),
                emptyToNull(condition),
                inStock,
                minPrice,
                maxPrice,
                pageableForSort(page, 12, sort)
        );

        model.addAttribute("products", productPage.getContent());
        model.addAttribute("page", productPage);
        model.addAttribute("searchExecuted", true);
        model.addAttribute("searchType", "advanced");
        model.addAttribute("totalElements", productPage.getTotalElements());
        model.addAttribute("advPages", new AdvancedSearchPageLinks(buildAdvancedPageLinkFunction(
                q, brand, vehicleModel, modification, condition, inStock, minPrice, maxPrice, sort)));
        return "search-advanced";
    }

    private IntFunction<String> buildAdvancedPageLinkFunction(
            String q,
            String brand,
            String vehicleModel,
            String modification,
            String condition,
            Boolean inStock,
            BigDecimal minPrice,
            BigDecimal maxPrice,
            String sort
    ) {
        return pageIdx -> {
            UriComponentsBuilder b = UriComponentsBuilder.fromPath("/search/advanced")
                    .queryParam("submitted", "true")
                    .queryParam("page", pageIdx)
                    .queryParam("sort", sort == null || sort.isBlank() ? "relevance" : sort);
            if (q != null && !q.isBlank()) {
                b.queryParam("q", q);
            }
            if (brand != null && !brand.isBlank()) {
                b.queryParam("brand", brand);
            }
            if (vehicleModel != null && !vehicleModel.isBlank()) {
                b.queryParam("vehicleModel", vehicleModel);
            }
            if (modification != null && !modification.isBlank()) {
                b.queryParam("modification", modification);
            }
            if (condition != null && !condition.isBlank()) {
                b.queryParam("condition", condition);
            }
            if (Boolean.TRUE.equals(inStock)) {
                b.queryParam("inStock", "true");
            }
            if (minPrice != null) {
                b.queryParam("minPrice", minPrice.stripTrailingZeros().toPlainString());
            }
            if (maxPrice != null) {
                b.queryParam("maxPrice", maxPrice.stripTrailingZeros().toPlainString());
            }
            return b.encode().build().toUriString();
        };
    }

    private static PageRequest pageableForSort(int page, int size, String sort) {
        if (sort == null || sort.isBlank()) {
            return PageRequest.of(page, size);
        }
        return switch (sort) {
            case "price_asc" -> PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "price"));
            case "price_desc" -> PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "price"));
            default -> PageRequest.of(page, size);
        };
    }

    private static String emptyToNull(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        return s.trim();
    }
}
