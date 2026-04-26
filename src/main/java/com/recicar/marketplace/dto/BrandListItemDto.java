package com.recicar.marketplace.dto;

/**
 * Brand row for navigation listings and APIs.
 */
public record BrandListItemDto(
        long id,
        String name,
        String slug,
        String country
) {
}
