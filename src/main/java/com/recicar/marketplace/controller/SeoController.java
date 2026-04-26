package com.recicar.marketplace.controller;

import com.recicar.marketplace.service.SitemapService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Robots and XML sitemap for search engines.
 */
@RestController
public class SeoController {

    private final SitemapService sitemapService;

    public SeoController(SitemapService sitemapService) {
        this.sitemapService = sitemapService;
    }

    @GetMapping(value = "/robots.txt", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> robots(HttpServletRequest request) {
        String base = ServletUriComponentsBuilder.fromContextPath(request).toUriString();
        String body = "User-agent: *\n"
                + "Disallow: /api/\n"
                + "Disallow: /user-dashboard\n"
                + "Disallow: /vendor/\n"
                + "Disallow: /admin/\n"
                + "Disallow: /cart\n"
                + "Disallow: /checkout\n"
                + "\n"
                + "Sitemap: " + base + "/sitemap.xml\n";
        return ResponseEntity.ok()
                .contentType(new MediaType(MediaType.TEXT_PLAIN, StandardCharsets.UTF_8))
                .body(body);
    }

    @GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
    public ResponseEntity<String> sitemap(HttpServletRequest request) {
        String base = ServletUriComponentsBuilder.fromContextPath(request).toUriString();
        List<String> paths = sitemapService.allPathsForSitemap();
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");
        for (String path : paths) {
            String loc = base + (path.startsWith("/") ? path : "/" + path);
            xml.append("  <url><loc>");
            xml.append(escapeXml(loc));
            xml.append("</loc></url>\n");
        }
        xml.append("</urlset>");
        return ResponseEntity.ok()
                .contentType(new MediaType(MediaType.APPLICATION_XML, StandardCharsets.UTF_8))
                .body(xml.toString());
    }

    private static String escapeXml(String s) {
        if (s == null) {
            return "";
        }
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
}
