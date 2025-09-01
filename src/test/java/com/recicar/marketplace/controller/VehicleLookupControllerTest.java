package com.recicar.marketplace.controller;

import com.recicar.marketplace.repository.VehicleCompatibilityRepository;
import com.recicar.marketplace.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = VehicleLookupController.class)
class VehicleLookupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VehicleCompatibilityRepository vehicleCompatibilityRepository;
    
    @MockBean
    private CategoryService categoryService;

    @Test
    void getMakes_returnsList() throws Exception {
        when(vehicleCompatibilityRepository.findDistinctMakes()).thenReturn(List.of("toyota", "honda"));

        mockMvc.perform(get("/api/search/vehicles/makes"))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"toyota\",\"honda\"]"));
    }

    @Test
    void getModels_returnsList() throws Exception {
        when(vehicleCompatibilityRepository.findDistinctModelsByMake("toyota")).thenReturn(List.of("camry", "corolla"));

        mockMvc.perform(get("/api/search/vehicles/models").param("make", "toyota"))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"camry\",\"corolla\"]"));
    }

    @Test
    void getEngines_returnsList() throws Exception {
        when(vehicleCompatibilityRepository.findDistinctEnginesByMakeAndModel("toyota", "camry")).thenReturn(List.of("2.0 petrol", "2.5 hybrid"));

        mockMvc.perform(get("/api/search/vehicles/engines").param("make", "toyota").param("model", "camry"))
                .andExpect(status().isOk())
                .andExpect(content().json("[\"2.0 petrol\",\"2.5 hybrid\"]"));
    }
}

