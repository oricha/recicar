package com.recicar.marketplace.service;

import com.recicar.marketplace.dto.VendorOrderListItemDto;
import com.recicar.marketplace.entity.Order;
import com.recicar.marketplace.entity.OrderItem;
import com.recicar.marketplace.entity.Vendor;
import com.recicar.marketplace.repository.OrderItemRepository;
import com.recicar.marketplace.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VendorOrderMetricsServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private VendorOrderMetricsService metricsService;

    @Test
    void getOrdersForVendorPage_capsSizeAtOneHundred() {
        Vendor v = new Vendor();
        v.setId(55L);

        when(orderRepository.findPageForVendor(eq(55L), eq(PageRequest.of(0, 100))))
                .thenReturn(new PageImpl<>(List.of()));

        metricsService.getOrdersForVendorPage(v, 0, 500);

        verify(orderRepository).findPageForVendor(55L, PageRequest.of(0, 100));
    }

    @Test
    void getRecentOrders_filtersLineTotalsToVendorLinesOnly() {
        Vendor seller = new Vendor();
        seller.setId(99L);

        Order order = new Order();
        order.setId(700L);
        order.setOrderNumber("ON-1");
        order.setCreatedAt(LocalDateTime.now());
        order.setStatus(Order.OrderStatus.SHIPPED);

        OrderItem theirs = new OrderItem();
        theirs.setVendorId(99L);
        theirs.setTotalPrice(new BigDecimal("40.00"));
        theirs.setOrder(order);

        OrderItem other = new OrderItem();
        other.setVendorId(1L);
        other.setTotalPrice(new BigDecimal("999"));
        other.setOrder(order);

        List<OrderItem> items = new ArrayList<>();
        items.add(theirs);
        items.add(other);
        order.setItems(items);

        when(orderRepository.findPageForVendor(eq(99L), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(order)));

        List<VendorOrderListItemDto> recent = metricsService.getRecentOrdersForVendor(seller, 5);

        assertThat(recent).hasSize(1);
        assertThat(recent.get(0).lineTotalForVendor()).isEqualByComparingTo(new BigDecimal("40.00"));
        assertThat(recent.get(0).lineItemCount()).isEqualTo(1);
    }
}
