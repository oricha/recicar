package com.recicar.marketplace.controller;

import com.recicar.marketplace.dto.NewsletterSubscribeRequest;
import com.recicar.marketplace.service.NewsletterSubscriptionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/newsletter")
public class NewsletterApiController {

    private final NewsletterSubscriptionService newsletterSubscriptionService;

    public NewsletterApiController(NewsletterSubscriptionService newsletterSubscriptionService) {
        this.newsletterSubscriptionService = newsletterSubscriptionService;
    }

    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@Valid @RequestBody NewsletterSubscribeRequest request) {
        try {
            boolean created = newsletterSubscriptionService.subscribeOrAcknowledge(
                    request.getEmail(), "api");
            if (created) {
                return ResponseEntity.status(HttpStatus.CREATED)
                        .body(Map.of("message", "Suscrito correctamente"));
            }
            return ResponseEntity.ok(Map.of("message", "Ese correo ya estaba inscrito"));
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage());
        }
    }
}
