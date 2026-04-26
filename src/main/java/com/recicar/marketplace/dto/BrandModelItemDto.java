package com.recicar.marketplace.dto;

/**
 * Vehicle model under a brand for model selection navigation.
 */
public record BrandModelItemDto(
        long id,
        String modelName,
        String slug,
        String generation,
        Integer yearFrom,
        Integer yearTo
) {
}
