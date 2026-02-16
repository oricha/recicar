package com.recicar.marketplace.controller;

import com.recicar.marketplace.entity.Category;
import com.recicar.marketplace.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllers = CategoriesController.class)
@AutoConfigureMockMvc(addFilters = false)
class CategoriesControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Test
    void categoriesPageLoadsRootCategories() throws Exception {
        Category category = new Category();
        category.setId(1L);
        category.setName("Motor");
        category.setSlug("motor");

        when(categoryService.findRootCategories()).thenReturn(List.of(category));

        mockMvc.perform(get("/categories"))
                .andExpect(status().isOk())
                .andExpect(view().name("categories"))
                .andExpect(model().attributeExists("categories"))
                .andExpect(model().attribute("categories", List.of(category)));
    }

    @Test
    void categorySlugPathRedirectsToSearch() throws Exception {
        mockMvc.perform(get("/categories/motor"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/search?category=motor"));
    }
}
