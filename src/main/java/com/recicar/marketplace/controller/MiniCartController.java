package com.recicar.marketplace.controller;

import com.recicar.marketplace.dto.CartDto;
import com.recicar.marketplace.dto.CartItemDto;
import com.recicar.marketplace.repository.UserRepository;
import com.recicar.marketplace.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class MiniCartController {

    private static final String SESSION_CART_KEY = "SESSION_CART";

    private final CartService cartService;
    private final UserRepository userRepository;

    public MiniCartController(CartService cartService, UserRepository userRepository) {
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    @GetMapping("/mini-cart")
    public String getMiniCart(@AuthenticationPrincipal UserDetails userDetails, HttpSession session, Model model) {
        if (userDetails != null) {
            // Authenticated user - get cart from database
            userRepository.findByEmailIgnoreCase(userDetails.getUsername())
                    .ifPresent(user -> model.addAttribute("cart", cartService.getCart(user.getId())));
        } else {
            // Anonymous user - get cart from session
            List<CartItemDto> sessionCart = getSessionCart(session);
            CartDto cartDto = new CartDto();
            cartDto.setItems(sessionCart);
            // Calculate subtotal for session cart
            cartDto.setSubtotal(sessionCart.stream()
                    .map(item -> item.getPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity())))
                    .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));
            model.addAttribute("cart", cartDto);
        }

        if (!model.containsAttribute("cart")) {
            CartDto emptyCart = new CartDto();
            model.addAttribute("cart", emptyCart);
        }
        return "fragments/_mini_cart :: miniCart";
    }

    @SuppressWarnings("unchecked")
    private List<CartItemDto> getSessionCart(HttpSession session) {
        List<CartItemDto> cart = (List<CartItemDto>) session.getAttribute(SESSION_CART_KEY);
        if (cart == null) {
            cart = new java.util.ArrayList<>();
            session.setAttribute(SESSION_CART_KEY, cart);
        }
        return cart;
    }
}

