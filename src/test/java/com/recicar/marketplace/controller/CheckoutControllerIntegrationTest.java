package com.recicar.marketplace.controller;

import com.recicar.marketplace.dto.CartDto;
import com.recicar.marketplace.dto.CheckoutForm;
import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.entity.User;
import com.recicar.marketplace.repository.ProductRepository;
import com.recicar.marketplace.repository.UserRepository;
import com.recicar.marketplace.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
public class CheckoutControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @MockBean
    private ProductRepository productRepository;

    @MockBean
    private UserRepository userRepository;

    private User mockUser;
    private Product mockProduct;
    private CartDto mockCartDto;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@example.com");

        mockProduct = new Product();
        mockProduct.setId(1L);
        mockProduct.setName("Test Product");
        mockProduct.setPrice(new BigDecimal("10.00"));
        mockProduct.setStockQuantity(10);

        mockCartDto = new CartDto();
        mockCartDto.setUserId(1L);
        mockCartDto.setSubtotal(new BigDecimal("10.00"));

        when(userRepository.findById(anyLong())).thenReturn(Optional.of(mockUser));
        when(productRepository.findById(anyLong())).thenReturn(Optional.of(mockProduct));
        when(cartService.getCart(anyLong())).thenReturn(mockCartDto);
        when(cartService.calculateShippingCost(anyLong(), anyString(), anyString(), anyString())).thenReturn(new BigDecimal("5.00"));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"CUSTOMER"})
    void testCheckoutPage() throws Exception {
        mockMvc.perform(get("/checkout"))
                .andExpect(status().isOk())
                .andExpect(view().name("checkout"));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"CUSTOMER"})
    void testPlaceOrder() throws Exception {
        CheckoutForm checkoutForm = new CheckoutForm();
        checkoutForm.setFirstName("John");
        checkoutForm.setLastName("Doe");
        checkoutForm.setAddress("123 Main St");
        checkoutForm.setCity("Anytown");
        checkoutForm.setState("CA");
        checkoutForm.setZipCode("12345");
        checkoutForm.setCountry("USA");
        checkoutForm.setPaymentMethod("creditCard");

        mockMvc.perform(post("/checkout")
                        .flashAttr("checkoutForm", checkoutForm))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/orders/confirmation?orderNumber=*"));
    }

    @Test
    @WithMockUser(username = "test@example.com", roles = {"CUSTOMER"})
    void testCustomerPurchaseFlow() throws Exception {
        // 1. Add product to cart
        mockMvc.perform(post("/cart/items")
                        .param("productId", "1")
                        .param("quantity", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/cart"));

        // 2. Proceed to checkout page
        mockMvc.perform(get("/checkout"))
                .andExpect(status().isOk())
                .andExpect(view().name("checkout"));

        // 3. Place order
        CheckoutForm checkoutForm = new CheckoutForm();
        checkoutForm.setFirstName("John");
        checkoutForm.setLastName("Doe");
        checkoutForm.setAddress("123 Main St");
        checkoutForm.setCity("Anytown");
        checkoutForm.setState("CA");
        checkoutForm.setZipCode("12345");
        checkoutForm.setCountry("USA");
        checkoutForm.setPaymentMethod("creditCard");

        mockMvc.perform(post("/checkout")
                        .flashAttr("checkoutForm", checkoutForm))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/orders/confirmation?orderNumber=*"));
    }
}
