package com.recicar.marketplace.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CartItemDto {

    private Long id;
    private Long productId;
    private String productName;
    private int quantity;
    private BigDecimal price;
    private String imageUrl;
}
