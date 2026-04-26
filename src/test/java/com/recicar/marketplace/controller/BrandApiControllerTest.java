package com.recicar.marketplace.controller;

import com.recicar.marketplace.entity.Brand;
import com.recicar.marketplace.config.MvcSliceTestConfig;
import com.recicar.marketplace.service.BrandService;
import com.recicar.marketplace.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BrandApiController.class)
@org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc(addFilters = false)
@Import(MvcSliceTestConfig.class)
class BrandApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BrandService brandService;

    @MockBean
    private CategoryService categoryService;

    @Test
    void listBrands_ok() throws Exception {
        Brand b = new Brand();
        b.setId(1L);
        b.setName("Test");
        b.setSlug("test");
        b.setCountry("X");
        when(brandService.findAll()).thenReturn(List.of(b));

        mockMvc.perform(get("/api/v1/brands"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].slug").value("test"));
    }

    @Test
    void brandModels_404() throws Exception {
        when(brandService.findBySlug("missing")).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/brands/missing/models"))
                .andExpect(status().isNotFound());
    }
}
