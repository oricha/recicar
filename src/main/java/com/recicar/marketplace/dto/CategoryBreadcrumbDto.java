package com.recicar.marketplace.dto;

/**
 * Single level for product detail breadcrumb (category hierarchy).
 */
public class CategoryBreadcrumbDto {

    private String name;
    private String slug;

    public CategoryBreadcrumbDto() {
    }

    public CategoryBreadcrumbDto(String name, String slug) {
        this.name = name;
        this.slug = slug;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }
}
