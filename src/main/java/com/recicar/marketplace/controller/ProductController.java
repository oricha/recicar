package com.recicar.marketplace.controller;

import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping("/list")
    public String productList(Model model) {
        Page<Product> productPage = productRepository.findAll(PageRequest.of(0, 12));
        model.addAttribute("products", productPage.getContent());
        model.addAttribute("page", productPage);
        return "shop-right-sidebar-list";
    }

    @GetMapping("/product-details")
    public String productDetails(@RequestParam("id") Long id, Model model) {
        Optional<Product> productOptional = productRepository.findById(id);
        if (productOptional.isPresent()) {
            model.addAttribute("product", productOptional.get());
            return "product-details";
        } else {
            return "error"; // Or a specific 'product not found' page
        }
    }
}
