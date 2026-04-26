package com.recicar.marketplace.service;

import com.recicar.marketplace.dto.VendorOrderListItemDto;
import com.recicar.marketplace.entity.Order;
import com.recicar.marketplace.entity.OrderItem;
import com.recicar.marketplace.entity.Vendor;
import com.recicar.marketplace.repository.OrderItemRepository;
import com.recicar.marketplace.repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Aggregates order-related metrics and lists for a seller.
 */
@Service
@Transactional(readOnly = true)
public class VendorOrderMetricsService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public VendorOrderMetricsService(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public long countPendingOrders(Vendor vendor) {
        return orderRepository.countPendingOrdersForVendor(vendor.getId());
    }

    public BigDecimal sumMonthRevenueExcludingCanceled(Vendor vendor) {
        LocalDate firstDay = LocalDate.now().withDayOfMonth(1);
        return orderItemRepository.sumLineTotalForVendorSince(vendor.getId(), firstDay.atStartOfDay());
    }

    @Transactional(readOnly = true)
    public List<VendorOrderListItemDto> getRecentOrdersForVendor(Vendor vendor, int maxOrders) {
        int size = Math.min(50, Math.max(1, maxOrders));
        Page<Order> page = orderRepository.findPageForVendor(
                vendor.getId(),
                PageRequest.of(0, size));
        return page.getContent().stream()
                .map(o -> toListItem(vendor, o))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<Order> getOrdersForVendorPage(Vendor vendor, int page, int size) {
        if (size > 100) {
            size = 100;
        }
        if (size < 1) {
            size = 20;
        }
        if (page < 0) {
            page = 0;
        }
        return orderRepository.findPageForVendor(vendor.getId(), PageRequest.of(page, size));
    }

    @Transactional(readOnly = true)
    public Page<VendorOrderListItemDto> getOrderListItemPage(Vendor vendor, int page, int size) {
        Page<Order> o = getOrdersForVendorPage(vendor, page, size);
        return o.map(order -> toListItem(vendor, order));
    }

    private static VendorOrderListItemDto toListItem(Vendor vendor, Order order) {
        List<OrderItem> lines = order.getItems() == null ? List.of() : order.getItems().stream()
                .filter(i -> Objects.equals(i.getVendorId(), vendor.getId()))
                .toList();
        BigDecimal lineTotal = lines.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new VendorOrderListItemDto(
                order.getId(),
                order.getOrderNumber(),
                order.getCreatedAt(),
                order.getStatus().name(),
                lineTotal,
                lines.size()
        );
    }
}
