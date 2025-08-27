package com.recicar.marketplace.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartDto {

    private Long id;
    private Long userId;
    private List<CartItemDto> items;
    private BigDecimal subtotal;
}
