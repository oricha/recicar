package com.recicar.marketplace.service;

import com.recicar.marketplace.entity.Order;
import com.recicar.marketplace.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationServiceImpl implements NotificationService {

    @Override
    public void sendOrderConfirmationEmail(Order order) {
        log.info("Simulating sending order confirmation email for order: {}", order.getOrderNumber());
        // In a real application, integrate with an email service provider here
    }

    @Override
    public void sendAccountVerificationEmail(User user) {
        log.info("Simulating sending account verification email to user: {}", user.getEmail());
        // In a real application, integrate with an email service provider here
    }

    @Override
    public void sendPasswordResetEmail(User user, String resetLink) {
        log.info("Simulating sending password reset email to: {}, link: {}", user.getEmail(), resetLink);
        // In a real application, integrate with an email service provider here
    }
}
