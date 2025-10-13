package com.recicar.marketplace.controller;

import com.recicar.marketplace.dto.CartDto;
import com.recicar.marketplace.dto.CartItemDto;
import com.recicar.marketplace.service.CartService;
import com.recicar.marketplace.repository.UserRepository;
import com.recicar.marketplace.repository.ProductRepository;
import com.recicar.marketplace.entity.User;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/cart")
public class CartController {

    private static final String SESSION_CART_KEY = "SESSION_CART";

    private final CartService cartService;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartController(CartService cartService, UserRepository userRepository, ProductRepository productRepository) {
        this.cartService = cartService;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @GetMapping
    public String getCartPage(@AuthenticationPrincipal UserDetails userDetails, HttpSession session, Model model) {
        try {
            Long userId = resolveUserId(userDetails);
            log.debug("Resolved userId: {}", userId);
            
            CartDto cart;
            if (userId != null) {
                cart = cartService.getCart(userId);
            } else {
                log.debug("Getting cart for anonymous user");
                // Anonymous user - get cart from session
                List<CartItemDto> sessionCart = getSessionCart(session);
                // Ensure each item has id set to productId for consistency
                for (CartItemDto item : sessionCart) {
                    if (item.getId() == null) {
                        item.setId(item.getProductId());
                    }
                }
                cart = new CartDto();
                cart.setItems(sessionCart);
                // Calculate subtotal for session cart with null safety
                cart.setSubtotal(sessionCart.stream()
                        .filter(item -> item.getPrice() != null) // Filter out null prices
                        .map(item -> item.getPrice().multiply(java.math.BigDecimal.valueOf(item.getQuantity())))
                        .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add));
            }
            model.addAttribute("cart", cart);
            return "cart";
        } catch (Exception e) {
            // Log the error and return a safe fallback
            log.error("Error loading cart page", e);

            // Create an empty cart as fallback
            CartDto emptyCart = new CartDto();
            emptyCart.setItems(new java.util.ArrayList<>());
            emptyCart.setSubtotal(java.math.BigDecimal.ZERO);
            model.addAttribute("cart", emptyCart);
            return "cart";
        }
    }

    @PostMapping("/items")
    public String addItemToCart(@AuthenticationPrincipal UserDetails userDetails, HttpSession session, @RequestParam Long productId, @RequestParam int quantity) {
        Long userId = resolveUserId(userDetails);
        if (userId != null) {
            cartService.addItemToCart(userId, productId, quantity);
        } else {
            // Anonymous user - add to session cart
            List<CartItemDto> sessionCart = getSessionCart(session);
            addItemToSessionCart(sessionCart, productId, quantity);
            session.setAttribute(SESSION_CART_KEY, sessionCart);
        }
        return "redirect:/cart";
    }

    @PostMapping("/update")
    public String updateCart(@AuthenticationPrincipal UserDetails userDetails, HttpSession session, @RequestParam("itemId") Long itemId, @RequestParam("quantity") int quantity) {
        Long userId = resolveUserId(userDetails);
        if (userId != null) {
            cartService.updateItemInCart(userId, itemId, quantity);
        } else {
            // Anonymous user - update session cart
            List<CartItemDto> sessionCart = getSessionCart(session);
            for (CartItemDto item : sessionCart) {
                if (item.getId() != null && item.getId().equals(itemId)) {
                    item.setQuantity(quantity);
                    break;
                }
            }
            session.setAttribute(SESSION_CART_KEY, sessionCart);
        }
        return "redirect:/cart";
    }

    @GetMapping("/remove/{itemId}")
    public String removeItemFromCart(@AuthenticationPrincipal UserDetails userDetails, HttpSession session, @PathVariable Long itemId) {
        Long userId = resolveUserId(userDetails);
        if (userId != null) {
            cartService.removeItemFromCart(userId, itemId);
        } else {
            // Anonymous user - remove from session cart
            List<CartItemDto> sessionCart = getSessionCart(session);
            sessionCart.removeIf(item -> item.getId() != null && item.getId().equals(itemId));
            session.setAttribute(SESSION_CART_KEY, sessionCart);
        }
        return "redirect:/cart";
    }

    @GetMapping("/clear")
    public String clearCart(@AuthenticationPrincipal UserDetails userDetails, HttpSession session) {
        Long userId = resolveUserId(userDetails);
        if (userId != null) {
            cartService.clearCart(userId);
        } else {
            // Anonymous user - clear session cart
            List<CartItemDto> sessionCart = getSessionCart(session);
            sessionCart.clear();
            session.setAttribute(SESSION_CART_KEY, sessionCart);
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

    @SuppressWarnings("unchecked")
    private List<CartItemDto> getSessionCart(HttpSession session) {
        List<CartItemDto> cart = (List<CartItemDto>) session.getAttribute(SESSION_CART_KEY);
        if (cart == null) {
            cart = new java.util.ArrayList<>();
            session.setAttribute(SESSION_CART_KEY, cart);
        }
        return cart;
    }

    private void addItemToSessionCart(List<CartItemDto> cart, Long productId, int quantity) {
        try {
            // Check if item already exists in cart
            for (CartItemDto item : cart) {
                if (item.getProductId() != null && item.getProductId().equals(productId)) {
                    item.setQuantity(item.getQuantity() + quantity);
                    return;
                }
            }

            // Item doesn't exist, add new item with product details
            var productOpt = productRepository.findById(productId);
            if (productOpt.isPresent()) {
                var product = productOpt.get();
                CartItemDto newItem = new CartItemDto();
                newItem.setId(productId); // Use productId as id for session cart
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
            log.error("Error adding item to session cart", e);
        }
    }

    private Long resolveUserId(UserDetails userDetails) {
        log.debug("=== resolveUserId called ===");
        if (userDetails == null) {
            log.debug("UserDetails is null, returning null");
            return null;
        }
        String email = userDetails.getUsername();
        log.debug("Email: {}", email);
        try {
            Long userId = userRepository.findByEmailIgnoreCase(email)
                    .map(User::getId)
                    .orElse(null);
            log.debug("Resolved userId: {}", userId);
            return userId;
        } catch (Exception e) {
            log.error("Error resolving userId", e);
            return null;
        }
    }
}
