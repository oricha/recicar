package com.recicar.marketplace.controller;

import com.recicar.marketplace.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class SearchController {

    private final ProductService productService;

    @GetMapping("/search")
    public String search(@RequestParam("query") String query, Model model) {
        // Simple check to differentiate between part number, OEM number, and general search
        if (query.matches("[a-zA-Z0-9\\-]{5,}")) { // Example regex for part/OEM number
            List<?> products = productService.findByPartNumber(query);
            if (products == null || products.isEmpty()) {
                products = productService.findByOemNumber(query);
            }
            model.addAttribute("products", products);
        } else {
            model.addAttribute("products", productService.searchProducts(query, org.springframework.data.domain.PageRequest.of(0, 12)).getContent());
        }
        model.addAttribute("searchQuery", query);
        return "shop-list";
    }
}
