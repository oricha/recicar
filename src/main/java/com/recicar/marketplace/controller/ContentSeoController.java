package com.recicar.marketplace.controller;

import com.recicar.marketplace.service.PartCodeReferenceService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

/**
 * Content marketing & SEO tool pages (OEM / brand list, tire calculator).
 */
@Controller
public class ContentSeoController {

    private final PartCodeReferenceService partCodeReferenceService;

    public ContentSeoController(PartCodeReferenceService partCodeReferenceService) {
        this.partCodeReferenceService = partCodeReferenceService;
    }

    @GetMapping("/sitemap")
    public String sitemapAlias() {
        return "redirect:/sitemap.xml";
    }

    @GetMapping("/lista-de-codigos-de-repuestos")
    public String partCodesList(Model model, HttpServletRequest request) {
        model.addAttribute("pageTitle", "Lista de códigos y referencias de repuestos");
        model.addAttribute("metaDescription",
                "Guía básica de referencias OEM, marcas de equipo y buenas prácticas al buscar recambio.");
        model.addAttribute("partCodes", partCodeReferenceService.listAll());
        model.addAttribute("canonicalUrl", ServletUriComponentsBuilder.fromRequest(request).toUriString());
        return "content-seo/lista-codigos-repuestos";
    }

    @GetMapping("/equivalencia-neumaticos")
    public String tireEquivalence(Model model, HttpServletRequest request) {
        model.addAttribute("pageTitle", "Equivalencia y medida de neumáticos");
        model.addAttribute("metaDescription",
                "Herramienta educativa: leer el flanco del neumático (ETRTO) y calculadora de diámetro exterior aproximado.");
        model.addAttribute("canonicalUrl", ServletUriComponentsBuilder.fromRequest(request).toUriString());
        return "content-seo/equivalencia-neumaticos";
    }
}
