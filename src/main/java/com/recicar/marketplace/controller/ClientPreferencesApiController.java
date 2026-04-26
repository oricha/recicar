package com.recicar.marketplace.controller;

import com.recicar.marketplace.web.ux.ClientPreferences;
import com.recicar.marketplace.web.ux.ClientPreferencesService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/client-preferences")
public class ClientPreferencesApiController {

    private static final int COOKIE_MAX_AGE = 60 * 60 * 24 * 400;

    private final ClientPreferencesService clientPreferencesService;

    public ClientPreferencesApiController(ClientPreferencesService clientPreferencesService) {
        this.clientPreferencesService = clientPreferencesService;
    }

    @GetMapping
    public Map<String, Object> getCurrent(HttpServletRequest request) {
        ClientPreferences p = clientPreferencesService.resolve(request);
        var props = clientPreferencesService.getProperties();
        List<Map<String, String>> regions = props.getRegions().stream()
                .map(r -> {
                    Map<String, String> m = new HashMap<>();
                    m.put("code", r.getCode() != null ? r.getCode() : "");
                    m.put("name", r.getName() != null ? r.getName() : "");
                    return m;
                })
                .collect(Collectors.toList());
        return Map.of(
                "region", p.getRegionCode(),
                "includeVat", p.isIncludeVat(),
                "regions", regions
        );
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> update(
            @RequestBody Map<String, Object> body,
            HttpServletResponse response) {
        String region = null;
        if (body.get("region") != null) {
            region = String.valueOf(body.get("region")).trim();
        }
        if (region == null || region.isEmpty() || !clientPreferencesService.isValidRegion(region)) {
            region = clientPreferencesService.defaultCode();
        } else {
            region = region.toUpperCase();
        }
        boolean includeVat = true;
        if (body.get("includeVat") != null) {
            Object v = body.get("includeVat");
            if (v instanceof Boolean b) {
                includeVat = b;
            } else {
                String s = String.valueOf(v);
                includeVat = !"0".equals(s) && !"false".equalsIgnoreCase(s);
            }
        }
        response.addCookie(secureStyleCookie(ClientPreferences.COOKIE_REGION, region, COOKIE_MAX_AGE));
        response.addCookie(secureStyleCookie(ClientPreferences.COOKIE_VAT, includeVat ? "1" : "0", COOKIE_MAX_AGE));
        return ResponseEntity.status(HttpStatus.OK).body(Map.of(
                "region", region,
                "includeVat", includeVat
        ));
    }

    private static Cookie secureStyleCookie(String name, String value, int maxAge) {
        Cookie c = new Cookie(name, value);
        c.setPath("/");
        c.setMaxAge(maxAge);
        c.setHttpOnly(false);
        return c;
    }
}
