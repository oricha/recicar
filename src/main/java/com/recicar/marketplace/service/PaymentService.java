package com.recicar.marketplace.service;

import com.recicar.marketplace.entity.Payment;
import com.recicar.marketplace.entity.PaymentMethodOption;
import com.recicar.marketplace.entity.Order;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    public Payment processPayment(Order order, String paymentMethod) {
        Payment payment = order.getPayment();
        String m = paymentMethod == null ? "" : paymentMethod.trim();
        if (m.isEmpty()) {
            payment.setStatus(Payment.PaymentStatus.PENDING);
            return payment;
        }
        try {
            PaymentMethodOption option = PaymentMethodOption.valueOf(m.toUpperCase());
            if (option == PaymentMethodOption.BANK_TRANSFER) {
                payment.setStatus(Payment.PaymentStatus.PENDING);
            } else {
                payment.setStatus(Payment.PaymentStatus.COMPLETED);
            }
        } catch (IllegalArgumentException e) {
            // Backwards compatibility (creditCard, paypal, etc.)
            if (m.toLowerCase().contains("card")
                    || m.toLowerCase().contains("paypal")
                    || m.toLowerCase().contains("paysera")) {
                payment.setStatus(Payment.PaymentStatus.COMPLETED);
            } else if (m.toLowerCase().contains("bank") || m.toLowerCase().contains("transfer")) {
                payment.setStatus(Payment.PaymentStatus.PENDING);
            } else {
                payment.setStatus(Payment.PaymentStatus.PENDING);
            }
        }
        return payment;
    }
}
