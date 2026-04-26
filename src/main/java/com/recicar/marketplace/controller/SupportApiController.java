package com.recicar.marketplace.controller;

import com.recicar.marketplace.dto.BlogPostSummaryDto;
import com.recicar.marketplace.dto.FaqCategoryDto;
import com.recicar.marketplace.service.SupportContentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Public JSON API for help content (FAQs, blog listing).
 */
@RestController
@RequestMapping("/api/v1/support")
public class SupportApiController {

    private final SupportContentService supportContentService;
    private final String supportEmail;

    public SupportApiController(
            SupportContentService supportContentService,
            @Value("${app.support.email:help@ovoko.es}") String supportEmail) {
        this.supportContentService = supportContentService;
        this.supportEmail = supportEmail;
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> supportInfo() {
        return ResponseEntity.ok(Map.of("supportEmail", supportEmail));
    }

    @GetMapping("/faqs")
    public ResponseEntity<List<FaqCategoryDto>> listFaqs() {
        return ResponseEntity.ok(supportContentService.listFaqCategories());
    }

    @GetMapping("/blog")
    public ResponseEntity<List<BlogPostSummaryDto>> listBlog(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<BlogPostSummaryDto> p = supportContentService.listPublishedBlogSummaries(page, size);
        return ResponseEntity.ok(p.getContent());
    }
}
