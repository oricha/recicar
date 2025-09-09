package com.recicar.marketplace.controller;

import com.recicar.marketplace.dto.CartDto;
import com.recicar.marketplace.repository.UserRepository;
import com.recicar.marketplace.service.CartService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MiniCartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    public MiniCartController(CartService cartService, UserRepository userRepository) {
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    @GetMapping("/mini-cart")
    public String getMiniCart(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        CartDto cart = new CartDto();
        if (userDetails != null) {
            userRepository.findByEmailIgnoreCase(userDetails.getUsername())
                    .ifPresent(user -> model.addAttribute("cart", cartService.getCart(user.getId())));
        }
        if (!model.containsAttribute("cart")) {
            model.addAttribute("cart", cart);
        }
        return "fragments/_mini_cart :: miniCart";
    }
}

