package com.recicar.marketplace.controller;

import com.recicar.marketplace.dto.CartDto;
import com.recicar.marketplace.dto.CartItemDto;
import com.recicar.marketplace.entity.User;
import com.recicar.marketplace.repository.UserRepository;
import com.recicar.marketplace.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@org.springframework.test.context.ActiveProfiles("test")
@AutoConfigureMockMvc
class CartControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private com.recicar.marketplace.repository.ProductRepository productRepository;

    private User mockUser;
    private CartDto mockCart;

    @BeforeEach
    void setUp() {
        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("buyer@example.com");

        CartItemDto item = new CartItemDto();
        item.setId(10L);
        item.setProductId(100L);
        item.setProductName("Test Part");
        item.setQuantity(2);
        item.setPrice(new BigDecimal("19.99"));
        item.setImageUrl("/img/x.jpg");

        mockCart = new CartDto();
        mockCart.setId(5L);
        mockCart.setUserId(1L);
        mockCart.setItems(List.of(item));
        mockCart.setSubtotal(new BigDecimal("39.98"));

        when(userRepository.findByEmailIgnoreCase(mockUser.getEmail())).thenReturn(Optional.of(mockUser));
        when(cartService.addItemToCart(anyLong(), anyLong(), anyInt())).thenReturn(mockCart);
        doNothing().when(cartService).validateCart(anyLong());
    }

    @Test
    @WithMockUser(username = "buyer@example.com", roles = {"CUSTOMER"})
    void addItem_viaWebController_redirectsToCart() throws Exception {
        mockMvc.perform(post("/cart/items")
                        .param("productId", "100")
                        .param("quantity", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart"));
    }

    @Test
    void addItem_viaApi_asAnonymous_usesSessionCart() throws Exception {
        // Anonymous users can add to session cart - returns 200 with cart
        mockMvc.perform(post("/api/cart/items")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content("productId=100&quantity=1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "buyer@example.com", roles = {"CUSTOMER"})
    void addItem_viaApi_returnsCartJson() throws Exception {
        mockMvc.perform(post("/api/cart/items")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .content("productId=100&quantity=2"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.userId").value(1))
                .andExpect(jsonPath("$.items[0].productId").value(100))
                .andExpect(jsonPath("$.items[0].quantity").value(2));
    }

    @Test
    @WithMockUser(username = "buyer@example.com", roles = {"CUSTOMER"})
    void proceedToCheckout_redirectsToCheckout() throws Exception {
        mockMvc.perform(post("/cart/checkout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/checkout"));
    }

    @Test
    @WithMockUser(username = "buyer@example.com", roles = {"CUSTOMER"})
    void getCart_authenticated_rendersCartPage() throws Exception {
        when(cartService.getCart(anyLong())).thenReturn(mockCart);
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/cart"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Carrito de Compras")))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Test Part")));
    }

    @Test
    void getCart_anonymous_ok() throws Exception {
        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get("/cart"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Carrito de Compras")));
    }
}
