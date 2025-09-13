package com.recicar.marketplace.service;

import com.recicar.marketplace.entity.Order;
import com.recicar.marketplace.entity.User;
import org.springframework.stereotype.Service;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Override
    public void sendOrderConfirmationEmail(Order order) {
        System.out.println("Simulating sending order confirmation email for order: " + order.getOrderNumber());
        // In a real application, integrate with an email service provider here
    }

    @Override
    public void sendAccountVerificationEmail(User user) {
        System.out.println("Simulating sending account verification email to user: " + user.getEmail());
        // In a real application, integrate with an email service provider here
    }

    @Override
    public void sendPasswordResetEmail(User user, String resetLink) {
        System.out.println("Simulating sending password reset email to: " + user.getEmail() + ", link: " + resetLink);
        // In a real application, integrate with an email service provider here
    }
}
