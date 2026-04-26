package com.recicar.marketplace.dto;

import java.time.LocalDateTime;

public record ConversationSummaryResponse(
        Long id,
        Long vendorId,
        String vendorBusinessName,
        Long customerId,
        String customerName,
        Long productId,
        String productName,
        LocalDateTime updatedAt
) { }
