package com.recicar.marketplace.web.search;

import java.util.function.IntFunction;

/**
 * Thymeleaf-friendly pagination links for advanced search (wrapper around {@link IntFunction}).
 */
public final class AdvancedSearchPageLinks {

    private final IntFunction<String> linker;

    public AdvancedSearchPageLinks(IntFunction<String> linker) {
        this.linker = linker;
    }

    public String page(int pageIndex) {
        return linker.apply(pageIndex);
    }
}
