package com.recicar.marketplace.web.ux;

import com.recicar.marketplace.config.MarketplaceUxProperties;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Locale;

/**
 * Thymeleaf helper: {@code @priceView.formatEur(clientPreferences, product.price)}
 * and {@code @priceView.plainEur(amount)} for amounts without regional IVA adjustment.
 */
@Component("priceView")
public class PriceViewHelper {

    private static final RoundingMode RM = RoundingMode.HALF_UP;

    private final MarketDisplayPriceService marketDisplayPriceService;
    private final MarketplaceUxProperties marketplaceUxProperties;

    public PriceViewHelper(MarketDisplayPriceService marketDisplayPriceService, MarketplaceUxProperties marketplaceUxProperties) {
        this.marketDisplayPriceService = marketDisplayPriceService;
        this.marketplaceUxProperties = marketplaceUxProperties;
    }

    public String formatEur(ClientPreferences clientPreferences, BigDecimal listedGross) {
        if (listedGross == null) {
            return "—";
        }
        ClientPreferences p = clientPreferences == null
                ? new ClientPreferences(
                        marketplaceUxProperties.getDefaultRegion() == null
                                ? "ES"
                                : marketplaceUxProperties.getDefaultRegion().toUpperCase(), true)
                : clientPreferences;
        BigDecimal amount = marketDisplayPriceService.toDisplayAmount(listedGross, p, marketplaceUxProperties);
        if (amount == null) {
            return "—";
        }
        return String.format(Locale.ROOT, "€%s", amount.toPlainString());
    }

    /**
     * Formats a stored amount as {@code €nnn.mm} using {@link Locale#ROOT} (ASCII digits, no currency symbol from locale).
     * Use for cart lines, checkout totals, vendor figures — not for catalog list prices (use {@link #formatEur}).
     */
    public String plainEur(BigDecimal amount) {
        if (amount == null) {
            return "€0.00";
        }
        return String.format(Locale.ROOT, "€%s", amount.setScale(2, RM).toPlainString());
    }
}
