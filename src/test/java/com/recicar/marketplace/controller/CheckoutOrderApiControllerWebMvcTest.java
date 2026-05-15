package com.recicar.marketplace.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recicar.marketplace.dto.OrderRequest;
import com.recicar.marketplace.dto.PaymentRequest;
import com.recicar.marketplace.dto.ShippingInfoRequest;
import com.recicar.marketplace.entity.Order;
import com.recicar.marketplace.entity.User;
import com.recicar.marketplace.repository.UserRepository;
import com.recicar.marketplace.service.CartService;
import com.recicar.marketplace.service.OrderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.method.annotation.AuthenticationPrincipalArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CheckoutOrderApiControllerWebMvcTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private OrderService orderService;

    @Mock
    private CartService cartService;

    @Mock
    private UserRepository userRepository;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(new CheckoutOrderApiController(orderService, cartService, userRepository))
                .setCustomArgumentResolvers(new AuthenticationPrincipalArgumentResolver())
                .build();
    }

    @AfterEach
    void clearSecurity() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void checkout_createsOrderAndClearsCart() throws Exception {
        UserDetails principal = org.springframework.security.core.userdetails.User
                .withUsername("buyer@test.com")
                .password("x")
                .roles("USER")
                .build();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));

        User u = new User();
        u.setId(77L);
        when(userRepository.findByEmailIgnoreCase("buyer@test.com")).thenReturn(Optional.of(u));

        Order saved = new Order();
        saved.setId(900L);
        saved.setOrderNumber("ORD-2026-000042");
        saved.setStatus(Order.OrderStatus.PENDING);
        saved.setTotalAmount(new BigDecimal("120.00"));
        when(orderService.createOrder(any(OrderRequest.class))).thenReturn(saved);

        OrderRequest body = new OrderRequest();
        body.setCustomerId(77L);
        body.setItems(List.of());
        ShippingInfoRequest ship = new ShippingInfoRequest();
        ship.setAddress("Calle 1");
        ship.setCity("Madrid");
        ship.setState("M");
        ship.setZipCode("28001");
        ship.setCountry("ES");
        body.setShippingInfo(ship);
        PaymentRequest pay = new PaymentRequest();
        pay.setPaymentMethod("VISA");
        body.setPayment(pay);

        mockMvc.perform(post("/api/v1/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderNumber").value("ORD-2026-000042"));

        verify(cartService).clearCart(eq(77L));
    }

    @Test
    void getOrder_returnsPayloadWhenOwned() throws Exception {
        UserDetails principal = org.springframework.security.core.userdetails.User
                .withUsername("buyer@test.com")
                .password("x")
                .roles("USER")
                .build();
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));

        User u = new User();
        u.setId(77L);
        when(userRepository.findByEmailIgnoreCase("buyer@test.com")).thenReturn(Optional.of(u));

        Order order = new Order();
        order.setId(3L);
        order.setOrderNumber("ORD-2026-000099");
        order.setStatus(Order.OrderStatus.PENDING);
        order.setSubtotal(BigDecimal.TEN);
        order.setServiceFee(BigDecimal.ZERO);
        order.setTaxAmount(BigDecimal.ZERO);
        order.setShippingAmount(BigDecimal.ZERO);
        order.setTotalAmount(BigDecimal.TEN);
        order.setItems(List.of());
        when(orderService.findOrderWithLinesForCustomer(eq(3L), eq(77L))).thenReturn(Optional.of(order));

        mockMvc.perform(get("/api/v1/orders/3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderNumber").value("ORD-2026-000099"));
    }

    @Test
    void checkout_requiresAuthentication() throws Exception {
        OrderRequest body = new OrderRequest();
        body.setCustomerId(1L);
        body.setItems(List.of());

        mockMvc.perform(post("/api/v1/checkout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isUnauthorized());
    }
}
