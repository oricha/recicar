package com.recicar.marketplace.entity;

/**
 * Supported checkout payment brands / rails (placeholders; PSP integration is out of scope here).
 */
public enum PaymentMethodOption {
    PAYPAL,
    VISA,
    MASTERCARD,
    MAESTRO,
    PAYSERA,
    BANK_TRANSFER
}
