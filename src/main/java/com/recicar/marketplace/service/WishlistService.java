package com.recicar.marketplace.service;

import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.entity.User;
import com.recicar.marketplace.entity.WishlistItem;
import com.recicar.marketplace.repository.ProductRepository;
import com.recicar.marketplace.repository.UserRepository;
import com.recicar.marketplace.repository.WishlistItemRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Persisted wishlist for registered users; session wishlist is merged on login and kept in sync.
 */
@Service
public class WishlistService {

    public static final String SESSION_WISHLIST_KEY = "WISHLIST_PRODUCT_IDS";

    private final WishlistItemRepository wishlistItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public WishlistService(WishlistItemRepository wishlistItemRepository, UserRepository userRepository,
            ProductRepository productRepository) {
        this.wishlistItemRepository = wishlistItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public int countForUser(Long userId) {
        return wishlistItemRepository.countByUser_Id(userId);
    }

    @Transactional(readOnly = true)
    public Set<Long> productIdsForUser(Long userId) {
        return new LinkedHashSet<>(wishlistItemRepository.findProductIdsByUserId(userId));
    }

    @Transactional
    public int addItem(Long userId, Long productId) {
        if (productId == null || !productRepository.existsById(productId)) {
            return countForUser(userId);
        }
        if (wishlistItemRepository.findByUser_IdAndProduct_Id(userId, productId).isPresent()) {
            return wishlistItemRepository.countByUser_Id(userId);
        }
        User user = userRepository.findById(userId).orElseThrow();
        Product product = productRepository.getReferenceById(productId);
        WishlistItem item = new WishlistItem();
        item.setUser(user);
        item.setProduct(product);
        wishlistItemRepository.save(item);
        return wishlistItemRepository.countByUser_Id(userId);
    }

    @Transactional
    public int removeItem(Long userId, Long productId) {
        if (productId != null) {
            wishlistItemRepository.deleteByUser_IdAndProduct_Id(userId, productId);
        }
        return wishlistItemRepository.countByUser_Id(userId);
    }

    /**
     * Merges session wishlist IDs into the database for the user, then overwrites the session
     * with the union stored in the database.
     */
    @Transactional
    public void mergeSessionWishlistIntoDatabase(HttpSession session, Long userId) {
        if (session == null) {
            return;
        }
        @SuppressWarnings("unchecked")
        Set<Long> sessionIds = (Set<Long>) session.getAttribute(SESSION_WISHLIST_KEY);
        if (sessionIds != null) {
            for (Long productId : sessionIds) {
                if (productId != null && productRepository.existsById(productId)
                        && wishlistItemRepository.findByUser_IdAndProduct_Id(userId, productId).isEmpty()) {
                    addItem(userId, productId);
                }
            }
        }
        Set<Long> fromDb = productIdsForUser(userId);
        session.setAttribute(SESSION_WISHLIST_KEY, fromDb);
    }

    /**
     * Replaces the session set with the database copy (after add/remove in API).
     */
    @Transactional(readOnly = true)
    public void syncSessionWithDatabase(HttpSession session, Long userId) {
        if (session == null) {
            return;
        }
        Set<Long> fromDb = productIdsForUser(userId);
        session.setAttribute(SESSION_WISHLIST_KEY, new LinkedHashSet<>(fromDb));
    }
}
