package com.recicar.marketplace.service;

import com.recicar.marketplace.dto.CartDto;
import java.math.BigDecimal;

public interface CartService {

    CartDto getCart(Long userId);

    CartDto addItemToCart(Long userId, Long productId, int quantity);

    CartDto updateItemInCart(Long userId, Long itemId, int quantity);

    void removeItemFromCart(Long userId, Long itemId);

    void clearCart(Long userId);

    void validateCart(Long userId);

    BigDecimal calculateShippingCost(Long userId, String country, String state, String zipCode);
}
