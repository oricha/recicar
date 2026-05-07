package com.recicar.marketplace.controller;

import com.recicar.marketplace.dto.SavedSearchListRow;
import com.recicar.marketplace.entity.SavedSearch;
import com.recicar.marketplace.repository.UserRepository;
import com.recicar.marketplace.service.SearchService;
import com.recicar.marketplace.util.SavedSearchNavigation;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class SavedSearchesPageController {

    private final UserRepository userRepository;
    private final SearchService searchService;

    public SavedSearchesPageController(UserRepository userRepository, SearchService searchService) {
        this.userRepository = userRepository;
        this.searchService = searchService;
    }

    @GetMapping("/saved-searches")
    public String savedSearchesPage(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        return userRepository.findByEmailIgnoreCase(userDetails.getUsername())
                .map(user -> {
                    List<SavedSearchListRow> rows = searchService.getSavedSearches(user.getId()).stream()
                            .map(SavedSearchesPageController::toRow)
                            .toList();
                    model.addAttribute("pageTitle", "Búsquedas guardadas — ReciCar");
                    model.addAttribute("savedRows", rows);
                    return "saved-searches";
                })
                .orElse("redirect:/login");
    }

    private static SavedSearchListRow toRow(SavedSearch s) {
        String label = labelFor(s);
        return new SavedSearchListRow(s.getId(), label, SavedSearchNavigation.toAdvancedSearchUrl(s), s.getCreatedAt());
    }

    private static String labelFor(SavedSearch s) {
        if (s.getSearchQuery() != null && !s.getSearchQuery().isBlank()) {
            return s.getSearchQuery().trim();
        }
        return "Búsqueda #" + s.getId();
    }
}
