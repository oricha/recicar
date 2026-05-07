package com.recicar.marketplace.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.recicar.marketplace.entity.SavedSearch;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Builds the same {@code /search/advanced} query string that the advanced search form uses,
 * from persisted {@link SavedSearch} data.
 */
public final class SavedSearchNavigation {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private SavedSearchNavigation() {
    }

    public static String toAdvancedSearchUrl(SavedSearch saved) {
        UriComponentsBuilder b = UriComponentsBuilder.fromPath("/search/advanced")
                .queryParam("submitted", "true");
        String filtersJson = saved.getFiltersJson();
        if (filtersJson != null && !filtersJson.isBlank()) {
            try {
                JsonNode root = MAPPER.readTree(filtersJson);
                if (root.isObject()) {
                    root.fields().forEachRemaining(entry -> {
                        String key = entry.getKey();
                        if ("submitted".equals(key)) {
                            return;
                        }
                        JsonNode v = entry.getValue();
                        if (v == null || v.isNull()) {
                            return;
                        }
                        if (v.isTextual()) {
                            String t = v.asText();
                            if (!t.isBlank()) {
                                b.queryParam(key, t);
                            }
                        } else if (v.isNumber()) {
                            b.queryParam(key, v.asText());
                        } else if (v.isBoolean() && v.asBoolean()) {
                            b.queryParam(key, "true");
                        }
                    });
                }
            } catch (Exception ignored) {
                // Fallback: omit broken JSON; caller still gets submitted=true URL
            }
        }
        if (saved.getSearchQuery() != null && !saved.getSearchQuery().isBlank()) {
            b.replaceQueryParam("q", saved.getSearchQuery());
        }
        return b.encode().build().toUriString();
    }
}
