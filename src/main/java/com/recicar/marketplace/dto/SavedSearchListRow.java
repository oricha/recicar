package com.recicar.marketplace.dto;

import java.time.LocalDateTime;

public record SavedSearchListRow(Long id, String label, String runSearchUrl, LocalDateTime createdAt) {
}
