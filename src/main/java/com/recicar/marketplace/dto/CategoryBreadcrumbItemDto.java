package com.recicar.marketplace.dto;

/**
 * Single segment in a category path for hierarchy navigation.
 */
public record CategoryBreadcrumbItemDto(String name, String slug) {
}
