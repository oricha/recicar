package com.recicar.marketplace.dto;

import java.math.BigDecimal;

/**
 * Dashboard and API summary for a seller.
 */
public record VendorPanelSummaryDto(
        long activeProductCount,
        long totalProductCount,
        BigDecimal monthSales,
        long pendingOrderCount
) {
}
