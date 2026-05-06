package com.recicar.marketplace.config;

import com.recicar.marketplace.web.GlobalModelAttributes;
import com.recicar.marketplace.web.ux.ClientPreferencesService;
import com.recicar.marketplace.web.ux.MarketDisplayPriceService;
import com.recicar.marketplace.web.ux.PriceViewHelper;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Shared imports for @WebMvcTest: company + market UX + {@link GlobalModelAttributes}
 * (header/footer fragments).
 */
@Configuration
@Import({
        CompanyConfiguration.class,
        MarketplaceUxConfiguration.class,
        ClientPreferencesService.class,
        MarketDisplayPriceService.class,
        PriceViewHelper.class,
        GlobalModelAttributes.class
})
public class MvcSliceTestConfig {
}
