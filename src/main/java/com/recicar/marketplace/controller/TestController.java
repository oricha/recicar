package com.recicar.marketplace.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class TestController {

    @GetMapping("/test")
    public String test() {
        return "test";
    }

    @GetMapping("/test/json")
    @ResponseBody
    public String testJson() {
        return "{\"status\": \"ok\", \"message\": \"Static resources should be working now\"}";
    }

    @GetMapping("/test/css")
    @ResponseBody
    public String testCss() {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <title>CSS Test</title>
                <link rel="stylesheet" href="/assets/css/bootstrap.min.css">
                <link rel="stylesheet" href="/assets/css/style.css">
            </head>
            <body>
                <div class="container">
                    <h1 class="text-primary">CSS Test Page</h1>
                    <p class="text-success">If you see this styled, CSS is working!</p>
                    <button class="btn btn-primary">Bootstrap Button</button>
                </div>
            </body>
            </html>
            """;
    }
}
