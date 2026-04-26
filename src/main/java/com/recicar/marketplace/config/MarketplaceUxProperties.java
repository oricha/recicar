package com.recicar.marketplace.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Region list and price semantics for storefront display (IVA and país).
 */
@ConfigurationProperties(prefix = "app.market")
public class MarketplaceUxProperties {

    /**
     * Default region if cookies are missing.
     */
    private String defaultRegion = "ES";

    /**
     * IVA already included in {@code product.price} in the catalog (España).
     * Used to derive net, then re-apply target region IVA.
     */
    private BigDecimal referenceVat = new BigDecimal("0.21");

    private final List<Region> regions = new ArrayList<>();

    public String getDefaultRegion() {
        return defaultRegion;
    }

    public void setDefaultRegion(String defaultRegion) {
        this.defaultRegion = defaultRegion;
    }

    public BigDecimal getReferenceVat() {
        return referenceVat;
    }

    public void setReferenceVat(BigDecimal referenceVat) {
        this.referenceVat = referenceVat;
    }

    public List<Region> getRegions() {
        return regions;
    }

    public void setRegions(List<Region> regions) {
        this.regions.clear();
        if (regions != null) {
            this.regions.addAll(regions);
        }
    }

    public static class Region {
        private String code;
        private String name;
        private BigDecimal vat = BigDecimal.ZERO;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public BigDecimal getVat() {
            return vat;
        }

        public void setVat(BigDecimal vat) {
            this.vat = vat;
        }
    }
}
