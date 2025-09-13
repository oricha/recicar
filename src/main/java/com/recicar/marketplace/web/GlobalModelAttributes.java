package com.recicar.marketplace.web;

import com.recicar.marketplace.service.CustomUserDetailsService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Map;

@ControllerAdvice
public class GlobalModelAttributes {

    @ModelAttribute
    public void addSecurityAttributes(Map<String, Object> model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean authenticated = auth != null && auth.isAuthenticated() && !("anonymousUser".equals(String.valueOf(auth.getPrincipal())));
        model.put("isAuthenticated", authenticated);
        if (authenticated && auth.getPrincipal() instanceof CustomUserDetailsService.CustomUserPrincipal principal) {
            model.put("currentUser", principal.getUser());
        }
    }
}
