package com.recicar.marketplace.controller;

import com.recicar.marketplace.entity.Brand;
import com.recicar.marketplace.service.BrandService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Locale;

@Controller
public class BrandsController {

    private static final int BRANDS_PAGE_SIZE = 48;

    private final BrandService brandService;

    public BrandsController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping("/marcas")
    public String listBrands(
            @RequestParam(value = "q", required = false) String query,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model model
    ) {
        List<Brand> filtered = resolveFilteredBrands(query);
        int total = filtered.size();
        int pageSafe = Math.max(0, page);
        int from = Math.min(pageSafe * BRANDS_PAGE_SIZE, total);
        int to = Math.min(from + BRANDS_PAGE_SIZE, total);
        model.addAttribute("brands", filtered.subList(from, to));
        model.addAttribute("brandPage", pageSafe);
        model.addAttribute("brandPageSize", BRANDS_PAGE_SIZE);
        model.addAttribute("brandTotal", total);
        model.addAttribute("brandTotalPages", total == 0 ? 0 : (total + BRANDS_PAGE_SIZE - 1) / BRANDS_PAGE_SIZE);
        model.addAttribute("brandHasPrev", pageSafe > 0);
        model.addAttribute("brandHasNext", to < total);
        model.addAttribute("searchQuery", query == null ? "" : query.trim());
        model.addAttribute("pageTitle", "Marcas de vehículo — ReciCar");
        return "marcas";
    }

    private List<Brand> resolveFilteredBrands(String query) {
        List<Brand> all = brandService.findAll();
        if (query == null || query.isBlank()) {
            return all;
        }
        String q = query.trim().toLowerCase(Locale.ROOT);
        return all.stream()
                .filter(b -> b.getName() != null && b.getName().toLowerCase(Locale.ROOT).contains(q))
                .toList();
    }

    @GetMapping("/marcas/{slug}")
    public String brandDetail(@PathVariable("slug") String slug, Model model) {
        return brandService.findBySlug(slug)
                .map(brand -> {
                    model.addAttribute("brand", brand);
                    model.addAttribute("models", brandService.findByBrandId(brand.getId()));
                    model.addAttribute("pageTitle", brand.getName() + " — modelos — ReciCar");
                    return "marca-detail";
                })
                .orElse("redirect:/marcas");
    }
}
