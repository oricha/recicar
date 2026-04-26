package com.recicar.marketplace.controller;

import com.recicar.marketplace.entity.User;
import com.recicar.marketplace.repository.UserRepository;
import com.recicar.marketplace.service.WishlistService;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistApiController {

    private static final String SESSION_KEY = WishlistService.SESSION_WISHLIST_KEY;

    private final UserRepository userRepository;
    private final WishlistService wishlistService;

    public WishlistApiController(UserRepository userRepository, WishlistService wishlistService) {
        this.userRepository = userRepository;
        this.wishlistService = wishlistService;
    }

    @GetMapping("/items")
    public Map<String, Object> listItems(
            HttpSession session,
            @AuthenticationPrincipal UserDetails userDetails) {
        return resolveUser(userDetails)
                .map(u -> {
                    Set<Long> ids = wishlistService.productIdsForUser(u.getId());
                    Map<String, Object> out = new HashMap<>();
                    out.put("items", ids);
                    out.put("count", ids.size());
                    out.put("persisted", true);
                    return out;
                })
                .orElseGet(() -> {
                    Set<Long> wl = getSessionWishlist(session);
                    Map<String, Object> out = new HashMap<>();
                    out.put("items", wl);
                    out.put("count", wl.size());
                    out.put("persisted", false);
                    return out;
                });
    }

    @GetMapping("/count")
    public Map<String, Object> count(
            HttpSession session,
            @AuthenticationPrincipal UserDetails userDetails) {
        var u = resolveUser(userDetails);
        if (u.isEmpty()) {
            return countMap(getSessionWishlist(session).size(), false);
        }
        return countMap(wishlistService.countForUser(u.get().getId()), true);
    }

    private static Map<String, Object> countMap(int count, boolean persisted) {
        Map<String, Object> m = new HashMap<>();
        m.put("count", count);
        m.put("persisted", persisted);
        return m;
    }

    @PostMapping("/items")
    public ResponseEntity<Map<String, Object>> addItem(
            @RequestParam("productId") Long productId,
            HttpSession session,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (productId == null) {
            return ResponseEntity.badRequest().build();
        }
        var userOpt = resolveUser(userDetails);
        if (userOpt.isEmpty()) {
            Set<Long> wl = getSessionWishlist(session);
            wl.add(productId);
            session.setAttribute(SESSION_KEY, wl);
            return ResponseEntity.ok(countMap(wl.size(), false));
        }
        var u = userOpt.get();
        int c = wishlistService.addItem(u.getId(), productId);
        wishlistService.syncSessionWithDatabase(session, u.getId());
        return ResponseEntity.ok(countMap(c, true));
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Map<String, Object>> removeItem(
            @PathVariable("productId") Long productId,
            HttpSession session,
            @AuthenticationPrincipal UserDetails userDetails) {
        var userOpt = resolveUser(userDetails);
        if (userOpt.isEmpty()) {
            Set<Long> wl = getSessionWishlist(session);
            if (productId != null) {
                wl.remove(productId);
            }
            session.setAttribute(SESSION_KEY, wl);
            return ResponseEntity.ok(countMap(wl.size(), false));
        }
        var u = userOpt.get();
        int c = wishlistService.removeItem(u.getId(), productId);
        wishlistService.syncSessionWithDatabase(session, u.getId());
        return ResponseEntity.ok(countMap(c, true));
    }

    private java.util.Optional<User> resolveUser(UserDetails userDetails) {
        if (userDetails == null) {
            return java.util.Optional.empty();
        }
        return userRepository.findByEmailIgnoreCase(userDetails.getUsername());
    }

    private Set<Long> getSessionWishlist(HttpSession session) {
        @SuppressWarnings("unchecked")
        Set<Long> wl = (Set<Long>) session.getAttribute(SESSION_KEY);
        if (wl == null) {
            wl = new HashSet<>();
            session.setAttribute(SESSION_KEY, wl);
        }
        return wl;
    }
}
