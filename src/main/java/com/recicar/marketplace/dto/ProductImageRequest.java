package com.recicar.marketplace.dto;

import lombok.Data;

@Data
public class ProductImageRequest {
    private Long id;
    private String imageUrl;
    private boolean primary;
}
