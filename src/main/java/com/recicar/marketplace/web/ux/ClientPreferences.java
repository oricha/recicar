package com.recicar.marketplace.web.ux;

import java.util.Objects;

/**
 * Client storefront choices from cookies: EU region and IVA display mode.
 */
public final class ClientPreferences {

    public static final String COOKIE_REGION = "mkt_region";
    public static final String COOKIE_VAT = "mkt_vat";

    private final String regionCode;
    private final boolean includeVat;

    public ClientPreferences(String regionCode, boolean includeVat) {
        this.regionCode = regionCode;
        this.includeVat = includeVat;
    }

    public String getRegionCode() {
        return regionCode;
    }

    public boolean isIncludeVat() {
        return includeVat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ClientPreferences that = (ClientPreferences) o;
        return includeVat == that.includeVat && Objects.equals(regionCode, that.regionCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(regionCode, includeVat);
    }
}
