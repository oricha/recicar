package com.recicar.marketplace.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, HttpServletResponse response) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        int statusCode = 0;
        if (status != null) {
            try {
                statusCode = Integer.parseInt(status.toString());
            } catch (NumberFormatException ignored) {}
        }

        // Always present the custom 404 page; set status appropriately
        response.setStatus(HttpStatus.NOT_FOUND.value());
        return "404";
    }
}

