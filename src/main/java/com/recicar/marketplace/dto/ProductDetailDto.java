package com.recicar.marketplace.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProductDetailDto {

    private Long id;
    private String sku;
    private String title;
    private String description;
    private BigDecimal price;
    private String condition;
    private Integer stockQuantity;
    private String sellerName;
    private String sellerContact;
    private List<String> productImageUrls = new ArrayList<>();
    private List<String> vehiclePhotoUrls = new ArrayList<>();
    private Map<String, String> productSpecs;
    private Map<String, String> vehicleSpecs;
    private List<ProductCardDto> relatedParts = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSellerContact() {
        return sellerContact;
    }

    public void setSellerContact(String sellerContact) {
        this.sellerContact = sellerContact;
    }

    public List<String> getProductImageUrls() {
        return productImageUrls;
    }

    public void setProductImageUrls(List<String> productImageUrls) {
        this.productImageUrls = productImageUrls;
    }

    public List<String> getVehiclePhotoUrls() {
        return vehiclePhotoUrls;
    }

    public void setVehiclePhotoUrls(List<String> vehiclePhotoUrls) {
        this.vehiclePhotoUrls = vehiclePhotoUrls;
    }

    public Map<String, String> getProductSpecs() {
        return productSpecs;
    }

    public void setProductSpecs(Map<String, String> productSpecs) {
        this.productSpecs = productSpecs;
    }

    public Map<String, String> getVehicleSpecs() {
        return vehicleSpecs;
    }

    public void setVehicleSpecs(Map<String, String> vehicleSpecs) {
        this.vehicleSpecs = vehicleSpecs;
    }

    public List<ProductCardDto> getRelatedParts() {
        return relatedParts;
    }

    public void setRelatedParts(List<ProductCardDto> relatedParts) {
        this.relatedParts = relatedParts;
    }
}
