package com.recicar.marketplace.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.recicar.marketplace.dto.CategoryBreadcrumbDto;
import com.recicar.marketplace.dto.ProductDetailDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Structured data + short meta snippets for product detail SEO.
 */
@Component
public class ProductDetailSeoHelper {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String resolvePublicOrigin(HttpServletRequest request) {
        String schemeHeader = request.getHeader("X-Forwarded-Proto");
        String scheme = blankToNull(schemeHeader) != null ? schemeHeader : request.getScheme();
        String hostHeader = request.getHeader("X-Forwarded-Host");
        String host = blankToNull(hostHeader) != null ? stripPortFromForwardedHost(hostHeader) : request.getServerName();

        if (host == null || host.isBlank()) {
            host = "localhost";
        }

        boolean defaultHttps = "https".equalsIgnoreCase(scheme) && request.getServerPort() == 443;
        boolean defaultHttp = "http".equalsIgnoreCase(scheme) && request.getServerPort() == 80;
        boolean needsPort = !host.contains(":") && !defaultHttps && !defaultHttp && request.getServerPort() > 0;

        if (needsPort) {
            return scheme + "://" + host + ":" + request.getServerPort();
        }
        return scheme + "://" + host;
    }

    public String toAbsoluteUrl(String origin, String pathOrUrl) {
        if (pathOrUrl == null || pathOrUrl.isBlank()) {
            return "";
        }
        if (pathOrUrl.startsWith("http://") || pathOrUrl.startsWith("https://")) {
            return pathOrUrl;
        }
        String path = pathOrUrl.startsWith("/") ? pathOrUrl : "/" + pathOrUrl;
        String base = origin.endsWith("/") ? origin.substring(0, origin.length() - 1) : origin;
        return base + path;
    }

    public String metaDescription(ProductDetailDto detail) {
        if (detail == null || detail.getDescription() == null || detail.getDescription().isBlank()) {
            return detail != null ? detail.getTitle() : "";
        }
        String plain = detail.getDescription().replaceAll("<[^>]+>", " ").replaceAll("\\s+", " ").trim();
        if (plain.length() <= 160) {
            return plain;
        }
        return plain.substring(0, 157) + "…";
    }

    public String productJsonLd(ProductDetailDto d, String canonicalUrl) {
        if (d == null) {
            return "{}";
        }
        ObjectNode root = objectMapper.createObjectNode();
        root.put("@context", "https://schema.org");
        root.put("@type", "Product");
        root.put("name", d.getTitle());
        root.put("sku", d.getSku());
        root.put("mpn", d.getSku());
        root.put("description", metaDescription(d));
        root.put("url", canonicalUrl);
        if (d.getSellerName() != null) {
            ObjectNode brand = objectMapper.createObjectNode();
            brand.put("@type", "Brand");
            brand.put("name", d.getSellerName());
            root.set("brand", brand);
        }
        if (d.getProductImageUrls() != null && !d.getProductImageUrls().isEmpty()) {
            ArrayNode images = objectMapper.createArrayNode();
            d.getProductImageUrls().forEach(images::add);
            root.set("image", images);
        }
        ObjectNode offer = objectMapper.createObjectNode();
        offer.put("@type", "Offer");
        offer.put("priceCurrency", "EUR");
        if (d.getPrice() != null) {
            offer.put("price", d.getPrice().stripTrailingZeros().toPlainString());
        }
        offer.put("url", canonicalUrl);
        offer.put("availability", d.getStockQuantity() != null && d.getStockQuantity() > 0
                ? "https://schema.org/InStock" : "https://schema.org/OutOfStock");
        if (d.getSellerName() != null) {
            ObjectNode seller = objectMapper.createObjectNode();
            seller.put("@type", "Organization");
            seller.put("name", d.getSellerName());
            offer.set("seller", seller);
        }
        root.set("offers", offer);
        try {
            return objectMapper.writeValueAsString(root);
        } catch (Exception e) {
            return "{}";
        }
    }

    public String breadcrumbJsonLd(List<CategoryBreadcrumbDto> trail,
                                   String productTitle,
                                   String canonicalProductUrl,
                                   String origin) {
        ObjectNode root = objectMapper.createObjectNode();
        root.put("@context", "https://schema.org");
        root.put("@type", "BreadcrumbList");
        ArrayNode list = objectMapper.createArrayNode();
        int position = 1;

        list.add(breadcrumbEntry(position++, "Inicio", origin + "/"));

        list.add(breadcrumbEntry(position++, "Tienda", origin + "/shop-list"));

        if (trail != null) {
            for (CategoryBreadcrumbDto c : trail) {
                if (c.getSlug() == null || c.getSlug().isBlank()) {
                    continue;
                }
                String url = origin + "/shop-list?category=" + c.getSlug();
                list.add(breadcrumbEntry(position++, c.getName(), url));
            }
        }

        list.add(breadcrumbEntry(position, productTitle, canonicalProductUrl));
        root.set("itemListElement", list);
        try {
            return objectMapper.writeValueAsString(root);
        } catch (Exception e) {
            return "{}";
        }
    }

    private ObjectNode breadcrumbEntry(int position, String name, String url) {
        ObjectNode item = objectMapper.createObjectNode();
        item.put("@type", "ListItem");
        item.put("position", position);
        item.put("name", name);
        item.put("item", url);
        return item;
    }

    private static String blankToNull(String s) {
        return s == null || s.isBlank() ? null : s;
    }

    private static String stripPortFromForwardedHost(String hostHeader) {
        int comma = hostHeader.indexOf(',');
        String first = comma > 0 ? hostHeader.substring(0, comma).trim() : hostHeader.trim();
        int colon = first.indexOf(':');
        if (colon > 0 && first.indexOf(']') < 0) {
            return first.substring(0, colon);
        }
        return first;
    }
}
