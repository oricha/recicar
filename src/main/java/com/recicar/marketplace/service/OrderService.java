package com.recicar.marketplace.service;

import com.recicar.marketplace.dto.OrderItemRequest;
import com.recicar.marketplace.dto.OrderRequest;
import com.recicar.marketplace.entity.Order;

import java.util.List;

public interface OrderService {

    Order createOrder(OrderRequest orderRequest);

    List<OrderItemRequest> convertCartItemsToOrderItems(List<com.recicar.marketplace.dto.CartItemDto> cartItems);

    // Other methods for order management
}
