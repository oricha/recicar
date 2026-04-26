package com.recicar.marketplace.controller;

import com.recicar.marketplace.service.NewsletterSubscriptionService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class NewsletterController {

    private final NewsletterSubscriptionService newsletterSubscriptionService;

    public NewsletterController(NewsletterSubscriptionService newsletterSubscriptionService) {
        this.newsletterSubscriptionService = newsletterSubscriptionService;
    }

    /**
     * Form POST from site footer; redirects to referer or home with flash.
     */
    @PostMapping("/newsletter/subscribe")
    public String subscribeForm(
            @RequestParam("email") String email,
            @RequestParam(value = "next", required = false) String next,
            RedirectAttributes redirectAttributes) {
        try {
            boolean created = newsletterSubscriptionService.subscribeOrAcknowledge(email, "footer");
            if (created) {
                redirectAttributes.addFlashAttribute("success", "Gracias. Te has suscrito al boletín.");
            } else {
                redirectAttributes.addFlashAttribute("message", "Ese correo ya estaba inscrito. ¡Gracias por tu interés!");
            }
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        if (next != null && !next.isBlank() && (next.startsWith("/") && !next.startsWith("//"))) {
            return "redirect:" + next;
        }
        return "redirect:/";
    }
}
