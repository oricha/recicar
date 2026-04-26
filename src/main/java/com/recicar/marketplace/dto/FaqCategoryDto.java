package com.recicar.marketplace.dto;

import java.util.List;

public record FaqCategoryDto(String slug, String title, List<FaqEntryDto> entries) {
}
