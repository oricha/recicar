package com.recicar.marketplace.controller;

import com.recicar.marketplace.dto.OrderSummaryResponse;
import com.recicar.marketplace.dto.ProfileUpdateRequest;
import com.recicar.marketplace.dto.UserProfileResponse;
import com.recicar.marketplace.entity.Order;
import com.recicar.marketplace.entity.User;
import com.recicar.marketplace.repository.UserRepository;
import com.recicar.marketplace.service.OrderService;
import com.recicar.marketplace.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * REST API for registered users: profile and purchase history.
 */
@RestController
@RequestMapping("/api/v1/user")
public class RegisteredUserApiController {

    private final UserRepository userRepository;
    private final UserService userService;
    private final OrderService orderService;

    public RegisteredUserApiController(UserRepository userRepository, UserService userService, OrderService orderService) {
        this.userRepository = userRepository;
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        return resolveUser(userDetails)
                .map(u -> ResponseEntity.<Object>ok(toProfile(u)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .<Object>body(Map.of("message", "Authentication required")));
    }

    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody ProfileUpdateRequest request) {
        User user = resolveUser(userDetails).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Authentication required"));
        }
        try {
            User updated = userService.updateAccountFromProfileRequest(user.getId(), request);
            return ResponseEntity.ok(toProfile(updated));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @GetMapping("/orders")
    public ResponseEntity<?> listOrders(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        User user = resolveUser(userDetails).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Authentication required"));
        }
        if (size > 100) {
            size = 100;
        }
        List<Order> orders = orderService.findOrdersByCustomerId(
                user.getId(), PageRequest.of(page, size));
        List<OrderSummaryResponse> body = orders.stream()
                .map(this::toOrderSummary)
                .collect(Collectors.toList());
        return ResponseEntity.ok(body);
    }

    private OrderSummaryResponse toOrderSummary(Order o) {
        return new OrderSummaryResponse(
                o.getId(),
                o.getOrderNumber(),
                o.getStatus(),
                o.getTotalAmount(),
                o.getCreatedAt()
        );
    }

    private UserProfileResponse toProfile(User u) {
        return new UserProfileResponse(
                u.getId(),
                u.getFirstName(),
                u.getLastName(),
                u.getEmail(),
                u.getPhone(),
                u.getRole()
        );
    }

    private java.util.Optional<User> resolveUser(UserDetails userDetails) {
        if (userDetails == null) {
            return java.util.Optional.empty();
        }
        return userRepository.findByEmailIgnoreCase(userDetails.getUsername());
    }
}
