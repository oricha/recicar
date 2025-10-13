package com.recicar.marketplace.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
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
        log.error("Error occurred with status code: {}, Request URI: {}, Error message: {}", 
                statusCode, request.getRequestURI(), request.getAttribute(RequestDispatcher.ERROR_MESSAGE));
        
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

