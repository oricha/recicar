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

        // Log the error for debugging
        System.err.println("Error occurred with status code: " + statusCode);
        System.err.println("Request URI: " + request.getRequestURI());
        System.err.println("Error message: " + request.getAttribute(RequestDispatcher.ERROR_MESSAGE));
        
        // Don't set status if response is already committed
        if (!response.isCommitted()) {
            if (statusCode == 0) {
                statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
            }
            response.setStatus(statusCode);
        }
        
        // Return appropriate error page based on status code
        if (statusCode == HttpStatus.NOT_FOUND.value()) {
            return "404";
        } else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
            return "500";
        } else {
            return "error";
        }
    }
}

