package com.recicar.marketplace.controller;

import com.recicar.marketplace.repository.VehicleCompatibilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search/vehicles")
@RequiredArgsConstructor
public class VehicleLookupController {

    private final VehicleCompatibilityRepository vehicleCompatibilityRepository;

    @GetMapping("/makes")
    public ResponseEntity<List<String>> getMakes() {
        return ResponseEntity.ok(vehicleCompatibilityRepository.findDistinctMakes());
    }

    @GetMapping("/models")
    public ResponseEntity<List<String>> getModels(@RequestParam("make") String make) {
        if (make == null || make.isBlank()) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(vehicleCompatibilityRepository.findDistinctModelsByMake(make.trim()));
    }

    @GetMapping("/engines")
    public ResponseEntity<List<String>> getEngines(@RequestParam("make") String make,
                                                   @RequestParam("model") String model) {
        if (make == null || make.isBlank() || model == null || model.isBlank()) {
            return ResponseEntity.ok(List.of());
        }
        return ResponseEntity.ok(vehicleCompatibilityRepository.findDistinctEnginesByMakeAndModel(make.trim(), model.trim()));
    }
}

