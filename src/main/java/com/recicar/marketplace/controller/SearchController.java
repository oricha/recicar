package com.recicar.marketplace.controller;

import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    @GetMapping("/search/vehicle")
    public String searchByMakeModelEngineAndPart(
            @RequestParam("make") String make,
            @RequestParam("model") String model,
            @RequestParam("engineType") String engineType,
            @RequestParam(value = "partName", required = false) String partName,
            @RequestParam(value = "page", defaultValue = "0") int page,
            Model modelAttr
    ) {
        if (make == null || make.isBlank() || model == null || model.isBlank() || engineType == null || engineType.isBlank()) {
            modelAttr.addAttribute("errorMessage", "Make, Model and Engine Type are required");
            return "shop-list";
        }
        Page<Product> productPage = productService.findByMakeModelEngineAndPartName(make.trim(), model.trim(), engineType.trim(), partName, PageRequest.of(page, 12));
        modelAttr.addAttribute("products", productPage.getContent());
        modelAttr.addAttribute("page", productPage);
        modelAttr.addAttribute("vehicleMake", make);
        modelAttr.addAttribute("vehicleModel", model);
        modelAttr.addAttribute("vehicleEngine", engineType);
        modelAttr.addAttribute("partName", partName);
        return "shop-list";
    }
}
