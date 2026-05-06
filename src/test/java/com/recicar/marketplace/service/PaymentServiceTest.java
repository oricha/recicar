package com.recicar.marketplace.service;

import com.recicar.marketplace.entity.Order;
import com.recicar.marketplace.entity.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaymentServiceTest {

    private PaymentService paymentService;

    @BeforeEach
    void setUp() {
        paymentService = new PaymentService();
    }

    @Test
    void processPayment_marksVisaAsCompleted() {
        Payment processed = paymentService.processPayment(orderWithPendingPayment(), "VISA");
        assertEquals(Payment.PaymentStatus.COMPLETED, processed.getStatus());
    }

    @Test
    void processPayment_marksPaypalAsCompleted() {
        Payment processed = paymentService.processPayment(orderWithPendingPayment(), "paypal");
        assertEquals(Payment.PaymentStatus.COMPLETED, processed.getStatus());
    }

    @Test
    void processPayment_keepsBankTransferPending() {
        Payment processed = paymentService.processPayment(orderWithPendingPayment(), "BANK_TRANSFER");
        assertEquals(Payment.PaymentStatus.PENDING, processed.getStatus());
    }

    @Test
    void processPayment_fallsBackToPendingForUnsupportedMethod() {
        Payment processed = paymentService.processPayment(orderWithPendingPayment(), "unsupported");
        assertEquals(Payment.PaymentStatus.PENDING, processed.getStatus());
    }

    private static Order orderWithPendingPayment() {
        Order order = new Order();
        Payment payment = new Payment();
        payment.setAmount(new BigDecimal("100.00"));
        payment.setCreatedAt(LocalDateTime.now());
        payment.setStatus(Payment.PaymentStatus.PENDING);
        order.setPayment(payment);
        return order;
    }
}
