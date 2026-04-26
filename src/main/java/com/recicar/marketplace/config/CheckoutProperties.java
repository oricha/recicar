package com.recicar.marketplace.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;

/**
 * Tax and marketplace fee for cart/checkout. Prices are treated as ex-VAT.
 */
@ConfigurationProperties("app.checkout")
public class CheckoutProperties {

    /**
     * VAT as decimal (e.g. 0.21 = 21%).
     */
    private BigDecimal vatRate = new BigDecimal("0.21");

    /**
     * Service fee on merchandise subtotal (e.g. 0.02 = 2%).
     */
    private BigDecimal serviceFeeRate = new BigDecimal("0.02");

    public BigDecimal getVatRate() {
        return vatRate;
    }

    public void setVatRate(BigDecimal vatRate) {
        this.vatRate = vatRate;
    }

    public BigDecimal getServiceFeeRate() {
        return serviceFeeRate;
    }

    public void setServiceFeeRate(BigDecimal serviceFeeRate) {
        this.serviceFeeRate = serviceFeeRate;
    }
}
