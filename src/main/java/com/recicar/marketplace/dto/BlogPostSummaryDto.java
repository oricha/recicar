package com.recicar.marketplace.dto;

import java.time.LocalDateTime;

public record BlogPostSummaryDto(String slug, String title, String summary, LocalDateTime publishedAt) {
}
