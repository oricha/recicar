package com.recicar.marketplace.util;

import com.recicar.marketplace.entity.SavedSearch;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SavedSearchNavigationTest {

    @Test
    void toAdvancedSearchUrl_includesSubmittedQueryAndMergedFilters() {
        SavedSearch s = new SavedSearch();
        s.setSearchQuery("freno");
        s.setFiltersJson("{\"brand\":\"VW\",\"vehicleModel\":\"Golf\",\"q\":\"ignored\"}");

        String url = SavedSearchNavigation.toAdvancedSearchUrl(s);

        assertThat(url).startsWith("/search/advanced?");
        assertThat(url).contains("submitted=true");
        assertThat(url).contains("q=freno");
        assertThat(url).contains("brand=VW");
        assertThat(url).contains("vehicleModel=Golf");
    }

    @Test
    void toAdvancedSearchUrl_handlesInvalidJson() {
        SavedSearch s = new SavedSearch();
        s.setSearchQuery("x");
        s.setFiltersJson("not-json");

        String url = SavedSearchNavigation.toAdvancedSearchUrl(s);

        assertThat(url).contains("submitted=true");
        assertThat(url).contains("q=x");
    }
}
