package com.recicar.marketplace.service;

import com.recicar.marketplace.dto.CartDto;
import com.recicar.marketplace.dto.CartItemDto;
import com.recicar.marketplace.entity.Cart;
import com.recicar.marketplace.entity.CartItem;
import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.entity.User;
import com.recicar.marketplace.repository.CartRepository;
import com.recicar.marketplace.repository.ProductRepository;
import com.recicar.marketplace.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartServiceImpl(CartRepository cartRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public CartDto getCart(Long userId) {
        Cart cart = getCartForUser(userId);
        return toDto(cart);
    }

    @Override
    @Transactional
    public CartDto addItemToCart(Long userId, Long productId, int quantity) {
        Cart cart = getCartForUser(userId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (product.getStockQuantity() < quantity) {
            throw new RuntimeException("Not enough stock for product: " + product.getName());
        }

        if (quantity > 10) {
            throw new RuntimeException("You can add a maximum of 10 items of the same product to the cart.");
        }

        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            cartItem = new CartItem();
            cartItem.setCart(cart);
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cart.getItems().add(cartItem);
        }

        cartRepository.save(cart);
        return toDto(cart);
    }

    @Override
    @Transactional
    public CartDto updateItemInCart(Long userId, Long itemId, int quantity) {
        Cart cart = getCartForUser(userId);
        CartItem cartItem = cart.getItems().stream()
                .filter(item -> item.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Item not found in cart"));

        if (cartItem.getProduct().getStockQuantity() < quantity) {
            throw new RuntimeException("Not enough stock for product: " + cartItem.getProduct().getName());
        }

        if (quantity > 10) {
            throw new RuntimeException("You can add a maximum of 10 items of the same product to the cart.");
        }

        cartItem.setQuantity(quantity);
        cartRepository.save(cart);
        return toDto(cart);
    }

    @Override
    @Transactional
    public void removeItemFromCart(Long userId, Long itemId) {
        Cart cart = getCartForUser(userId);
        cart.getItems().removeIf(item -> item.getId().equals(itemId));
        cartRepository.save(cart);
    }

    @Override
    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getCartForUser(userId);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    @Override
    @Transactional(readOnly = true)
    public void validateCart(Long userId) {
        Cart cart = getCartForUser(userId);
        for (CartItem item : cart.getItems()) {
            if (item.getProduct().getStockQuantity() < item.getQuantity()) {
                throw new RuntimeException("Not enough stock for product: " + item.getProduct().getName());
            }
            if (item.getQuantity() > 10) {
                throw new RuntimeException("You can add a maximum of 10 items of the same product to the cart.");
            }
        }
    }

    private Cart getCartForUser(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    newCart.setItems(new java.util.ArrayList<>());
                    return cartRepository.save(newCart);
                });
    }

    private CartDto toDto(Cart cart) {
        CartDto dto = new CartDto();
        dto.setId(cart.getId());
        dto.setUserId(cart.getUser().getId());
        dto.setItems(cart.getItems().stream().map(this::toDto).collect(Collectors.toList()));
        dto.setSubtotal(cart.getItems().stream()
                .map(item -> item.getProduct().getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add));
        return dto;
    }

    private CartItemDto toDto(CartItem cartItem) {
        CartItemDto dto = new CartItemDto();
        dto.setId(cartItem.getId());
        dto.setProductId(cartItem.getProduct().getId());
        dto.setProductName(cartItem.getProduct().getName());
        dto.setQuantity(cartItem.getQuantity());
        dto.setPrice(cartItem.getProduct().getPrice());
        // Resolve a safe image URL (avoid NPE if no images)
        String imageUrl = null;
        if (cartItem.getProduct() != null) {
            var primary = cartItem.getProduct().getPrimaryImage();
            if (primary != null) {
                imageUrl = primary.getImageUrl();
            }
        }
        if (imageUrl == null) {
            imageUrl = "/assets/img/product/product1.jpg"; // fallback placeholder
        }
        dto.setImageUrl(imageUrl);
        return dto;
    }

    @Override
    public BigDecimal calculateShippingCost(Long userId, String country, String state, String zipCode) {
        // Simulate shipping cost calculation based on location or cart total
        // For simplicity, return a flat rate or a rate based on total amount
        Cart cart = getCartForUser(userId);
        BigDecimal subtotal = cart.getItems().stream()
                .map(item -> item.getProduct().getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (subtotal.compareTo(BigDecimal.valueOf(100)) < 0) {
            return BigDecimal.valueOf(10.00); // $10 shipping for orders under $100
        } else {
            return BigDecimal.valueOf(0.00); // Free shipping for orders $100 and above
        }
    }
}
