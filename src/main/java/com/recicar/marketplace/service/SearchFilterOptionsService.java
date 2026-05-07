package com.recicar.marketplace.service;

import com.recicar.marketplace.dto.BrandListItemDto;
import com.recicar.marketplace.entity.Brand;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

/**
 * Cached reference data for advanced search UI (brand list from {@link BrandService}).
 */
@Service
@Transactional(readOnly = true)
public class SearchFilterOptionsService {

    private final BrandService brandService;

    public SearchFilterOptionsService(BrandService brandService) {
        this.brandService = brandService;
    }

    /**
     * Brand rows for dropdowns; aligned with {@code /api/v1/brands} but suitable for Thymeleaf.
     */
    @Cacheable(cacheNames = "searchBrandOptions", key = "'all'", unless = "#result == null || #result.isEmpty()")
    public List<BrandListItemDto> listBrandOptions() {
        return brandService.findAll().stream()
                .sorted(Comparator.comparing(Brand::getName, String.CASE_INSENSITIVE_ORDER))
                .map(this::toDto)
                .toList();
    }

    private BrandListItemDto toDto(Brand b) {
        return new BrandListItemDto(
                b.getId(),
                b.getName(),
                b.getSlug(),
                b.getCountry() == null ? "" : b.getCountry()
        );
    }
}
