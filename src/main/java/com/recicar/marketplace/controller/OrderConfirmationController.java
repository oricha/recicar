package com.recicar.marketplace.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/orders")
public class OrderConfirmationController {

    @GetMapping("/confirmation")
    public String orderConfirmation(@RequestParam("orderNumber") String orderNumber, Model model) {
        model.addAttribute("orderNumber", orderNumber);
        return "order-confirmation";
    }
}
