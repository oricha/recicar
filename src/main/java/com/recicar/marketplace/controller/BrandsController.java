package com.recicar.marketplace.controller;

import com.recicar.marketplace.service.BrandService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BrandsController {

    private final BrandService brandService;

    public BrandsController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping("/marcas")
    public String listBrands(
            @RequestParam(value = "q", required = false) String query,
            Model model
    ) {
        if (query == null || query.isBlank()) {
            model.addAttribute("brands", brandService.findAll());
        } else {
            String q = query.trim().toLowerCase();
            model.addAttribute("brands", brandService.findAll().stream()
                    .filter(b -> b.getName().toLowerCase().contains(q))
                    .toList());
        }
        model.addAttribute("searchQuery", query == null ? "" : query.trim());
        model.addAttribute("pageTitle", "Marcas de vehículo — ReciCar");
        return "marcas";
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
