package com.recicar.marketplace.dto;

import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {

    private Long customerId;
    private List<OrderItemRequest> items;
    private ShippingInfoRequest shippingInfo;
    private PaymentRequest payment;

}
