package com.recicar.marketplace.dto;

import com.recicar.marketplace.entity.ProductCondition;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class ProductRequest {
    private Long id;
    private Long vendorId;
    private Long categoryId;
    private String name;
    private String description;
    private BigDecimal price;
    private String partNumber;
    private String oemNumber;
    private ProductCondition condition;
    private Integer stockQuantity;
    private BigDecimal weightKg;
    private boolean active;
    private List<ProductImageRequest> images;
    private List<VehicleCompatibilityRequest> compatibilities;
}
