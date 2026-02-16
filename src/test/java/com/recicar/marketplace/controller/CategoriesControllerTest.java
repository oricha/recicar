package com.recicar.marketplace.controller;

import com.recicar.marketplace.entity.Category;
import com.recicar.marketplace.service.CategoryService;
import com.recicar.marketplace.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CategoriesController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoriesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private ProductService productService;

    @Test
    public void testCategories_WithoutCategoryParam_ShowsCategoriesPage() throws Exception {
        // Setup
        Category category1 = new Category();
        category1.setId(1L);
        category1.setName("Motor");
        category1.setSlug("motor");

        Category category2 = new Category();
        category2.setId(2L);
        category2.setName("Frenos");
        category2.setSlug("frenos");

        List<Category> categories = Arrays.asList(category1, category2);
        when(categoryService.findAllActive()).thenReturn(categories);

        // Execute & Verify
        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(view().name("categories"))
                .andExpect(model().attributeExists("categories"));
    }

    @Test
    public void testCategories_WithCategoryParam_RedirectsToSearch() throws Exception {
        // Execute & Verify
        mockMvc.perform(get("/categories").param("category", "motor"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/search/category?slug=motor"));
    }

    @Test
    public void testCategories_WithCategoryParamAndPage_RedirectsToSearchWithPage() throws Exception {
        // Execute & Verify
        mockMvc.perform(get("/categories")
                        .param("category", "motor")
                        .param("page", "2"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/search/category?slug=motor&page=2"));
    }

    @Test
    public void testCategories_WithEmptyCategoryParam_ShowsCategoriesPage() throws Exception {
        // Setup
        when(categoryService.findAllActive()).thenReturn(Collections.emptyList());

        // Execute & Verify
        mockMvc.perform(get("/categories").param("category", ""))
                .andExpect(status().isOk())
                .andExpect(view().name("categories"));
    }

    @Test
    public void testCategoryBySlug_RedirectsToSearch() throws Exception {
        // Execute & Verify
        mockMvc.perform(get("/categories/motor"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/search/category?slug=motor"));
    }

    @Test
    public void testCategoryBySlug_WithPage_RedirectsToSearchWithPage() throws Exception {
        // Execute & Verify
        mockMvc.perform(get("/categories/motor").param("page", "3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/search/category?slug=motor&page=3"));
    }

    @Test
    public void testCategoryBySlug_WithSubcategory_RedirectsToSearch() throws Exception {
        // Execute & Verify - test hierarchical category handling
        mockMvc.perform(get("/categories/motor-diesel"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/search/category?slug=motor-diesel"));
    }

    @Test
    public void testCategories_WithWhitespaceCategoryParam_ShowsCategoriesPage() throws Exception {
        // Setup
        when(categoryService.findAllActive()).thenReturn(Collections.emptyList());

        // Execute & Verify - whitespace-only category should be treated as empty
        mockMvc.perform(get("/categories").param("category", "   "))
                .andExpect(status().isOk())
                .andExpect(view().name("categories"));
    }

    @Test
    public void testCategories_NoCategories_ShowsEmptyPage() throws Exception {
        // Setup
        when(categoryService.findAllActive()).thenReturn(Collections.emptyList());

        // Execute & Verify
        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(view().name("categories"))
                .andExpect(model().attribute("categories", Collections.emptyList()));
    }
}
