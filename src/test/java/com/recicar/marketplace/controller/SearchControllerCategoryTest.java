package com.recicar.marketplace.controller;

import com.recicar.marketplace.entity.Category;
import com.recicar.marketplace.entity.Product;
import com.recicar.marketplace.service.CategoryService;
import com.recicar.marketplace.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;

@WebMvcTest(controllers = SearchController.class)
@AutoConfigureMockMvc(addFilters = false)
class SearchControllerCategoryTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private CategoryService categoryService;

    @Test
    public void testSearchByCategory_Success() throws Exception {
        // Setup
        Category category = new Category();
        category.setId(1L);
        category.setName("Motor");
        category.setSlug("motor");

        Product product = new Product();
        product.setId(1L);
        product.setName("Engine Part");
        product.setCategory(category);

        when(categoryService.findBySlug("motor")).thenReturn(Optional.of(category));
        when(productService.findByCategory(eq(category), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(product)));
        when(categoryService.findRootCategories()).thenReturn(Collections.emptyList());

        // Execute & Verify
        mockMvc.perform(get("/search/category").param("slug", "motor"))
                .andExpect(status().isOk())
                .andExpect(view().name("shop-list"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attributeExists("page"))
                .andExpect(model().attribute("category", category))
                .andExpect(model().attribute("categorySlug", "motor"))
                .andExpect(model().attribute("searchType", "category"));
    }

    @Test
    public void testSearchByCategoryThroughMainSearchEndpoint_Success() throws Exception {
        Category category = new Category();
        category.setId(1L);
        category.setName("Motor");
        category.setSlug("motor");

        Product product = new Product();
        product.setId(1L);
        product.setName("Engine Part");
        product.setCategory(category);

        when(categoryService.findBySlug("motor")).thenReturn(Optional.of(category));
        when(productService.findByCategory(eq(category), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(product)));
        when(categoryService.findRootCategories()).thenReturn(Collections.emptyList());
        when(categoryService.getCategoryHierarchy(category)).thenReturn(List.of(category));

        mockMvc.perform(get("/search").param("category", "motor"))
                .andExpect(status().isOk())
                .andExpect(view().name("shop-list"))
                .andExpect(model().attribute("searchType", "category"))
                .andExpect(model().attribute("categorySlug", "motor"));
    }

    @Test
    public void testSearchByCategory_WithPagination() throws Exception {
        // Setup
        Category category = new Category();
        category.setId(1L);
        category.setName("Motor");
        category.setSlug("motor");

        Product product = new Product();
        product.setId(1L);
        product.setName("Engine Part");
        product.setCategory(category);

        when(categoryService.findBySlug("motor")).thenReturn(Optional.of(category));
        when(productService.findByCategory(eq(category), eq(PageRequest.of(1, 12))))
                .thenReturn(new PageImpl<>(Collections.singletonList(product)));
        when(categoryService.findRootCategories()).thenReturn(Collections.emptyList());

        // Execute & Verify
        mockMvc.perform(get("/search/category")
                        .param("slug", "motor")
                        .param("page", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("shop-list"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attributeExists("page"));
    }

    @Test
    public void testSearchByCategory_CategoryNotFound() throws Exception {
        // Setup
        when(categoryService.findBySlug("invalid-slug")).thenReturn(Optional.empty());
        when(categoryService.findRootCategories()).thenReturn(Collections.emptyList());

        // Execute & Verify
        mockMvc.perform(get("/search/category").param("slug", "invalid-slug"))
                .andExpect(status().isOk())
                .andExpect(view().name("shop-list"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "Category not found"));
    }

    @Test
    public void testSearchByCategory_EmptySlug() throws Exception {
        // Setup
        when(categoryService.findRootCategories()).thenReturn(Collections.emptyList());

        // Execute & Verify
        mockMvc.perform(get("/search/category").param("slug", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("shop-list"))
                .andExpect(model().attributeExists("errorMessage"))
                .andExpect(model().attribute("errorMessage", "Category is required"));
    }

    @Test
    public void testSearchByCategory_Subcategory() throws Exception {
        // Setup
        Category parentCategory = new Category();
        parentCategory.setId(1L);
        parentCategory.setName("Repuestos");
        parentCategory.setSlug("repuestos");

        Category subcategory = new Category();
        subcategory.setId(2L);
        subcategory.setName("Motor");
        subcategory.setSlug("motor");
        subcategory.setParent(parentCategory);

        Product product = new Product();
        product.setId(1L);
        product.setName("Engine Part");
        product.setCategory(subcategory);

        when(categoryService.findBySlug("motor")).thenReturn(Optional.of(subcategory));
        when(productService.findByCategory(eq(subcategory), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(product)));
        when(categoryService.findRootCategories()).thenReturn(Collections.emptyList());
        when(categoryService.getCategoryHierarchy(subcategory)).thenReturn(List.of(parentCategory, subcategory));

        // Execute & Verify
        mockMvc.perform(get("/search/category").param("slug", "motor"))
                .andExpect(status().isOk())
                .andExpect(view().name("shop-list"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("category", subcategory))
                .andExpect(model().attribute("searchType", "category"));
    }

    @Test
    public void testSearchByParentCategory_UsesHierarchySearch() throws Exception {
        Category parentCategory = new Category();
        parentCategory.setId(1L);
        parentCategory.setName("Repuestos");
        parentCategory.setSlug("repuestos");

        Category childCategory = new Category();
        childCategory.setId(2L);
        childCategory.setName("Motor");
        childCategory.setSlug("motor");
        childCategory.setParent(parentCategory);
        parentCategory.setChildren(List.of(childCategory));

        Product product = new Product();
        product.setId(1L);
        product.setName("Engine Part");
        product.setCategory(childCategory);

        when(categoryService.findBySlug("repuestos")).thenReturn(Optional.of(parentCategory));
        when(productService.findByCategoryIds(eq(List.of(1L, 2L)), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(product)));
        when(categoryService.findRootCategories()).thenReturn(Collections.emptyList());
        when(categoryService.getCategoryHierarchy(parentCategory)).thenReturn(List.of(parentCategory));

        mockMvc.perform(get("/search").param("category", "repuestos"))
                .andExpect(status().isOk())
                .andExpect(view().name("shop-list"))
                .andExpect(model().attribute("searchType", "category"))
                .andExpect(model().attribute("selectedCategoryIds", List.of(1L, 2L)));

        verify(productService).findByCategoryIds(eq(List.of(1L, 2L)), any(PageRequest.class));
    }

    @Test
    public void testSearchByCategory_NoProducts() throws Exception {
        // Setup
        Category category = new Category();
        category.setId(1L);
        category.setName("Motor");
        category.setSlug("motor");

        when(categoryService.findBySlug("motor")).thenReturn(Optional.of(category));
        when(productService.findByCategory(eq(category), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.emptyList()));
        when(categoryService.findRootCategories()).thenReturn(Collections.emptyList());
        when(categoryService.getCategoryHierarchy(category)).thenReturn(List.of(category));

        // Execute & Verify
        mockMvc.perform(get("/search/category").param("slug", "motor"))
                .andExpect(status().isOk())
                .andExpect(view().name("shop-list"))
                .andExpect(model().attributeExists("products"))
                .andExpect(model().attribute("category", category));
    }
}
