package com.recicar.marketplace.web;

import com.recicar.marketplace.config.CompanyInfoProperties;
import com.recicar.marketplace.service.CustomUserDetailsService;
import com.recicar.marketplace.web.ux.ClientPreferencesService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Map;

@ControllerAdvice
public class GlobalModelAttributes {

    @Value("${app.support.email:help@recicar.es}")
    private String supportEmail;

    private final CompanyInfoProperties companyInfoProperties;
    private final ClientPreferencesService clientPreferencesService;

    public GlobalModelAttributes(
            CompanyInfoProperties companyInfoProperties,
            ClientPreferencesService clientPreferencesService) {
        this.companyInfoProperties = companyInfoProperties;
        this.clientPreferencesService = clientPreferencesService;
    }

    @ModelAttribute
    public void addSupportAttributes(Map<String, Object> model) {
        model.put("supportEmail", supportEmail);
    }

    @ModelAttribute
    public void addCompanyAttributes(Map<String, Object> model) {
        model.put("company", companyInfoProperties);
    }

    @ModelAttribute
    public void addSecurityAttributes(Map<String, Object> model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean authenticated = auth != null && auth.isAuthenticated() && !("anonymousUser".equals(String.valueOf(auth.getPrincipal())));
        model.put("isAuthenticated", authenticated);
        if (authenticated && auth.getPrincipal() instanceof CustomUserDetailsService.CustomUserPrincipal principal) {
            model.put("currentUser", principal.getUser());
        }
    }

    @ModelAttribute
    public void addMarketplaceUxAttributes(HttpServletRequest request, Map<String, Object> model) {
        model.put("clientPreferences", clientPreferencesService.resolve(request));
        model.put("marketRegions", clientPreferencesService.getProperties().getRegions());
    }
}
