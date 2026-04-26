package com.recicar.marketplace.service;

import com.recicar.marketplace.dto.OrderItemRequest;
import com.recicar.marketplace.dto.OrderRequest;
import com.recicar.marketplace.entity.Order;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {

    Order createOrder(OrderRequest orderRequest);

    List<OrderItemRequest> convertCartItemsToOrderItems(List<com.recicar.marketplace.dto.CartItemDto> cartItems);

    /**
     * Orders for a customer, newest first.
     */
    List<Order> findOrdersByCustomerId(Long customerId, Pageable pageable);
}
