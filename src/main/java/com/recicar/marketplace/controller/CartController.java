package com.recicar.marketplace.controller;

import com.recicar.marketplace.dto.CartDto;
import com.recicar.marketplace.service.CartService;
import com.recicar.marketplace.repository.UserRepository;
import com.recicar.marketplace.entity.User;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    public CartController(CartService cartService, UserRepository userRepository) {
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String getCartPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        Long userId = resolveUserId(userDetails);
        CartDto cart;
        if (userId != null) {
            cart = cartService.getCart(userId);
        } else {
            cart = new CartDto();
            cart.setItems(java.util.Collections.emptyList());
            cart.setSubtotal(java.math.BigDecimal.ZERO);
        }
        model.addAttribute("cart", cart);
        return "cart";
    }

    @PostMapping("/items")
    public String addItemToCart(@AuthenticationPrincipal UserDetails userDetails, @RequestParam Long productId, @RequestParam int quantity) {
        Long userId = resolveUserId(userDetails);
        if (userId != null) {
            cartService.addItemToCart(userId, productId, quantity);
        }
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateCart(@AuthenticationPrincipal UserDetails userDetails, @RequestParam("items[0].id") Long itemId, @RequestParam("items[0].quantity") int quantity) {
        Long userId = resolveUserId(userDetails);
        if (userId != null) {
            cartService.updateItemInCart(userId, itemId, quantity);
        }
        return "redirect:/cart";
    }

    @GetMapping("/remove/{itemId}")
    public String removeItemFromCart(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long itemId) {
        Long userId = resolveUserId(userDetails);
        if (userId != null) {
            cartService.removeItemFromCart(userId, itemId);
        }
        return "redirect:/cart";
    }

    @GetMapping("/clear")
    public String clearCart(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = resolveUserId(userDetails);
        if (userId != null) {
            cartService.clearCart(userId);
        }
        return "redirect:/cart";
    }

    @PostMapping("/checkout")
    public String proceedToCheckout(@AuthenticationPrincipal UserDetails userDetails) {
        Long userId = resolveUserId(userDetails);
        if (userId == null) {
            return "redirect:/login";
        }
        cartService.validateCart(userId);
        return "redirect:/checkout";
    }

    private Long resolveUserId(UserDetails userDetails) {
        if (userDetails == null) return null;
        String email = userDetails.getUsername();
        return userRepository.findByEmailIgnoreCase(email)
                .map(User::getId)
                .orElse(null);
    }
}
