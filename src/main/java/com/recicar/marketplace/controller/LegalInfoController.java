package com.recicar.marketplace.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Páginas informativas: envíos, pagos, devoluciones, términos, privacidad, mensajería, garantías.
 * Contenido estático; sin persistencia.
 */
@Controller
public class LegalInfoController {

    @GetMapping("/info-de-envio")
    public String infoEnvio(Model model) {
        model.addAttribute("pageTitle", "Información de envío");
        return "info-de-envio";
    }

    @GetMapping("/info-de-pago")
    public String infoPago(Model model) {
        model.addAttribute("pageTitle", "Información de pago");
        return "info-de-pago";
    }

    @GetMapping("/politica-de-devolucion")
    public String politicaDevolucion(Model model) {
        model.addAttribute("pageTitle", "Política de devoluciones");
        return "politica-de-devolucion";
    }

    @GetMapping("/terminos-de-uso")
    public String terminosDeUso(Model model) {
        model.addAttribute("pageTitle", "Términos de uso");
        return "terminos-de-uso";
    }

    @GetMapping({"/politica-de-privacidad", "/privacy-policy"})
    public String politicaPrivacidad(Model model) {
        model.addAttribute("pageTitle", "Política de privacidad");
        return "politica-de-privacidad";
    }

    @GetMapping("/politica-de-comunicacion-por-chat")
    public String politicaChat(Model model) {
        model.addAttribute("pageTitle", "Política de comunicación por chat");
        return "politica-de-comunicacion-por-chat";
    }

    @GetMapping({"/garantias", "/warranty"})
    public String garantias(Model model) {
        model.addAttribute("pageTitle", "Garantías");
        return "garantias";
    }
}
