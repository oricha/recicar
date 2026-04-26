package com.recicar.marketplace.controller;

import com.recicar.marketplace.entity.Category;
import com.recicar.marketplace.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CategoryApiController.class)
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
class CategoryApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Test
    void listRoots_ok() throws Exception {
        Category root = new Category();
        root.setId(1L);
        root.setName("Motor");
        root.setSlug("motor");
        root.setSortOrder(1);
        when(categoryService.findRootCategories()).thenReturn(List.of(root));
        when(categoryService.findByParentId(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].slug").value("motor"));
    }

    @Test
    void getBySlug_404() throws Exception {
        when(categoryService.findBySlug("nope")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/categories/nope"))
                .andExpect(status().isNotFound());
    }
}
