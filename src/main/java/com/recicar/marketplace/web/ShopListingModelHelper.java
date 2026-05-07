package com.recicar.marketplace.web;

import com.recicar.marketplace.dto.ProductCardDto;
import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import java.util.Collections;
import java.util.List;

/**
 * Adds {@code productCards}, {@code page}, and legacy {@code products} for shop-list views.
 */
@Component
@RequiredArgsConstructor
public class ShopListingModelHelper {

    private final ProductService productService;

    public void putPagedListing(Model model, Page<Product> productPage) {
        model.addAttribute("products", productPage.getContent());
        Page<ProductCardDto> cardPage = productService.mapToProductCardPage(productPage);
        model.addAttribute("page", cardPage);
        model.addAttribute("productCards", cardPage.getContent());
    }

    public void putListListing(Model model, List<Product> products) {
        model.addAttribute("products", products);
        Page<ProductCardDto> cardPage = productService.mapListToProductCardPage(products);
        model.addAttribute("page", cardPage);
        model.addAttribute("productCards", cardPage.getContent());
    }

    public void putEmptyListing(Model model) {
        model.addAttribute("products", Collections.emptyList());
        model.addAttribute("productCards", Collections.emptyList());
        model.addAttribute("page", null);
    }
}
