package com.recicar.marketplace.web.ux;

import com.recicar.marketplace.config.MarketplaceUxProperties;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PriceViewHelperTest {

    @Test
    void plainEurUsesAsciiDigitsAndEuroPrefix() {
        MarketplaceUxProperties props = new MarketplaceUxProperties();
        MarketDisplayPriceService m = new MarketDisplayPriceService(props);
        PriceViewHelper h = new PriceViewHelper(m, props);
        assertEquals("€12.50", h.plainEur(new BigDecimal("12.5")));
        assertEquals("€0.00", h.plainEur(null));
    }
}
