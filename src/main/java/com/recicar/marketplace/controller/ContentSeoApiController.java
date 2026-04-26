package com.recicar.marketplace.controller;

import com.recicar.marketplace.entity.PartCodeReference;
import com.recicar.marketplace.service.PartCodeReferenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/content")
public class ContentSeoApiController {

    private final PartCodeReferenceService partCodeReferenceService;

    public ContentSeoApiController(PartCodeReferenceService partCodeReferenceService) {
        this.partCodeReferenceService = partCodeReferenceService;
    }

    @GetMapping("/part-codes")
    public ResponseEntity<List<PartCodeReference>> partCodes() {
        return ResponseEntity.ok(partCodeReferenceService.listAll());
    }
}
