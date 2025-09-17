package com.recicar.marketplace.controller;

import com.recicar.marketplace.dto.CartDto;
import com.recicar.marketplace.dto.CartItemDto;
import com.recicar.marketplace.repository.ProductRepository;
import com.recicar.marketplace.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/cart")
public class CartApiController {

    private static final String SESSION_CART_KEY = "SESSION_CART";

    private final CartService cartService;
    private final com.recicar.marketplace.repository.UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartApiController(CartService cartService, com.recicar.marketplace.repository.UserRepository userRepository, ProductRepository productRepository) {
        this.cartService = cartService;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @GetMapping("/count")
    public Map<String, Integer> getCount(@AuthenticationPrincipal UserDetails userDetails, HttpSession session) {
        Integer count = 0;
        if (userDetails != null) {
            // Authenticated user - get cart from database
            String email = userDetails.getUsername();
            var userOpt = userRepository.findByEmailIgnoreCase(email);
            if (userOpt.isPresent()) {
                CartDto cart = cartService.getCart(userOpt.get().getId());
                count = cart.getItems() == null ? 0 : cart.getItems().stream().mapToInt(i -> i.getQuantity()).sum();
            }
        } else {
            // Anonymous user - get cart from session
            List<CartItemDto> sessionCart = getSessionCart(session);
            count = sessionCart.stream().mapToInt(CartItemDto::getQuantity).sum();
        }
        Map<String, Integer> result = new HashMap<>();
        result.put("count", count);
        return result;
    }

    @PostMapping("/items")
    public ResponseEntity<CartDto> addItem(@AuthenticationPrincipal UserDetails userDetails,
                                            HttpSession session,
                                            @RequestParam Long productId,
                                            @RequestParam(defaultValue = "1") int quantity) {
        if (userDetails != null) {
            // Authenticated user - add to database cart
            String email = userDetails.getUsername();
            var userOpt = userRepository.findByEmailIgnoreCase(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401).build();
            }
            CartDto cart = cartService.addItemToCart(userOpt.get().getId(), productId, quantity);
            return ResponseEntity.ok(cart);
        } else {
            // Anonymous user - add to session cart
            List<CartItemDto> sessionCart = getSessionCart(session);
            addItemToSessionCart(sessionCart, productId, quantity);
            session.setAttribute(SESSION_CART_KEY, sessionCart);

            // Return a CartDto representation of the session cart
            CartDto cartDto = new CartDto();
            cartDto.setItems(sessionCart);
            return ResponseEntity.ok(cartDto);
        }
    }

    @SuppressWarnings("unchecked")
    private List<CartItemDto> getSessionCart(HttpSession session) {
        List<CartItemDto> cart = (List<CartItemDto>) session.getAttribute(SESSION_CART_KEY);
        if (cart == null) {
            cart = new ArrayList<>();
            session.setAttribute(SESSION_CART_KEY, cart);
        }
        return cart;
    }

    private void addItemToSessionCart(List<CartItemDto> cart, Long productId, int quantity) {
        // Check if item already exists in cart
        for (CartItemDto item : cart) {
            if (item.getProductId().equals(productId)) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }

        // Item doesn't exist, add new item with product details
        try {
            var productOpt = productRepository.findById(productId);
            if (productOpt.isPresent()) {
                var product = productOpt.get();
                CartItemDto newItem = new CartItemDto();
                newItem.setProductId(productId);
                newItem.setQuantity(quantity);
                newItem.setProductName(product.getName());
                newItem.setPrice(product.getPrice());

                // Set image URL if available
                if (product.getImages() != null && !product.getImages().isEmpty()) {
                    var primaryImage = product.getImages().stream()
                            .filter(img -> img.isPrimary())
                            .findFirst()
                            .orElse(product.getImages().get(0));
                    newItem.setImageUrl(primaryImage.getImageUrl());
                }

                cart.add(newItem);
            }
        } catch (Exception e) {
            // Fallback: create item with minimal data
            CartItemDto newItem = new CartItemDto();
            newItem.setProductId(productId);
            newItem.setQuantity(quantity);
            newItem.setProductName("Product " + productId);
            newItem.setPrice(new java.math.BigDecimal("0.00"));
            cart.add(newItem);
        }
    }
}
