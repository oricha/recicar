package com.recicar.marketplace.service;

import com.recicar.marketplace.entity.Payment;
import com.recicar.marketplace.entity.Order;
import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    public Payment processPayment(Order order, String paymentMethod) {
        Payment payment = order.getPayment();
        // Simulate payment processing
        if ("creditCard".equals(paymentMethod) || "paypal".equals(paymentMethod)) {
            payment.setStatus(Payment.PaymentStatus.COMPLETED);
        } else {
            payment.setStatus(Payment.PaymentStatus.FAILED);
        }
        return payment;
    }
}
