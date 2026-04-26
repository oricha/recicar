package com.recicar.marketplace.controller;

import com.recicar.marketplace.config.CompanyInfoProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Public company facts for client apps and SEO consumers.
 */
@RestController
@RequestMapping("/api/v1/company")
public class CompanyPublicApiController {

    private final CompanyInfoProperties company;

    public CompanyPublicApiController(CompanyInfoProperties company) {
        this.company = company;
    }

    @GetMapping("/public")
    public ResponseEntity<Map<String, Object>> publicInfo() {
        return ResponseEntity.ok(Map.of(
                "displayName", company.getDisplayName(),
                "legalName", company.getLegalName(),
                "copyrightLine", company.getCopyrightLine(),
                "tagline", company.getTagline(),
                "address", company.getAddress(),
                "phone", company.getPhone() != null ? company.getPhone() : "",
                "social", Map.of(
                        "facebook", nvl(company.getSocial().getFacebook()),
                        "youtube", nvl(company.getSocial().getYoutube()),
                        "instagram", nvl(company.getSocial().getInstagram())
                )
        ));
    }

    private static String nvl(String s) {
        return s != null ? s : "";
    }
}
