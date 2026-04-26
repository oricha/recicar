package com.recicar.marketplace.controller;

import com.recicar.marketplace.dto.CheckoutForm;
import jakarta.servlet.http.HttpSession;

final class CheckoutSessionSupport {

    static final String SHIPPING = "CHECKOUT_SHIPPING";

    private CheckoutSessionSupport() {
    }

    static CheckoutForm getShippingDraft(HttpSession session) {
        Object o = session.getAttribute(SHIPPING);
        if (o instanceof CheckoutForm f) {
            return f;
        }
        return new CheckoutForm();
    }

    static void setShippingDraft(HttpSession session, CheckoutForm form) {
        session.setAttribute(SHIPPING, form);
    }

    static void clear(HttpSession session) {
        session.removeAttribute(SHIPPING);
    }
}
