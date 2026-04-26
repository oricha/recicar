package com.recicar.marketplace.web.ux;

import com.recicar.marketplace.config.MarketplaceUxProperties;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Converts stored catalog price (gross, IVA de referencia) to amount shown for region/IVA toggle.
 */
@Service
public class MarketDisplayPriceService {

    private static final RoundingMode RM = RoundingMode.HALF_UP;
    private static final int SCALE = 2;

    private final Map<String, BigDecimal> vatByCode;

    public MarketDisplayPriceService(MarketplaceUxProperties properties) {
        this.vatByCode = properties.getRegions().stream()
                .filter(r -> r.getCode() != null && r.getVat() != null)
                .collect(Collectors.toMap(
                        r -> r.getCode().toUpperCase(),
                        MarketplaceUxProperties.Region::getVat,
                        (a, b) -> a));
    }

    public BigDecimal toDisplayAmount(BigDecimal listedGross, ClientPreferences preferences, MarketplaceUxProperties config) {
        if (listedGross == null) {
            return null;
        }
        BigDecimal refVat = config.getReferenceVat() == null
                ? BigDecimal.ZERO
                : config.getReferenceVat();
        if (refVat.compareTo(BigDecimal.ZERO) < 0) {
            refVat = BigDecimal.ZERO;
        }
        // Net from catalog (price assumed IVA ref included)
        BigDecimal denom = BigDecimal.ONE.add(refVat);
        if (denom.compareTo(BigDecimal.ZERO) == 0) {
            return listedGross.setScale(SCALE, RM);
        }
        BigDecimal net = listedGross.divide(denom, 8, RM);
        if (!preferences.isIncludeVat()) {
            return net.setScale(SCALE, RM);
        }
        BigDecimal regionVat = vatByCode.getOrDefault(
                preferences.getRegionCode() == null ? "" : preferences.getRegionCode().toUpperCase(),
                refVat);
        if (regionVat == null) {
            regionVat = refVat;
        }
        if (regionVat.compareTo(BigDecimal.ZERO) < 0) {
            regionVat = BigDecimal.ZERO;
        }
        return net.multiply(BigDecimal.ONE.add(regionVat)).setScale(SCALE, RM);
    }
}
