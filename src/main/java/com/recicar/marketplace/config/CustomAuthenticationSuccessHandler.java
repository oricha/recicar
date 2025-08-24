package com.recicar.marketplace.config;

import com.recicar.marketplace.entity.UserRole;
import com.recicar.marketplace.service.CustomUserDetailsService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import java.io.IOException;

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final RequestCache requestCache = new HttpSessionRequestCache();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException, ServletException {
        
        // Get the user details
        CustomUserDetailsService.CustomUserPrincipal userPrincipal = 
            (CustomUserDetailsService.CustomUserPrincipal) authentication.getPrincipal();
        
        UserRole userRole = userPrincipal.getUser().getRole();
        
        // Check if there was a saved request (user was trying to access a protected page)
        SavedRequest savedRequest = requestCache.getRequest(request, response);
        if (savedRequest != null) {
            String targetUrl = savedRequest.getRedirectUrl();
            // Clear the saved request
            requestCache.removeRequest(request, response);
            response.sendRedirect(targetUrl);
            return;
        }
        
        // Default redirect based on user role
        String redirectUrl = switch (userRole) {
            case ADMIN -> "/admin/dashboard";
            case VENDOR -> "/vendor/dashboard";
            case CUSTOMER -> "/";
        };
        
        response.sendRedirect(redirectUrl);
    }
}