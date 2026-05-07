package com.recicar.marketplace.service;

import com.recicar.marketplace.dto.CartItemDto;
import com.recicar.marketplace.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Guest session cart + wishlist merge after authentication (web or API session login).
 */
@Slf4j
@Service
public class PostLoginActionsService {

    private static final String SESSION_CART_KEY = "SESSION_CART";

    private final CartService cartService;
    private final UserRepository userRepository;
    private final WishlistService wishlistService;

    public PostLoginActionsService(
            CartService cartService,
            UserRepository userRepository,
            WishlistService wishlistService) {
        this.cartService = cartService;
        this.userRepository = userRepository;
        this.wishlistService = wishlistService;
    }

    /**
     * Merges guest session cart and wishlist into the persisted user records.
     */
    public void mergeGuestDataIntoUserSession(HttpSession session, String userEmail) {
        mergeSessionCartToDatabase(session, userEmail);
        mergeSessionWishlistToDatabase(session, userEmail);
    }

    @SuppressWarnings("unchecked")
    private void mergeSessionCartToDatabase(HttpSession session, String userEmail) {
        try {
            List<CartItemDto> sessionCart = (List<CartItemDto>) session.getAttribute(SESSION_CART_KEY);
            if (sessionCart == null || sessionCart.isEmpty()) {
                log.debug("No session cart to merge for user: {}", userEmail);
                return;
            }
            Long userId = userRepository.findByEmailIgnoreCase(userEmail)
                    .map(u -> u.getId())
                    .orElse(null);
            if (userId == null) {
                log.warn("User not found for email: {}", userEmail);
                return;
            }
            log.info("Merging {} guest cart items for user: {}", sessionCart.size(), userEmail);
            for (CartItemDto item : sessionCart) {
                try {
                    cartService.addItemToCart(userId, item.getProductId(), item.getQuantity());
                } catch (Exception e) {
                    log.error("Failed to merge cart item {}: {}", item.getProductId(), e.getMessage());
                }
            }
            session.removeAttribute(SESSION_CART_KEY);
        } catch (Exception e) {
            log.error("Error merging session cart: {}", e.getMessage(), e);
        }
    }

    private void mergeSessionWishlistToDatabase(HttpSession session, String userEmail) {
        try {
            Long userId = userRepository.findByEmailIgnoreCase(userEmail)
                    .map(u -> u.getId())
                    .orElse(null);
            if (userId == null) {
                return;
            }
            wishlistService.mergeSessionWishlistIntoDatabase(session, userId);
        } catch (Exception e) {
            log.error("Error merging session wishlist: {}", e.getMessage(), e);
        }
    }
}
