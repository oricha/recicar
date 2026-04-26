package com.recicar.marketplace.dto;

/**
 * Open or reuse a customer–vendor thread.
 */
public class StartConversationRequest {
    private Long vendorId;
    private Long productId;

    public Long getVendorId() {
        return vendorId;
    }

    public void setVendorId(Long vendorId) {
        this.vendorId = vendorId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }
}
