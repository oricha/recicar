package com.recicar.marketplace.web.ux;

import com.recicar.marketplace.config.MarketplaceUxProperties;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ClientPreferencesService {

    private final MarketplaceUxProperties properties;
    private final Set<String> validCodes;

    public ClientPreferencesService(MarketplaceUxProperties properties) {
        this.properties = properties;
        this.validCodes = properties.getRegions().stream()
                .map(r -> r.getCode() == null ? null : r.getCode().toUpperCase())
                .filter(c -> c != null)
                .collect(Collectors.toSet());
    }

    public ClientPreferences resolve(HttpServletRequest request) {
        String def = defaultCode();
        String region = readCookie(request, ClientPreferences.COOKIE_REGION, def);
        if (region == null) {
            region = def;
        }
        region = region.toUpperCase();
        if (!validCodes.isEmpty() && !validCodes.contains(region)) {
            region = def;
        }
        String vat = readCookie(request, ClientPreferences.COOKIE_VAT, "1");
        boolean includeVat = !"0".equals(vat) && !"false".equalsIgnoreCase(vat);
        return new ClientPreferences(region, includeVat);
    }

    public boolean isValidRegion(String code) {
        if (code == null || code.isBlank()) {
            return false;
        }
        if (validCodes.isEmpty()) {
            return true;
        }
        return validCodes.contains(code.toUpperCase());
    }

    public String defaultCode() {
        String c = properties.getDefaultRegion();
        return c == null ? "ES" : c.toUpperCase();
    }

    public MarketplaceUxProperties getProperties() {
        return properties;
    }

    private static String readCookie(HttpServletRequest request, String name, String defaultValue) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return defaultValue;
        }
        for (Cookie c : cookies) {
            if (name.equals(c.getName()) && c.getValue() != null && !c.getValue().isBlank()) {
                return c.getValue();
            }
        }
        return defaultValue;
    }
}
