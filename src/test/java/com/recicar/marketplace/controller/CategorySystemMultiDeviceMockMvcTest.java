package com.recicar.marketplace.controller;

import com.recicar.marketplace.config.MvcSliceTestConfig;
import com.recicar.marketplace.entity.Brand;
import com.recicar.marketplace.entity.BrandModel;
import com.recicar.marketplace.entity.Category;
import com.recicar.marketplace.service.BrandService;
import com.recicar.marketplace.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

/**
 * Smoke coverage for OpenSpec tasks 1.8–5.8 (same pages/APIs under varied client User-Agents).
 * Does not replace manual checks on real devices; encodes viewport-ready markup and stable JSON in CI.
 */
@WebMvcTest(controllers = {CategoriesController.class, BrandsController.class, CategoryApiController.class})
@AutoConfigureMockMvc(addFilters = false)
@Import(MvcSliceTestConfig.class)
class CategorySystemMultiDeviceMockMvcTest {

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
    private CategoryService categoryService;

    @MockBean
    private BrandService brandService;

    @ParameterizedTest(name = "[1.8 main-categories] {0}")
    @MethodSource("devices")
    void mainCategoriesPage_ok_andViewportMeta(String device, String userAgent) throws Exception {
        Category root = sampleCategory(1L, "Motor", "motor");
        when(categoryService.findRootCategories()).thenReturn(List.of(root));

        mockMvc.perform(get("/categories").with(userAgent(userAgent)))
                .andExpect(status().isOk())
                .andExpect(view().name("categories"))
                .andExpect(content().string(containsString("viewport")));
    }

    @ParameterizedTest(name = "[2.8 hierarchical-subcategories] {0}")
    @MethodSource("devices")
    void categoryBrowse_subcategoriesAndBreadcrumb_markupStable(String device, String userAgent) throws Exception {
        Category motorBloque = sampleCategory(10L, "Motor — bloque", "motor-bloque");
        motorBloque.setDescription("Test category");
        Category sub = sampleCategory(11L, "Árbol de levas", "arbol-de-levas");
        when(categoryService.findBySlug("motor-bloque")).thenReturn(Optional.of(motorBloque));
        when(categoryService.findByParentId(10L)).thenReturn(List.of(sub));
        when(categoryService.getCategoryHierarchy(motorBloque)).thenReturn(List.of(motorBloque));

        mockMvc.perform(get("/categories/view/motor-bloque").with(userAgent(userAgent)))
                .andExpect(status().isOk())
                .andExpect(view().name("category-browse"))
                .andExpect(content().string(containsString("breadcrumbs_area")))
                .andExpect(content().string(containsString("arbol-de-levas")));
    }

    @ParameterizedTest(name = "[3.8 brand-navigation] {0}")
    @MethodSource("devices")
    void brandList_ok(String device, String userAgent) throws Exception {
        Brand brand = new Brand();
        brand.setId(1L);
        brand.setName("Toyota");
        brand.setSlug("toyota");
        when(brandService.findAll()).thenReturn(List.of(brand));

        mockMvc.perform(get("/marcas").with(userAgent(userAgent)))
                .andExpect(status().isOk())
                .andExpect(view().name("marcas"))
                .andExpect(content().string(containsString("viewport")))
                .andExpect(content().string(containsString("Toyota")));
    }

    @Test
    void marcas_secondPage_showsPaginationNav() throws Exception {
        List<Brand> many = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            Brand b = new Brand();
            b.setId((long) (i + 1));
            b.setName("Brand " + i);
            b.setSlug("brand-" + i);
            many.add(b);
        }
        when(brandService.findAll()).thenReturn(many);

        mockMvc.perform(get("/marcas").param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Paginación de marcas")))
                .andExpect(content().string(containsString("brand-48")));
    }

    @ParameterizedTest(name = "[4.8 model-selection] {0}")
    @MethodSource("devices")
    void brandDetail_listsModels(String device, String userAgent) throws Exception {
        Brand toyota = new Brand();
        toyota.setId(1L);
        toyota.setName("Toyota");
        toyota.setSlug("toyota");

        BrandModel camry = new BrandModel();
        camry.setId(100L);
        camry.setModelName("Camry");
        camry.setSlug("camry");
        camry.setBrand(toyota);

        when(brandService.findBySlug("toyota")).thenReturn(Optional.of(toyota));
        when(brandService.findByBrandId(1L)).thenReturn(List.of(camry));

        mockMvc.perform(get("/marcas/toyota").with(userAgent(userAgent)))
                .andExpect(status().isOk())
                .andExpect(view().name("marca-detail"))
                .andExpect(content().string(containsString("Camry")));
    }

    @ParameterizedTest(name = "[5.8 hierarchy-navigation] {0}")
    @MethodSource("devices")
    void categoryBreadcrumbApi_jsonStable(String device, String userAgent) throws Exception {
        Category parent = sampleCategory(1L, "Repuestos", "repuestos");
        Category leaf = sampleCategory(2L, "Motor — bloque", "motor-bloque");
        when(categoryService.findBySlug("motor-bloque")).thenReturn(Optional.of(leaf));
        when(categoryService.getCategoryHierarchy(leaf)).thenReturn(List.of(parent, leaf));

        mockMvc.perform(get("/api/v1/categories/motor-bloque/breadcrumb").with(userAgent(userAgent)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].slug").value("repuestos"))
                .andExpect(jsonPath("$[1].slug").value("motor-bloque"));
    }

    private static Category sampleCategory(long id, String name, String slug) {
        Category c = new Category();
        c.setId(id);
        c.setName(name);
        c.setSlug(slug);
        c.setSortOrder((int) id);
        return c;
    }

    private static RequestPostProcessor userAgent(String agent) {
        return request -> {
            request.addHeader(HttpHeaders.USER_AGENT, agent);
            return request;
        };
    }
}
