package com.recicar.marketplace.controller;

import com.recicar.marketplace.dto.BlogPostSummaryDto;
import com.recicar.marketplace.dto.ContactRequest;
import com.recicar.marketplace.entity.BlogPost;
import com.recicar.marketplace.service.ContactMessageService;
import com.recicar.marketplace.service.SupportContentService;
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
            @RequestParam(defaultValue = "0") int page) {
        Page<BlogPostSummaryDto> posts = supportContentService.listPublishedBlogSummaries(page, 9);
        model.addAttribute("posts", posts);
        model.addAttribute("pageTitle", "Blog");
        return "support/blog-list";
    }

    @GetMapping("/blog/{slug}")
    public String blogPost(@PathVariable String slug, Model model) {
        BlogPost post = supportContentService.findPublishedPostBySlug(slug)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Artículo no encontrado"));
        model.addAttribute("post", post);
        model.addAttribute("pageTitle", post.getTitle());
        return "support/blog-post";
    }

    @GetMapping({"/blog-details", "/blog-fullwidth", "/blog-sidebar"})
    public String legacyBlogRoutes() {
        return "redirect:/blog";
    }
}
