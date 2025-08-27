package com.recicar.marketplace.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/test")
public class TestController {

    @GetMapping
    public String testPage(Model model) {
        model.addAttribute("message", "Test page is working!");
        return "test";
    }
    
    @GetMapping("/simple")
    public String simpleTest() {
        return "Simple test page without template";
    }
    
    @GetMapping("/json")
    @org.springframework.web.bind.annotation.ResponseBody
    public String jsonTest() {
        return "{\"message\": \"JSON test is working!\"}";
    }
}
