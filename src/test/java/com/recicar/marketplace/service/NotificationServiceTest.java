package com.recicar.marketplace.service;

import com.recicar.marketplace.entity.Order;
import com.recicar.marketplace.entity.Payment;
import com.recicar.marketplace.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.verify;

public class NotificationServiceTest {

    @InjectMocks
    private NotificationServiceImpl notificationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSendOrderConfirmationEmail() {
        Order order = new Order();
        order.setOrderNumber("ORD-123");
        order.setCustomer(new User());
        order.getCustomer().setEmail("test@example.com");
        order.setTotalAmount(new BigDecimal("100.00"));
        order.setPayment(new Payment());
        order.getPayment().setPaymentMethod("Credit Card");

        notificationService.sendOrderConfirmationEmail(order);
        // Verify that the email sending logic is triggered (e.g., by checking logs or mocking external email client)
        // For this simulation, we just check if the method runs without error
    }

    @Test
    public void testSendAccountVerificationEmail() {
        User user = new User();
        user.setEmail("test@example.com");

        notificationService.sendAccountVerificationEmail(user);
        // Verify that the email sending logic is triggered
    }
}
