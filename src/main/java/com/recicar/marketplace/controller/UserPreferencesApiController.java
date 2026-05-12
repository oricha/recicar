package com.recicar.marketplace.controller;

import com.recicar.marketplace.web.ux.ClientPreferences;
import com.recicar.marketplace.web.ux.ClientPreferencesService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/user/preferences")
public class UserPreferencesApiController {

    private static final int COOKIE_MAX_AGE = 60 * 60 * 24 * 400;

    private final ClientPreferencesService clientPreferencesService;

    public UserPreferencesApiController(ClientPreferencesService clientPreferencesService) {
        this.clientPreferencesService = clientPreferencesService;
    }

    @PostMapping("/region")
    public ResponseEntity<Map<String, Object>> updateRegion(
            @RequestBody Map<String, Object> body,
            HttpServletResponse response) {
        String region = body.get("region") == null ? "" : String.valueOf(body.get("region")).trim();
        if (!clientPreferencesService.isValidRegion(region)) {
            region = clientPreferencesService.defaultCode();
        } else {
            region = region.toUpperCase();
        }
        response.addCookie(preferenceCookie(ClientPreferences.COOKIE_REGION, region));
        return ResponseEntity.ok(Map.of("region", region));
    }

    @RequestMapping(value = "/price-format", method = {RequestMethod.POST, RequestMethod.PATCH})
    public ResponseEntity<Map<String, Object>> updatePriceFormat(
            @RequestBody Map<String, Object> body,
            HttpServletResponse response) {
        boolean includeVat = true;
        if (body.get("includeVat") != null) {
            Object value = body.get("includeVat");
            if (value instanceof Boolean bool) {
                includeVat = bool;
            } else {
                String str = String.valueOf(value);
                includeVat = !"0".equals(str) && !"false".equalsIgnoreCase(str);
            }
        }
        response.addCookie(preferenceCookie(ClientPreferences.COOKIE_VAT, includeVat ? "1" : "0"));
        return ResponseEntity.ok(Map.of("includeVat", includeVat));
    }

    private static Cookie preferenceCookie(String name, String value) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(COOKIE_MAX_AGE);
        cookie.setHttpOnly(false);
        return cookie;
    }
}
