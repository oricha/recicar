package com.recicar.marketplace.controller;

import com.recicar.marketplace.service.SearchService;
import com.recicar.marketplace.web.ux.ClientPreferences;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import java.nio.charset.StandardCharsets;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Integration + multi-device smoke for OpenSpec technical-functionality (tasks 1.7–5.8).
 * Encodes stable viewport/markup and public JSON APIs across mobile, tablet, desktop UAs.
 */
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
class TechnicalFunctionalityMvcIntegrationTest {

    private static final String UA_MOBILE = "Mozilla/5.0 (iPhone; CPU iPhone OS 17_0 like Mac OS X) "
            + "AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.0 Mobile/15E148 Safari/604.1";
    private static final String UA_TABLET = "Mozilla/5.0 (iPad; CPU OS 17_0 like Mac OS X) "
            + "AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.0 Mobile/15E148 Safari/604.1";
    private static final String UA_DESKTOP = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
            + "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

    static Stream<Arguments> devices() {
        return Stream.of(
                Arguments.of("mobile", UA_MOBILE),
                Arguments.of("tablet", UA_TABLET),
                Arguments.of("desktop", UA_DESKTOP)
        );
    }

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SearchService searchService;

    @BeforeEach
    void stubSearchSuggestionsAvoidNativeQuery() {
        when(searchService.getSearchSuggestions(anyString())).thenReturn(List.of());
    }

    /** 1.8 + 2.8 + 3.8 (markup) + 4.8 (IVA control): new-base + global market bar + hamburger */
    @ParameterizedTest(name = "[responsive+hamburger+market bar] {0}")
    @MethodSource("devices")
    void faqPage_hasViewportTechnicalAssetsHamburgerAndMarketControls(String device, String userAgent)
            throws Exception {
        mockMvc.perform(get("/faq").with(userAgent(userAgent)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("viewport")))
                .andExpect(content().string(containsString("technical-marketplace.css")))
                .andExpect(content().string(containsString("hamburgerBtn")))
                .andExpect(content().string(containsString("mainNavRoot")))
                .andExpect(content().string(containsString("mkt_region_select")))
                .andExpect(content().string(containsString("mkt_vat_toggle")));
    }

    /** 5.8: advanced search shell + header autocomplete container */
    @ParameterizedTest(name = "[advanced-search html] {0}")
    @MethodSource("devices")
    void searchAdvancedPage_hasHeaderSearchAndAutocompleteHost(String device, String userAgent) throws Exception {
        mockMvc.perform(get("/search/advanced").with(userAgent(userAgent)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("viewport")))
                .andExpect(content().string(containsString("technical-marketplace.css")))
                .andExpect(content().string(containsString("header-search-input")))
                .andExpect(content().string(containsString("header-search-suggestions")))
                .andExpect(content().string(containsString("Búsqueda avanzada")));
    }

    /** 3.7 + 4.7: region list and VAT flag */
    @Test
    void clientPreferencesGet_returnsRegionsAndVat() throws Exception {
        mockMvc.perform(get("/api/v1/client-preferences").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.region").exists())
                .andExpect(jsonPath("$.includeVat").exists())
                .andExpect(jsonPath("$.regions").isArray())
                .andExpect(jsonPath("$.regions[0].code").exists())
                .andExpect(jsonPath("$.regions[0].name").exists());
    }

    /** 3.7 + 4.7: persisted cookies for region + VAT */
    @Test
    void clientPreferencesPost_setsRegionAndVatCookies() throws Exception {
        String body = "{\"region\":\"FR\",\"includeVat\":false}";
        MvcResult result = mockMvc.perform(post("/api/v1/client-preferences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body.getBytes(StandardCharsets.UTF_8)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.region").value("FR"))
                .andExpect(jsonPath("$.includeVat").value(false))
                .andReturn();
        Cookie[] cookies = result.getResponse().getCookies();
        assertThat(cookies)
                .filteredOn(c -> ClientPreferences.COOKIE_REGION.equals(c.getName()))
                .singleElement()
                .extracting(Cookie::getValue)
                .isEqualTo("FR");
        assertThat(cookies)
                .filteredOn(c -> ClientPreferences.COOKIE_VAT.equals(c.getName()))
                .singleElement()
                .extracting(Cookie::getValue)
                .isEqualTo("0");
    }

    /** 5.7: v1 suggestions (empty list OK if DB has no matches) */
    @Test
    void searchSuggestionsV1_returnsJsonArray() throws Exception {
        mockMvc.perform(get("/api/v1/search/suggestions").param("q", "bo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    /** 5.7: compat path used by storefront scripts */
    @Test
    void searchSuggestionsLegacyPath_returnsJsonArray() throws Exception {
        mockMvc.perform(get("/api/search/suggestions").param("q", "bo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    private static RequestPostProcessor userAgent(String agent) {
        return request -> {
            request.addHeader(HttpHeaders.USER_AGENT, agent);
            return request;
        };
    }
}
