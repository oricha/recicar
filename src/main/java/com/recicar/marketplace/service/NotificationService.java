package com.recicar.marketplace.service;

import com.recicar.marketplace.entity.Order;
import com.recicar.marketplace.entity.User;
import org.springframework.stereotype.Service;

@Service
public interface NotificationService {

    void sendOrderConfirmationEmail(Order order);

    void sendAccountVerificationEmail(User user);

    void sendPasswordResetEmail(User user, String resetLink);

    /**
     * Forwards a public contact form submission to the configured support inbox (logged until SMTP is integrated).
     */
    void sendSupportContactInquiry(String fromName, String fromEmail, String subject, String body, String toAddress);

    // Other notification methods
}
