package com.recicar.marketplace.config;

import com.recicar.marketplace.web.ux.ClientPreferencesService;
import com.recicar.marketplace.web.ux.MarketDisplayPriceService;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Shared imports for @WebMvcTest: company + market UX (GlobalModelAttributes).
 */
@Configuration
@Import({
        CompanyConfiguration.class,
        MarketplaceUxConfiguration.class,
        ClientPreferencesService.class,
        MarketDisplayPriceService.class
})
public class MvcSliceTestConfig {
}
