package com.recicar.marketplace.controller;

import com.recicar.marketplace.dto.BlogPostSummaryDto;
import com.recicar.marketplace.dto.ContactRequest;
import com.recicar.marketplace.entity.BlogPost;
import com.recicar.marketplace.service.ContactMessageService;
import com.recicar.marketplace.service.SupportContentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Controller
public class HelpSupportController {

    private final SupportContentService supportContentService;
    private final ContactMessageService contactMessageService;
    private final String supportInbox;

    public HelpSupportController(
            SupportContentService supportContentService,
            ContactMessageService contactMessageService,
            @Value("${app.support.email:help@ovoko.es}") String supportInbox) {
        this.supportContentService = supportContentService;
        this.contactMessageService = contactMessageService;
        this.supportInbox = supportInbox;
    }

    @GetMapping("/help")
    public String helpCenter(Model model) {
        model.addAttribute("pageTitle", "Centro de ayuda");
        return "support/help-center";
    }

    @GetMapping("/faq")
    public String faq(Model model) {
        model.addAttribute("pageTitle", "Preguntas frecuentes");
        model.addAttribute("categories", supportContentService.listFaqCategories());
        return "support/faq";
    }

    @GetMapping({"/contact", "/contactos"})
    public String contactForm(Model model) {
        if (!model.containsAttribute("contactRequest")) {
            model.addAttribute("contactRequest", new ContactRequest());
        }
        model.addAttribute("pageTitle", "Contacto");
        return "support/contact";
    }

    @PostMapping({"/contact", "/contactos"})
    public String contactSubmit(
            @Valid @ModelAttribute("contactRequest") ContactRequest contactRequest,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("pageTitle", "Contacto");
            return "support/contact";
        }
        contactMessageService.submit(contactRequest, supportInbox);
        redirectAttributes.addFlashAttribute("success", "Gracias. Hemos recibido tu mensaje. Te responderemos pronto.");
        return "redirect:/contact";
    }

    @GetMapping("/blog")
    public String blogList(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            HttpServletRequest request) {
        Page<BlogPostSummaryDto> posts = supportContentService.listPublishedBlogSummaries(page, 9);
        model.addAttribute("posts", posts);
        model.addAttribute("pageTitle", "Blog — ReciCar");
        model.addAttribute("metaDescription", "Consejos y guías sobre recambio de coche, referencias y mantenimiento.");
        model.addAttribute("canonicalUrl", ServletUriComponentsBuilder.fromRequest(request).toUriString());
        return "support/blog-list";
    }

    @GetMapping("/blog/{slug}")
    public String blogPost(@PathVariable String slug, Model model, HttpServletRequest request) {
        BlogPost post = supportContentService.findPublishedPostBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artículo no encontrado"));
        String canonical = ServletUriComponentsBuilder.fromRequest(request).toUriString();
        String meta = post.getMetaDescription();
        if (meta == null || meta.isBlank()) {
            meta = post.getSummary();
        }
        model.addAttribute("post", post);
        model.addAttribute("pageTitle", post.getTitle());
        model.addAttribute("metaDescription", meta);
        model.addAttribute("canonicalUrl", canonical);
        model.addAttribute("blogPostingJsonLd", toBlogPostingJsonLd(post, canonical));
        return "support/blog-post";
    }

    private static String toBlogPostingJsonLd(BlogPost p, String url) {
        String head = jsonEscape(p.getTitle());
        String desc = p.getMetaDescription() != null && !p.getMetaDescription().isBlank()
                ? p.getMetaDescription() : p.getSummary();
        if (desc == null) {
            desc = "";
        } else {
            desc = jsonEscape(desc);
        }
        return "{\"@context\":\"https://schema.org\",\"@type\":\"BlogPosting\","
                + "\"headline\":\"" + head + "\","
                + "\"datePublished\":\"" + p.getPublishedAt().toString() + "\","
                + "\"description\":\"" + desc + "\","
                + "\"url\":\"" + jsonEscape(url) + "\"}";
    }

    private static String jsonEscape(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", " ")
                .replace("\r", "");
    }

    @GetMapping({"/blog-details", "/blog-fullwidth", "/blog-sidebar"})
    public String legacyBlogRoutes() {
        return "redirect:/blog";
    }
}
