package com.recicar.marketplace.dto;

/**
 * API / view projection for a FAQ row.
 */
public record FaqEntryDto(Long id, String question, String answer) {
}
