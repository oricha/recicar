package com.recicar.marketplace.controller;

import com.recicar.marketplace.dto.CartDto;
import com.recicar.marketplace.service.CartService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartApiController {

    private final CartService cartService;

    public CartApiController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/count")
    public Map<String, Integer> getCount(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = 1L; // TODO: derive from userDetails
        CartDto cart = cartService.getCart(userId);
        int count = cart.getItems() == null ? 0 : cart.getItems().stream().mapToInt(i -> i.getQuantity()).sum();
        Map<String, Integer> result = new HashMap<>();
        result.put("count", count);
        return result;
    }

    @PostMapping("/items")
    public ResponseEntity<CartDto> addItem(@AuthenticationPrincipal UserDetails userDetails,
                                           @RequestParam Long productId,
                                           @RequestParam(defaultValue = "1") int quantity) {
        Long userId = 1L; // TODO: derive from userDetails
        CartDto cart = cartService.addItemToCart(userId, productId, quantity);
        return ResponseEntity.ok(cart);
    }
}

