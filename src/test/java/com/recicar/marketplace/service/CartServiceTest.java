package com.recicar.marketplace.service;

import com.recicar.marketplace.dto.CartDto;
import com.recicar.marketplace.entity.Cart;
import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.entity.User;
import com.recicar.marketplace.repository.CartRepository;
import com.recicar.marketplace.repository.ProductRepository;
import com.recicar.marketplace.repository.UserRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Disabled
public class CartServiceTest {

    @InjectMocks
    private CartServiceImpl cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetCart() {
        User user = new User();
        user.setId(1L);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setItems(new ArrayList<>());

        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));

        CartDto cartDto = cartService.getCart(1L);

        assertNotNull(cartDto);
        assertEquals(1L, cartDto.getUserId());
    }

    @Test
    public void testAddItemToCart() {
        User user = new User();
        user.setId(1L);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setItems(new ArrayList<>());

        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(new BigDecimal("10.00"));
        product.setStockQuantity(10);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(cartRepository.save(any(Cart.class))).thenAnswer(i -> i.getArguments()[0]);

        CartDto cartDto = cartService.addItemToCart(1L, 1L, 1);

        assertNotNull(cartDto);
        assertEquals(1, cartDto.getItems().size());
        assertEquals(new BigDecimal("10.00"), cartDto.getSubtotal());
    }

    @Test
    public void testAddItemToCart_notEnoughStock() {
        User user = new User();
        user.setId(1L);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setItems(new ArrayList<>());

        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(new BigDecimal("10.00"));
        product.setStockQuantity(0);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cartService.addItemToCart(1L, 1L, 1);
        });

        assertEquals("Not enough stock for product: Test Product", exception.getMessage());
    }

    @Test
    public void testAddItemToCart_quantityLimitExceeded() {
        User user = new User();
        user.setId(1L);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setItems(new ArrayList<>());

        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(new BigDecimal("10.00"));
        product.setStockQuantity(10);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserId(1L)).thenReturn(Optional.of(cart));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cartService.addItemToCart(1L, 1L, 11);
        });

        assertEquals("You can add a maximum of 10 items of the same product to the cart.", exception.getMessage());
    }

    @Test
    public void testUpdateItemInCart() {
        User user = new User();
        user.setId(1L);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setItems(new ArrayList<>());

        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(new BigDecimal("10.00"));
        product.setStockQuantity(10);

        // Add an item to the cart first
        cartService.addItemToCart(1L, 1L, 1);

        // Now update the item
        CartDto cartDto = cartService.updateItemInCart(1L, cart.getItems().get(0).getId(), 2);

        assertNotNull(cartDto);
        assertEquals(1, cartDto.getItems().size());
        assertEquals(new BigDecimal("20.00"), cartDto.getSubtotal());
    }

    @Test
    public void testUpdateItemInCart_notEnoughStock() {
        User user = new User();
        user.setId(1L);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setItems(new ArrayList<>());

        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(new BigDecimal("10.00"));
        product.setStockQuantity(1);

        // Add an item to the cart first
        cartService.addItemToCart(1L, 1L, 1);

        // Now try to update with more than stock
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cartService.updateItemInCart(1L, cart.getItems().get(0).getId(), 2);
        });

        assertEquals("Not enough stock for product: Test Product", exception.getMessage());
    }

    @Test
    public void testUpdateItemInCart_quantityLimitExceeded() {
        User user = new User();
        user.setId(1L);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setItems(new ArrayList<>());

        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(new BigDecimal("10.00"));
        product.setStockQuantity(10);

        // Add an item to the cart first
        cartService.addItemToCart(1L, 1L, 1);

        // Now try to update with more than limit
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cartService.updateItemInCart(1L, cart.getItems().get(0).getId(), 11);
        });

        assertEquals("You can add a maximum of 10 items of the same product to the cart.", exception.getMessage());
    }

    @Test
    public void testValidateCart() {
        User user = new User();
        user.setId(1L);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setItems(new ArrayList<>());

        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(new BigDecimal("10.00"));
        product.setStockQuantity(10);

        // Add an item to the cart
        cartService.addItemToCart(1L, 1L, 1);

        // Validate cart (should not throw exception)
        cartService.validateCart(1L);
    }

    @Test
    public void testValidateCart_notEnoughStock() {
        User user = new User();
        user.setId(1L);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setItems(new ArrayList<>());

        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(new BigDecimal("10.00"));
        product.setStockQuantity(0);

        // Add an item to the cart
        cartService.addItemToCart(1L, 1L, 1);

        // Validate cart (should throw exception)
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cartService.validateCart(1L);
        });

        assertEquals("Not enough stock for product: Test Product", exception.getMessage());
    }

    @Test
    public void testValidateCart_quantityLimitExceeded() {
        User user = new User();
        user.setId(1L);

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.setItems(new ArrayList<>());

        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(new BigDecimal("10.00"));
        product.setStockQuantity(10);

        // Add an item to the cart
        cartService.addItemToCart(1L, 1L, 11);

        // Validate cart (should throw exception)
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cartService.validateCart(1L);
        });

        assertEquals("You can add a maximum of 10 items of the same product to the cart.", exception.getMessage());
    }
}
