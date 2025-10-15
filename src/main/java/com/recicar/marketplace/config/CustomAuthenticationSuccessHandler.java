package com.recicar.marketplace.config;

import com.recicar.marketplace.dto.CartItemDto;
import com.recicar.marketplace.entity.UserRole;
import com.recicar.marketplace.service.CartService;
import com.recicar.marketplace.service.CustomUserDetailsService;
import com.recicar.marketplace.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import java.io.IOException;
import java.util.List;

@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final String SESSION_CART_KEY = "SESSION_CART";
    
    private final RequestCache requestCache = new HttpSessionRequestCache();
    private final CartService cartService;
    private final UserRepository userRepository;

    public CustomAuthenticationSuccessHandler(CartService cartService, UserRepository userRepository) {
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {
        
        // Get the user details
        CustomUserDetailsService.CustomUserPrincipal userPrincipal = 
            (CustomUserDetailsService.CustomUserPrincipal) authentication.getPrincipal();
        
        UserRole userRole = userPrincipal.getUser().getRole();
        String userEmail = userPrincipal.getUsername();
        
        // Merge session cart with database cart
        mergeSessionCartToDatabase(request.getSession(), userEmail);
        
        // Check if there was a saved request (user was trying to access a protected page)
        SavedRequest savedRequest = requestCache.getRequest(request, response);
        if (savedRequest != null) {
            String targetUrl = savedRequest.getRedirectUrl();
            // Clear the saved request
            requestCache.removeRequest(request, response);
            response.sendRedirect(targetUrl);
            return;
        }
        
        // Default redirect based on user role
        String redirectUrl = switch (userRole) {
            case ADMIN -> "/admin/dashboard";
            case VENDOR -> "/vendor/dashboard";
            case CUSTOMER -> "/";
        };
        
        response.sendRedirect(redirectUrl);
    }
    
    @SuppressWarnings("unchecked")
    private void mergeSessionCartToDatabase(HttpSession session, String userEmail) {
        try {
            // Get session cart
            List<CartItemDto> sessionCart = (List<CartItemDto>) session.getAttribute(SESSION_CART_KEY);
            
            if (sessionCart == null || sessionCart.isEmpty()) {
                log.debug("No session cart to merge for user: {}", userEmail);
                return;
            }
            
            // Get user ID
            Long userId = userRepository.findByEmailIgnoreCase(userEmail)
                    .map(user -> user.getId())
                    .orElse(null);
            
            if (userId == null) {
                log.warn("User not found for email: {}", userEmail);
                return;
            }
            
            // Merge each item from session cart to database cart
            log.info("Merging {} items from session cart to database cart for user: {}", sessionCart.size(), userEmail);
            for (CartItemDto item : sessionCart) {
                try {
                    cartService.addItemToCart(userId, item.getProductId(), item.getQuantity());
                    log.debug("Merged item {} (quantity: {}) to database cart", item.getProductId(), item.getQuantity());
                } catch (Exception e) {
                    log.error("Failed to merge item {} to database cart: {}", item.getProductId(), e.getMessage());
                }
            }
            
            // Clear session cart after successful merge
            session.removeAttribute(SESSION_CART_KEY);
            log.debug("Session cart cleared after merge");
            
        } catch (Exception e) {
            log.error("Error merging session cart to database: {}", e.getMessage(), e);
        }
    }
}