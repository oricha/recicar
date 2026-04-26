package com.recicar.marketplace.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * A single order in vendor-facing lists, with totals only for that vendor's line items.
 */
public record VendorOrderListItemDto(
        Long orderId,
        String orderNumber,
        LocalDateTime createdAt,
        String status,
        BigDecimal lineTotalForVendor,
        int lineItemCount
) {
}
