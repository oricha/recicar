package com.recicar.marketplace.controller;

import com.recicar.marketplace.dto.CartDto;
import com.recicar.marketplace.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public String getCartPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        // TODO: Get user ID from userDetails
        Long userId = 1L; // Assuming user ID is 1 for now
        CartDto cart = cartService.getCart(userId);
        model.addAttribute("cart", cart);
        return "cart";
    }

    @PostMapping("/items")
    public String addItemToCart(@AuthenticationPrincipal UserDetails userDetails, @RequestParam Long productId, @RequestParam int quantity) {
        // TODO: Get user ID from userDetails
        Long userId = 1L; // Assuming user ID is 1 for now
        cartService.addItemToCart(userId, productId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateCart(@AuthenticationPrincipal UserDetails userDetails, @RequestParam("items[0].id") Long itemId, @RequestParam("items[0].quantity") int quantity) {
        // TODO: Get user ID from userDetails
        Long userId = 1L; // Assuming user ID is 1 for now
        cartService.updateItemInCart(userId, itemId, quantity);
        return "redirect:/cart";
    }

    @GetMapping("/remove/{itemId}")
    public String removeItemFromCart(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long itemId) {
        // TODO: Get user ID from userDetails
        Long userId = 1L; // Assuming user ID is 1 for now
        cartService.removeItemFromCart(userId, itemId);
        return "redirect:/cart";
    }

    @GetMapping("/clear")
    public String clearCart(@AuthenticationPrincipal UserDetails userDetails) {
        // TODO: Get user ID from userDetails
        Long userId = 1L; // Assuming user ID is 1 for now
        cartService.clearCart(userId);
        return "redirect:/cart";
    }
}
