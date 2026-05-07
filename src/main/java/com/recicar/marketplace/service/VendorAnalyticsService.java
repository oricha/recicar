package com.recicar.marketplace.service;

import com.recicar.marketplace.dto.VendorSalesAnalyticsDto;
import com.recicar.marketplace.entity.Vendor;
import com.recicar.marketplace.repository.OrderItemRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Lightweight seller analytics based on persisted order lines.
 */
@Service
public class VendorAnalyticsService {

    private final OrderItemRepository orderItemRepository;

    public VendorAnalyticsService(OrderItemRepository orderItemRepository) {
        this.orderItemRepository = orderItemRepository;
    }

    /**
     * Default window: inclusive last 30 days ending today.
     */
    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "vendorSalesAnalytics", key = "#vendor.id + '_' + #from + '_' + #to")
    public VendorSalesAnalyticsDto salesBetween(Vendor vendor, LocalDate from, LocalDate to) {
        LocalDate effectiveTo = (to != null ? to : LocalDate.now());
        LocalDate effectiveFrom = (from != null ? from : effectiveTo.minusDays(29));
        if (effectiveFrom.isAfter(effectiveTo)) {
            LocalDate tmp = effectiveFrom;
            effectiveFrom = effectiveTo;
            effectiveTo = tmp;
        }
        LocalDateTime start = effectiveFrom.atStartOfDay();
        LocalDateTime endExclusive = effectiveTo.plusDays(1).atStartOfDay();

        BigDecimal revenue = orderItemRepository.sumLineTotalForVendorBetween(
                vendor.getId(), start, endExclusive);
        if (revenue == null) {
            revenue = BigDecimal.ZERO;
        }
        long distinctOrders = orderItemRepository.countDistinctOrdersForVendorBetween(
                vendor.getId(), start, endExclusive);
        long lineCount = orderItemRepository.countLinesForVendorBetween(
                vendor.getId(), start, endExclusive);

        revenue = revenue.setScale(2, RoundingMode.HALF_UP);
        return new VendorSalesAnalyticsDto(effectiveFrom, effectiveTo, revenue, distinctOrders, lineCount);
    }
}
