package com.recicar.marketplace.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.Set;

/**
 * DPD is the intended carrier for EU-wide delivery. This class provides a thin placeholder:
 * no live API key required; use {@link CartService#calculateShippingCost} for the monetary quote.
 */
@Service
public class DpdShippingService {

    private static final Set<String> EU_COUNTRY_CODES = Set.of(
            "AT", "BE", "BG", "HR", "CY", "CZ", "DK", "EE", "FI", "FR", "DE", "GR", "HU", "IE", "IT",
            "LV", "LT", "LU", "MT", "NL", "PL", "PT", "RO", "SK", "SI", "ES", "SE", "CH", "NO", "IS", "LI"
    );

    @Value("${app.shipping.dpd.brand:ReciCar · envío con DPD}")
    private String brandLabel;

    public String getBrandLabel() {
        return brandLabel;
    }

    public boolean isEuDestination(String country) {
        if (country == null || country.isBlank()) {
            return false;
        }
        String c = country.trim();
        if (c.length() == 2) {
            return EU_COUNTRY_CODES.contains(c.toUpperCase(Locale.ROOT));
        }
        return c.toLowerCase(Locale.ROOT).contains("espa") || c.equalsIgnoreCase("España")
                || c.equalsIgnoreCase("Spain");
    }

    /**
     * Optional uplift when DPD is used (€); keep 0 until real tariff tables are wired.
     */
    public BigDecimal dpdSurcharge(String country) {
        return isEuDestination(country) ? BigDecimal.ZERO : BigDecimal.ZERO;
    }
}
