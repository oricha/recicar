package com.recicar.marketplace.controller;

import com.recicar.marketplace.dto.OrderRequest;
import com.recicar.marketplace.entity.Order;
import com.recicar.marketplace.repository.UserRepository;
import com.recicar.marketplace.service.CartService;
import com.recicar.marketplace.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class CheckoutOrderApiController {

    private final OrderService orderService;
    private final CartService cartService;
    private final UserRepository userRepository;

    public CheckoutOrderApiController(OrderService orderService, CartService cartService, UserRepository userRepository) {
        this.orderService = orderService;
        this.cartService = cartService;
        this.userRepository = userRepository;
    }

    @PostMapping("/checkout")
    public ResponseEntity<Map<String, Object>> checkout(
            @AuthenticationPrincipal UserDetails principal,
            @RequestBody OrderRequest body
    ) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        var user = userRepository.findByEmailIgnoreCase(principal.getUsername()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        if (body.getCustomerId() == null || !body.getCustomerId().equals(user.getId())) {
            return ResponseEntity.status(403).body(Map.of("error", "customerId does not match authenticated user"));
        }
        try {
            Order order = orderService.createOrder(body);
            cartService.clearCart(user.getId());
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("orderId", order.getId());
            payload.put("orderNumber", order.getOrderNumber());
            payload.put("status", order.getStatus().name());
            payload.put("totalAmount", order.getTotalAmount());
            return ResponseEntity.ok(payload);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/orders/{id}")
    public ResponseEntity<Map<String, Object>> getOrderForCustomer(
            @AuthenticationPrincipal UserDetails principal,
            @PathVariable Long id
    ) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        var user = userRepository.findByEmailIgnoreCase(principal.getUsername()).orElse(null);
        if (user == null) {
            return ResponseEntity.status(401).build();
        }
        return orderService.findOrderWithLinesForCustomer(id, user.getId())
                .map(this::toOrderPayload)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private Map<String, Object> toOrderPayload(Order o) {
        var lines = o.getItems().stream()
                .map(i -> {
                    Map<String, Object> line = new LinkedHashMap<>();
                    line.put("productId", i.getProduct().getId());
                    line.put("productName", i.getProduct().getName());
                    line.put("quantity", i.getQuantity());
                    line.put("unitPrice", i.getUnitPrice());
                    line.put("lineTotal", i.getTotalPrice());
                    return line;
                })
                .toList();

        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("id", o.getId());
        payload.put("orderNumber", o.getOrderNumber());
        payload.put("status", o.getStatus().name());
        payload.put("subtotal", o.getSubtotal());
        payload.put("serviceFee", o.getServiceFee());
        payload.put("taxAmount", o.getTaxAmount());
        payload.put("shippingAmount", o.getShippingAmount());
        payload.put("totalAmount", o.getTotalAmount());
        payload.put("items", lines);

        if (o.getShippingInfo() != null) {
            Map<String, Object> ship = new LinkedHashMap<>();
            ship.put("recipientName", o.getShippingInfo().getRecipientName());
            ship.put("city", o.getShippingInfo().getCity());
            ship.put("country", o.getShippingInfo().getCountry());
            ship.put("postalCode", o.getShippingInfo().getPostalCode());
            payload.put("shipping", ship);
        }
        if (o.getPayment() != null) {
            payload.put("paymentStatus", o.getPayment().getStatus().name());
            payload.put("paymentMethod", o.getPayment().getPaymentMethod());
        }
        return payload;
    }
}
