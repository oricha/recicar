package com.recicar.marketplace.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/wishlist")
public class WishlistApiController {

    private static final String SESSION_KEY = "WISHLIST_PRODUCT_IDS";

    @GetMapping("/count")
    public Map<String, Integer> count(HttpSession session) {
        Set<Long> wl = getWishlist(session);
        Map<String, Integer> out = new HashMap<>();
        out.put("count", wl.size());
        return out;
    }

    @PostMapping("/items")
    public ResponseEntity<Map<String, Integer>> addItem(@RequestParam("productId") Long productId,
                                                        HttpSession session) {
        if (productId == null) {
            return ResponseEntity.badRequest().build();
        }
        Set<Long> wl = getWishlist(session);
        wl.add(productId);
        session.setAttribute(SESSION_KEY, wl);
        Map<String, Integer> out = new HashMap<>();
        out.put("count", wl.size());
        return ResponseEntity.ok(out);
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Map<String, Integer>> removeItem(@PathVariable("productId") Long productId,
                                                           HttpSession session) {
        Set<Long> wl = getWishlist(session);
        if (productId != null) {
            wl.remove(productId);
            session.setAttribute(SESSION_KEY, wl);
        }
        Map<String, Integer> out = new HashMap<>();
        out.put("count", wl.size());
        return ResponseEntity.ok(out);
    }

    private Set<Long> getWishlist(HttpSession session) {
        @SuppressWarnings("unchecked")
        Set<Long> wl = (Set<Long>) session.getAttribute(SESSION_KEY);
        if (wl == null) {
            wl = new HashSet<>();
            session.setAttribute(SESSION_KEY, wl);
        }
        return wl;
    }
}

