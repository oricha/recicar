package com.recicar.marketplace.service;

import com.recicar.marketplace.entity.Order;
import com.recicar.marketplace.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface NotificationService {

    void sendOrderConfirmationEmail(Order order);

    void sendAccountVerificationEmail(User user);

    void sendPasswordResetEmail(User user, String resetLink);

    // Other notification methods
}
