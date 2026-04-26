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
@RequestMapping("/user-dashboard")
public class UserDashboardController {

    private final UserService userService;
    private final OrderService orderService;

    public UserDashboardController(UserService userService, OrderService orderService) {
        this.userService = userService;
        this.orderService = orderService;
    }

    @GetMapping
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

    @GetMapping("/chat")
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
