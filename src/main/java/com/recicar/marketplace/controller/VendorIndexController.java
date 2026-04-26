package com.recicar.marketplace.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class VendorIndexController {

    @GetMapping("/vendor")
    public String root() {
        return "redirect:/vendor/dashboard";
    }
}
