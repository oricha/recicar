package com.recicar.marketplace.dto;

import com.recicar.marketplace.entity.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * List row for a customer's order history.
 */
public record OrderSummaryResponse(
        Long id,
        String orderNumber,
        Order.OrderStatus status,
        BigDecimal totalAmount,
        LocalDateTime createdAt
) { }
