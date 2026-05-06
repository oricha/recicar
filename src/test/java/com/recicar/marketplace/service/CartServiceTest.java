package com.recicar.marketplace.service;

import com.recicar.marketplace.dto.CartDto;
import com.recicar.marketplace.entity.Cart;
import com.recicar.marketplace.entity.CartItem;
import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.entity.User;
import com.recicar.marketplace.repository.CartRepository;
import com.recicar.marketplace.repository.ProductRepository;
import com.recicar.marketplace.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CartPricingService cartPricingService;

    @InjectMocks
    private CartServiceImpl cartService;

    private User user;
    private Cart cart;
    private Product product;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);

        cart = new Cart();
        cart.setId(10L);
        cart.setUser(user);
        cart.setItems(new ArrayList<>());

        product = new Product();
        product.setId(100L);
        product.setName("Alternador Bosch");
        product.setPrice(new BigDecimal("15.00"));
        product.setStockQuantity(8);
    }

    @Test
    void getCart_usesStoredSnapshotPriceForLineAndSubtotal() {
        CartItem item = new CartItem();
        item.setId(501L);
        item.setCart(cart);
        item.setProduct(product);
        item.setQuantity(2);
        item.setPrice(new BigDecimal("12.00"));
        cart.getItems().add(item);

        product.setPrice(new BigDecimal("18.50"));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        CartDto dto = cartService.getCart(1L);

        assertEquals(new BigDecimal("24.00"), dto.getSubtotal());
        assertEquals(new BigDecimal("12.00"), dto.getItems().get(0).getPrice());
        verify(cartPricingService).applyPricing(eq(dto), eq(1L), eq(null), eq(null), eq(null));
    }

    @Test
    void addItemToCart_capturesCurrentProductPriceAsSnapshot() {
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(100L)).thenReturn(Optional.of(product));

        CartDto dto = cartService.addItemToCart(1L, 100L, 2);

        assertEquals(1, cart.getItems().size());
        assertEquals(new BigDecimal("15.00"), cart.getItems().get(0).getPrice());
        assertEquals(new BigDecimal("30.00"), dto.getSubtotal());
        assertEquals(new BigDecimal("15.00"), dto.getItems().get(0).getPrice());
    }

    @Test
    void addItemToCart_rejectsWhenStockIsInsufficient() {
        product.setStockQuantity(1);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(100L)).thenReturn(Optional.of(product));

        RuntimeException error = assertThrows(RuntimeException.class,
                () -> cartService.addItemToCart(1L, 100L, 2));

        assertEquals("Not enough stock for product: Alternador Bosch", error.getMessage());
    }

    @Test
    void updateItemInCart_rejectsWhenQuantityExceedsLimit() {
        product.setStockQuantity(20);
        CartItem item = new CartItem();
        item.setId(501L);
        item.setCart(cart);
        item.setProduct(product);
        item.setQuantity(1);
        item.setPrice(new BigDecimal("15.00"));
        cart.getItems().add(item);
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        RuntimeException error = assertThrows(RuntimeException.class,
                () -> cartService.updateItemInCart(1L, 501L, 11));

        assertEquals("You can add a maximum of 10 items of the same product to the cart.", error.getMessage());
    }
}
