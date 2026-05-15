package com.recicar.marketplace.controller;

import com.recicar.marketplace.dto.CartDto;
import com.recicar.marketplace.dto.CartItemDto;
import com.recicar.marketplace.repository.ProductRepository;
import com.recicar.marketplace.service.CartPricingService;
import com.recicar.marketplace.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping({"/api/cart", "/api/v1/cart"})
public class CartApiController {

    private static final String SESSION_CART_KEY = "SESSION_CART";

    private final CartService cartService;
    private final com.recicar.marketplace.repository.UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CartPricingService cartPricingService;

    public CartApiController(
            CartService cartService,
            com.recicar.marketplace.repository.UserRepository userRepository,
            ProductRepository productRepository,
            CartPricingService cartPricingService) {
        this.cartService = cartService;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.cartPricingService = cartPricingService;
    }

    @GetMapping
    public ResponseEntity<CartDto> getFullCart(
            @AuthenticationPrincipal UserDetails userDetails,
            HttpSession session) {
        if (userDetails != null) {
            return userRepository.findByEmailIgnoreCase(userDetails.getUsername())
                    .map(u -> ResponseEntity.ok(cartService.getCart(u.getId())))
                    .orElseGet(() -> ResponseEntity.status(401).build());
        }
        return ResponseEntity.ok(buildSessionCartDto(session));
    }

    @GetMapping("/count")
    public Map<String, Integer> getCount(@AuthenticationPrincipal UserDetails userDetails, HttpSession session) {
        Integer count = 0;
        if (userDetails != null) {
            String email = userDetails.getUsername();
            var userOpt = userRepository.findByEmailIgnoreCase(email);
            if (userOpt.isPresent()) {
                CartDto cart = cartService.getCart(userOpt.get().getId());
                count = cart.getItems() == null ? 0 : cart.getItems().stream().mapToInt(CartItemDto::getQuantity).sum();
            }
        } else {
            List<CartItemDto> sessionCart = getSessionCart(session);
            count = sessionCart.stream().mapToInt(CartItemDto::getQuantity).sum();
        }
        Map<String, Integer> result = new HashMap<>();
        result.put("count", count);
        return result;
    }

    @PostMapping({"/items", "/add"})
    public ResponseEntity<CartDto> addItem(@AuthenticationPrincipal UserDetails userDetails,
                                           HttpSession session,
                                           @RequestParam Long productId,
                                           @RequestParam(defaultValue = "1") int quantity) {
        if (userDetails != null) {
            String email = userDetails.getUsername();
            var userOpt = userRepository.findByEmailIgnoreCase(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401).build();
            }
            CartDto cart = cartService.addItemToCart(userOpt.get().getId(), productId, quantity);
            return ResponseEntity.ok(cart);
        }
        List<CartItemDto> sessionCart = getSessionCart(session);
        mergeSessionLine(sessionCart, productId, quantity);
        session.setAttribute(SESSION_CART_KEY, sessionCart);
        return ResponseEntity.ok(buildSessionCartDto(sessionCart));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<CartDto> updateItem(
            @AuthenticationPrincipal UserDetails userDetails,
            HttpSession session,
            @PathVariable Long itemId,
            @RequestParam int quantity) {
        if (quantity < 1) {
            return ResponseEntity.badRequest().build();
        }
        if (userDetails != null) {
            var userOpt = userRepository.findByEmailIgnoreCase(userDetails.getUsername());
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401).build();
            }
            CartDto cart = cartService.updateItemInCart(userOpt.get().getId(), itemId, quantity);
            return ResponseEntity.ok(cart);
        }
        List<CartItemDto> sessionCart = getSessionCart(session);
        boolean found = false;
        for (CartItemDto item : sessionCart) {
            if (item.getId() != null && item.getId().equals(itemId)) {
                found = true;
                productRepository.findById(item.getProductId()).ifPresentOrElse(product -> {
                    int capped = Math.min(quantity, 10);
                    capped = Math.min(capped, product.getStockQuantity());
                    item.setQuantity(Math.max(1, capped));
                }, () -> item.setQuantity(quantity));
                break;
            }
        }
        if (!found) {
            return ResponseEntity.notFound().build();
        }
        session.setAttribute(SESSION_CART_KEY, sessionCart);
        return ResponseEntity.ok(buildSessionCartDto(sessionCart));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<CartDto> removeItem(
            @AuthenticationPrincipal UserDetails userDetails,
            HttpSession session,
            @PathVariable Long itemId) {
        if (userDetails != null) {
            var userOpt = userRepository.findByEmailIgnoreCase(userDetails.getUsername());
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(401).build();
            }
            cartService.removeItemFromCart(userOpt.get().getId(), itemId);
            return ResponseEntity.ok(cartService.getCart(userOpt.get().getId()));
        }
        List<CartItemDto> sessionCart = getSessionCart(session);
        boolean removed = sessionCart.removeIf(item -> item.getId() != null && item.getId().equals(itemId));
        if (!removed) {
            return ResponseEntity.notFound().build();
        }
        session.setAttribute(SESSION_CART_KEY, sessionCart);
        return ResponseEntity.ok(buildSessionCartDto(sessionCart));
    }

    private CartDto buildSessionCartDto(HttpSession session) {
        return buildSessionCartDto(getSessionCart(session));
    }

    private CartDto buildSessionCartDto(List<CartItemDto> sessionCart) {
        CartDto cartDto = new CartDto();
        cartDto.setItems(sessionCart);
        BigDecimal sub = sessionCart.stream()
                .filter(i -> i.getPrice() != null)
                .map(i -> i.getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        cartDto.setSubtotal(sub);
        cartPricingService.applyPricing(cartDto, null, null, null, null);
        return cartDto;
    }

    private void mergeSessionLine(List<CartItemDto> cart, Long productId, int quantity) {
        for (CartItemDto item : cart) {
            if (item.getProductId() != null && item.getProductId().equals(productId)) {
                productRepository.findById(productId).ifPresentOrElse(product -> {
                    int next = item.getQuantity() + quantity;
                    int max = Math.min(10, product.getStockQuantity());
                    item.setQuantity(Math.min(next, max));
                    if (item.getId() == null) {
                        item.setId(productId);
                    }
                }, () -> {
                    item.setQuantity(item.getQuantity() + quantity);
                    if (item.getId() == null) {
                        item.setId(productId);
                    }
                });
                return;
            }
        }
        try {
            var productOpt = productRepository.findById(productId);
            if (productOpt.isPresent()) {
                var product = productOpt.get();
                int q = Math.min(quantity, 10);
                q = Math.min(q, product.getStockQuantity());
                if (q < 1) {
                    return;
                }
                CartItemDto newItem = new CartItemDto();
                newItem.setId(productId);
                newItem.setProductId(productId);
                newItem.setQuantity(q);
                newItem.setProductName(product.getName());
                newItem.setPrice(product.getPrice());
                if (product.getImages() != null && !product.getImages().isEmpty()) {
                    var primaryImage = product.getImages().stream()
                            .filter(img -> img.isPrimary())
                            .findFirst()
                            .orElse(product.getImages().get(0));
                    newItem.setImageUrl(primaryImage.getImageUrl());
                }
                cart.add(newItem);
            }
        } catch (Exception ignored) {
            CartItemDto newItem = new CartItemDto();
            newItem.setProductId(productId);
            newItem.setId(productId);
            newItem.setQuantity(quantity);
            newItem.setProductName("Product " + productId);
            newItem.setPrice(BigDecimal.ZERO);
            cart.add(newItem);
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
}
