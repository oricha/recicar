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
    private final com.recicar.marketplace.repository.UserRepository userRepository;

    public CartApiController(CartService cartService, com.recicar.marketplace.repository.UserRepository userRepository) {
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    @GetMapping("/count")
    public Map<String, Integer> getCount(@AuthenticationPrincipal UserDetails userDetails) {
        Integer count = 0;
        if (userDetails != null) {
            String email = userDetails.getUsername();
            var userOpt = userRepository.findByEmailIgnoreCase(email);
            if (userOpt.isPresent()) {
                CartDto cart = cartService.getCart(userOpt.get().getId());
                count = cart.getItems() == null ? 0 : cart.getItems().stream().mapToInt(i -> i.getQuantity()).sum();
            }
        }
        Map<String, Integer> result = new HashMap<>();
        result.put("count", count);
        return result;
    }

    @PostMapping("/items")
    public ResponseEntity<CartDto> addItem(@AuthenticationPrincipal UserDetails userDetails,
                                           @RequestParam Long productId,
                                           @RequestParam(defaultValue = "1") int quantity) {
        if (userDetails == null) {
            return ResponseEntity.status(401).build();
        }
        String email = userDetails.getUsername();
        var userOpt = userRepository.findByEmailIgnoreCase(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).build();
        }
        CartDto cart = cartService.addItemToCart(userOpt.get().getId(), productId, quantity);
        return ResponseEntity.ok(cart);
    }
}
