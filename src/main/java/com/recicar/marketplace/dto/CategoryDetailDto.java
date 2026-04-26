package com.recicar.marketplace.dto;

import java.util.List;

/**
 * Category with direct children and breadcrumb for hierarchy navigation.
 */
public record CategoryDetailDto(
        long id,
        String name,
        String slug,
        String description,
        List<CategoryBreadcrumbItemDto> breadcrumb,
        List<CategorySummaryDto> children
) {
}
