package com.recicar.marketplace.controller;

import com.recicar.marketplace.service.OrderService;
import com.recicar.marketplace.service.UserService;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class UserDashboardController {

    private final UserService userService;
    private final OrderService orderService;

    public UserDashboardController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping({"/user-dashboard", "/dashboard"})
    public String dashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        return userService.findByEmail(userDetails.getUsername())
                .map(user -> {
                    model.addAttribute("user", user);
                    model.addAttribute("orders", orderService.findOrdersByCustomerId(user.getId(), PageRequest.of(0, 20)));
                    return "user-dashboard";
                })
                .orElse("redirect:/login");
    }

    /**
     * Full customer order history (HTML); complements JSON {@code GET /api/v1/user/orders}.
     */
    @GetMapping("/orders")
    public String customerOrders(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        return userService.findByEmail(userDetails.getUsername())
                .map(user -> {
                    model.addAttribute("user", user);
                    model.addAttribute("orders", orderService.findOrdersByCustomerId(user.getId(), PageRequest.of(0, 200)));
                    model.addAttribute("pageTitle", "Mis pedidos — ReciCar");
                    return "customer-orders";
                })
                .orElse("redirect:/login");
    }

    @GetMapping({"/user-dashboard/chat", "/messages"})
    public String chat(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails == null) {
            return "redirect:/login";
        }
        return userService.findByEmail(userDetails.getUsername())
                .map(user -> {
                    model.addAttribute("user", user);
                    return "user-dashboard-chat";
                })
                .orElse("redirect:/login");
    }
}
