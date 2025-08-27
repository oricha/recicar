package com.recicar.marketplace.service;

import com.recicar.marketplace.entity.Order;
import com.recicar.marketplace.entity.Payment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testProcessPayment_creditCard_success() {
        Order order = new Order();
        Payment payment = new Payment();
        payment.setAmount(new BigDecimal("100.00"));
        payment.setCreatedAt(LocalDateTime.now());
        payment.setStatus(Payment.PaymentStatus.PENDING);
        order.setPayment(payment);

        Payment processedPayment = paymentService.processPayment(order, "creditCard");

        assertEquals(Payment.PaymentStatus.COMPLETED, processedPayment.getStatus());
    }

    @Test
    public void testProcessPayment_paypal_success() {
        Order order = new Order();
        Payment payment = new Payment();
        payment.setAmount(new BigDecimal("100.00"));
        payment.setCreatedAt(LocalDateTime.now());
        payment.setStatus(Payment.PaymentStatus.PENDING);
        order.setPayment(payment);

        Payment processedPayment = paymentService.processPayment(order, "paypal");

        assertEquals(Payment.PaymentStatus.COMPLETED, processedPayment.getStatus());
    }

    @Test
    public void testProcessPayment_otherMethod_failure() {
        Order order = new Order();
        Payment payment = new Payment();
        payment.setAmount(new BigDecimal("100.00"));
        payment.setCreatedAt(LocalDateTime.now());
        payment.setStatus(Payment.PaymentStatus.PENDING);
        order.setPayment(payment);

        Payment processedPayment = paymentService.processPayment(order, "unsupported");

        assertEquals(Payment.PaymentStatus.FAILED, processedPayment.getStatus());
    }
}
