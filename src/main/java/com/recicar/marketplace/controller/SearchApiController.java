package com.recicar.marketplace.controller;

import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.service.CategoryService;
import com.recicar.marketplace.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/products")
@RequiredArgsConstructor
public class SearchApiController {

    private final ProductService productService;
    private final CategoryService categoryService;

    @GetMapping("/search")
    public String searchProducts(@RequestParam("q") String query, @RequestParam(defaultValue = "0") int page, Model model) {
        if (query.isEmpty()) {
            return "redirect:/products";
        }
        if (query.length() < 2) {
            model.addAttribute("errorMessage", "Search term must be at least 2 characters long");
            model.addAttribute("categories", categoryService.findAllActive());
            return "shop-list";
        }
        Page<Product> productPage = productService.searchProducts(query, PageRequest.of(page, 12));
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("page", productPage);
        model.addAttribute("searchQuery", query);
        model.addAttribute("totalElements", productPage.getTotalElements());
        model.addAttribute("categories", categoryService.findAllActive());
        return "shop-list";
    }

    @GetMapping("/part/{partNumber}")
    public String searchByPartNumber(@PathVariable String partNumber, Model model) {
        if (partNumber.length() < 2) {
            model.addAttribute("errorMessage", "Part number must be at least 2 characters long");
            return "products/part-search";
        }
        List<Product> products = productService.findByPartNumber(partNumber);
        model.addAttribute("products", products);
        model.addAttribute("partNumber", partNumber);
        model.addAttribute("searchType", "Part Number");
        return "shop-list";
    }

    @GetMapping("/oem/{oemNumber}")
    public String searchByOemNumber(@PathVariable String oemNumber, Model model) {
        if (oemNumber.length() < 2) {
            model.addAttribute("errorMessage", "OEM number must be at least 2 characters long");
            return "shop-list";
        }
        List<Product> products = productService.findByOemNumber(oemNumber);
        model.addAttribute("products", products);
        model.addAttribute("oemNumber", oemNumber);
        model.addAttribute("searchType", "OEM Number");
        return "shop-list";
    }

    @GetMapping("/vehicle")
    public String searchByVehicle(@RequestParam(required = false) String make, @RequestParam(required = false) String model, @RequestParam(required = false) String engine, @RequestParam(required = false) Integer year, @RequestParam(defaultValue = "0") int page, Model m) {
        if (make == null || model == null || engine == null || year == null) {
            m.addAttribute("errorMessage", "Vehicle make, model, engine, and year are required");
            return "products/vehicle-compatibility";
        }
        if (make.length() < 2 || model.length() < 2 || engine.length() < 2) {
            m.addAttribute("errorMessage", "Vehicle make, model, and engine must be at least 2 characters long");
            return "products/vehicle-compatibility";
        }
        if (year < 1900 || year > 2030) {
            m.addAttribute("errorMessage", "Vehicle year must be between 1900 and 2030");
            return "products/vehicle-compatibility";
        }

        Page<Product> productPage = productService.findByVehicleCompatibility(make, model, engine, year, PageRequest.of(page, 12));
        m.addAttribute("products", productPage.getContent());
        m.addAttribute("page", productPage);
        m.addAttribute("vehicleMake", make);
        m.addAttribute("vehicleModel", model);
        m.addAttribute("vehicleEngine", engine);
        m.addAttribute("vehicleYear", year);
        return "products/vehicle-compatibility";
    }
}