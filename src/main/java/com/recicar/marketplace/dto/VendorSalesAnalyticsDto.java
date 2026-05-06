package com.recicar.marketplace.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Aggregated seller sales for analytics (based on shipped line totals for this vendor's lines).
 */
public record VendorSalesAnalyticsDto(
        LocalDate periodFromInclusive,
        LocalDate periodToInclusive,
        BigDecimal grossLineRevenue,
        long distinctOrderCount,
        long lineItemCount
) {
}
