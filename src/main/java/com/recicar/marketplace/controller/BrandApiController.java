package com.recicar.marketplace.controller;

import com.recicar.marketplace.dto.BrandListItemDto;
import com.recicar.marketplace.dto.BrandModelItemDto;
import com.recicar.marketplace.entity.Brand;
import com.recicar.marketplace.entity.BrandModel;
import com.recicar.marketplace.service.BrandService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/brands")
public class BrandApiController {

    private final BrandService brandService;

    public BrandApiController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping
    public List<BrandListItemDto> list(@RequestParam(value = "q", required = false) String query) {
        List<Brand> all = brandService.findAll();
        if (query == null || query.isBlank()) {
            return all.stream().map(this::toListItem).toList();
        }
        String q = query.trim().toLowerCase();
        return all.stream()
                .filter(b -> b.getName().toLowerCase().contains(q))
                .map(this::toListItem)
                .toList();
    }

    @GetMapping("/{slug}")
    public ResponseEntity<BrandListItemDto> getBySlug(@PathVariable("slug") String slug) {
        return brandService.findBySlug(slug)
                .map(b -> ResponseEntity.ok(toListItem(b)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/{slug}/models")
    public ResponseEntity<List<BrandModelItemDto>> listModels(@PathVariable("slug") String slug) {
        return brandService.findBySlug(slug)
                .map(b -> ResponseEntity.ok(
                        brandService.findByBrandId(b.getId()).stream()
                                .map(this::toModelItem)
                                .toList()
                ))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private BrandListItemDto toListItem(Brand b) {
        return new BrandListItemDto(
                b.getId(),
                b.getName(),
                b.getSlug(),
                b.getCountry() == null ? "" : b.getCountry()
        );
    }

    private BrandModelItemDto toModelItem(BrandModel m) {
        return new BrandModelItemDto(
                m.getId(),
                m.getModelName(),
                m.getSlug(),
                m.getGeneration() == null ? "" : m.getGeneration(),
                m.getYearFrom(),
                m.getYearTo()
        );
    }
}
