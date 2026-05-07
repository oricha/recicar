package com.recicar.marketplace.service;

import com.recicar.marketplace.dto.VendorSalesAnalyticsDto;
import com.recicar.marketplace.entity.Vendor;
import com.recicar.marketplace.repository.OrderItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VendorAnalyticsServiceTest {

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private VendorAnalyticsService vendorAnalyticsService;

    @Test
    void salesBetween_aggregatesRepositories() {
        Vendor v = new Vendor();
        v.setId(42L);

        LocalDate from = LocalDate.of(2026, 4, 1);
        LocalDate to = LocalDate.of(2026, 4, 10);

        when(orderItemRepository.sumLineTotalForVendorBetween(eq(42L), any(), any())).thenReturn(new BigDecimal("150.505"));
        when(orderItemRepository.countDistinctOrdersForVendorBetween(eq(42L), any(), any())).thenReturn(3L);
        when(orderItemRepository.countLinesForVendorBetween(eq(42L), any(), any())).thenReturn(8L);

        VendorSalesAnalyticsDto dto = vendorAnalyticsService.salesBetween(v, from, to);

        assertThat(dto.distinctOrderCount()).isEqualTo(3);
        assertThat(dto.lineItemCount()).isEqualTo(8);
        assertThat(dto.grossLineRevenue()).isEqualByComparingTo(new BigDecimal("150.51"));
        assertThat(dto.periodFromInclusive()).isEqualTo(from);
        assertThat(dto.periodToInclusive()).isEqualTo(to);

        verify(orderItemRepository).sumLineTotalForVendorBetween(eq(42L), eq(from.atStartOfDay()), eq(to.plusDays(1).atStartOfDay()));
    }

    @Test
    void salesBetween_swapsDatesWhenInverted() {
        Vendor v = new Vendor();
        v.setId(1L);
        LocalDate a = LocalDate.of(2026, 5, 2);
        LocalDate b = LocalDate.of(2026, 4, 1);

        when(orderItemRepository.sumLineTotalForVendorBetween(eq(1L), any(), any())).thenReturn(BigDecimal.ZERO);
        when(orderItemRepository.countDistinctOrdersForVendorBetween(eq(1L), any(), any())).thenReturn(0L);
        when(orderItemRepository.countLinesForVendorBetween(eq(1L), any(), any())).thenReturn(0L);

        VendorSalesAnalyticsDto dto = vendorAnalyticsService.salesBetween(v, a, b);

        assertThat(dto.periodFromInclusive()).isEqualTo(b);
        assertThat(dto.periodToInclusive()).isEqualTo(a);
    }
}
