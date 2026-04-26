package com.recicar.marketplace.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartDto {

    private Long id;
    private Long userId;
    private List<CartItemDto> items;
    /** Sum of line totals (ex-VAT). */
    private BigDecimal subtotal;
    /** Marketplace fee (ex-VAT) on subtotal. */
    private BigDecimal serviceFee;
    private BigDecimal vatRate;
    private BigDecimal vatAmount;
    private BigDecimal shippingAmount;
    private BigDecimal totalAmount;
    /** When DPD is applicable (EU). */
    private String shippingCarrierLabel;
}
