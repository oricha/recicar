package com.recicar.marketplace.dto;

public class StoreInfoUpdateRequest {
    private String storeName;
    private String storePhone;
    private String storeEmail;

    public StoreInfoUpdateRequest() {}

    public StoreInfoUpdateRequest(String storeName, String storePhone, String storeEmail) {
        this.storeName = storeName;
        this.storePhone = storePhone;
        this.storeEmail = storeEmail;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStorePhone() {
        return storePhone;
    }

    public void setStorePhone(String storePhone) {
        this.storePhone = storePhone;
    }

    public String getStoreEmail() {
        return storeEmail;
    }

    public void setStoreEmail(String storeEmail) {
        this.storeEmail = storeEmail;
    }
}
