package com.recicar.marketplace.dto;

import java.math.BigDecimal;

public class ProductCardDto {

    private Long id;
    private String name;
    private String partNumber;
    private BigDecimal price;
    private String condition;
    private boolean inStock;
    private String imageUrl;
    private String sellerName;
    private boolean topSeller;
    private BigDecimal sellerRating;
    private BigDecimal serviceFeePercent;
    private BigDecimal serviceFeeMin;
    private BigDecimal serviceFeeMax;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(String partNumber) {
        this.partNumber = partNumber;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public boolean isInStock() {
        return inStock;
    }

    public void setInStock(boolean inStock) {
        this.inStock = inStock;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public boolean isTopSeller() {
        return topSeller;
    }

    public void setTopSeller(boolean topSeller) {
        this.topSeller = topSeller;
    }

    public BigDecimal getSellerRating() {
        return sellerRating;
    }

    public void setSellerRating(BigDecimal sellerRating) {
        this.sellerRating = sellerRating;
    }

    public BigDecimal getServiceFeePercent() {
        return serviceFeePercent;
    }

    public void setServiceFeePercent(BigDecimal serviceFeePercent) {
        this.serviceFeePercent = serviceFeePercent;
    }

    public BigDecimal getServiceFeeMin() {
        return serviceFeeMin;
    }

    public void setServiceFeeMin(BigDecimal serviceFeeMin) {
        this.serviceFeeMin = serviceFeeMin;
    }

    public BigDecimal getServiceFeeMax() {
        return serviceFeeMax;
    }

    public void setServiceFeeMax(BigDecimal serviceFeeMax) {
        this.serviceFeeMax = serviceFeeMax;
    }
}
