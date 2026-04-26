package com.recicar.marketplace.service;

import com.recicar.marketplace.repository.BlogPostRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Generates sitemap URL lists for /sitemap.xml.
 */
@Service
@Transactional(readOnly = true)
public class SitemapService {

    private static final List<String> STATIC_PATHS = List.of(
            "/",
            "/help",
            "/faq",
            "/contact",
            "/acerca-de-nosotros",
            "/blog",
            "/categories",
            "/lista-de-codigos-de-repuestos",
            "/equivalencia-neumaticos",
            "/info-de-envio",
            "/info-de-pago",
            "/politica-de-devolucion",
            "/terminos-de-uso",
            "/politica-de-privacidad",
            "/garantias"
    );

    private final BlogPostRepository blogPostRepository;

    public SitemapService(BlogPostRepository blogPostRepository) {
        this.blogPostRepository = blogPostRepository;
    }

    public List<String> allPathsForSitemap() {
        List<String> paths = new ArrayList<>(STATIC_PATHS);
        for (String slug : blogPostRepository.findAllPublishedSlugs()) {
            paths.add("/blog/" + slug);
        }
        return paths;
    }
}
