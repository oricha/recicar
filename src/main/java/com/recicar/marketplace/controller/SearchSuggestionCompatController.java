package com.recicar.marketplace.controller;

import com.recicar.marketplace.service.SearchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Legacy path used by some templates: {@code /api/search/suggestions}
 * (v1 is {@code /api/v1/search/suggestions}).
 */
@RestController
public class SearchSuggestionCompatController {

    private final SearchService searchService;

    public SearchSuggestionCompatController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping("/api/search/suggestions")
    public ResponseEntity<List<String>> suggest(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "query", required = false) String query) {
        String t = (q != null && !q.isBlank()) ? q : (query != null && !query.isBlank() ? query : "");
        return ResponseEntity.ok(searchService.getSearchSuggestions(t));
    }
}
