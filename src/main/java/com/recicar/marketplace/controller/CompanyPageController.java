package com.recicar.marketplace.controller;

import com.recicar.marketplace.config.CompanyInfoProperties;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Public “about the company” pages (trust & transparency).
 */
@Controller
public class CompanyPageController {

    private final CompanyInfoProperties companyInfo;

    public CompanyPageController(CompanyInfoProperties companyInfo) {
        this.companyInfo = companyInfo;
    }

    @GetMapping("/acerca-de-nosotros")
    public String aboutUs(Model model, HttpServletRequest request) {
        model.addAttribute("pageTitle", "Acerca de " + companyInfo.getDisplayName());
        model.addAttribute("metaDescription",
                "Conoce la misión de " + companyInfo.getDisplayName() + " y nuestro compromiso con el recambio de calidad.");
        model.addAttribute("canonicalUrl", ServletUriComponentsBuilder.fromRequest(request).toUriString());
        return "company/acerca-de-nosotros";
    }

    @GetMapping("/about")
    public String aboutLegacy() {
        return "redirect:/acerca-de-nosotros";
    }
}
