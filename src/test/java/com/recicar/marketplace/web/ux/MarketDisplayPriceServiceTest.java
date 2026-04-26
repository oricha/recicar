package com.recicar.marketplace.web.ux;

import com.recicar.marketplace.config.MarketplaceUxProperties;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MarketDisplayPriceServiceTest {

    @Test
    void esGrossStaysWhenIncludeVatAndEsRegion() {
        MarketplaceUxProperties p = new MarketplaceUxProperties();
        p.getRegions().add(region("ES", "0.21"));
        p.setDefaultRegion("ES");
        p.setReferenceVat(new BigDecimal("0.21"));
        var svc = new MarketDisplayPriceService(p);
        BigDecimal display = svc.toDisplayAmount(
                new BigDecimal("121.00"),
                new ClientPreferences("ES", true),
                p);
        assertEquals(new BigDecimal("121.00"), display);
    }

    @Test
    void netWhenExcludeVat() {
        MarketplaceUxProperties p = new MarketplaceUxProperties();
        p.getRegions().add(region("ES", "0.21"));
        p.setReferenceVat(new BigDecimal("0.21"));
        var svc = new MarketDisplayPriceService(p);
        BigDecimal display = svc.toDisplayAmount(
                new BigDecimal("121.00"),
                new ClientPreferences("ES", false),
                p);
        // 121/1.21
        assertEquals(new BigDecimal("100.00"), display);
    }

    @Test
    void frVatReappliedFromNet() {
        MarketplaceUxProperties p = new MarketplaceUxProperties();
        p.getRegions().add(region("ES", "0.21"));
        p.getRegions().add(region("FR", "0.20"));
        p.setReferenceVat(new BigDecimal("0.21"));
        var svc = new MarketDisplayPriceService(p);
        // net(121) = 100, * 1.2 = 120
        BigDecimal display = svc.toDisplayAmount(
                new BigDecimal("121.00"),
                new ClientPreferences("FR", true),
                p);
        assertEquals(new BigDecimal("120.00"), display);
    }

    private static MarketplaceUxProperties.Region region(String code, String vat) {
        MarketplaceUxProperties.Region r = new MarketplaceUxProperties.Region();
        r.setCode(code);
        r.setVat(new BigDecimal(vat));
        return r;
    }
}
