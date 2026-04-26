package com.recicar.marketplace.service;

import com.recicar.marketplace.entity.NewsletterSubscriber;
import com.recicar.marketplace.repository.NewsletterSubscriberRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NewsletterSubscriptionService {

    private static final Logger log = LoggerFactory.getLogger(NewsletterSubscriptionService.class);

    private final NewsletterSubscriberRepository newsletterSubscriberRepository;

    public NewsletterSubscriptionService(NewsletterSubscriberRepository newsletterSubscriberRepository) {
        this.newsletterSubscriberRepository = newsletterSubscriberRepository;
    }

    /**
     * @return true if a new row was created, false if email was already registered
     */
    @Transactional
    public boolean subscribeOrAcknowledge(String email, String source) {
        if (email == null) {
            throw new IllegalArgumentException("Email requerido");
        }
        String lower = email.trim().toLowerCase();
        if (lower.isEmpty()) {
            throw new IllegalArgumentException("Email requerido");
        }
        if (newsletterSubscriberRepository.existsByEmailIgnoreCase(lower)) {
            log.debug("Newsletter: email already subscribed: {}", lower);
            return false;
        }
        NewsletterSubscriber s = new NewsletterSubscriber();
        s.setEmail(lower);
        s.setSource(source != null && !source.isBlank() ? source : "unknown");
        newsletterSubscriberRepository.save(s);
        log.info("Newsletter subscription: source={} email={} ", s.getSource(), lower);
        return true;
    }
}
