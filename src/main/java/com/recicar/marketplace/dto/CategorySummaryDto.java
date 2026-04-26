package com.recicar.marketplace.dto;

/**
 * Category node for navigation APIs (list or tree children).
 */
public record CategorySummaryDto(
        long id,
        String name,
        String slug,
        Integer sortOrder,
        boolean hasChildren,
        int childCount
) {
}
