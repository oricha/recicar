
package com.recicar.marketplace.controller;

import com.recicar.marketplace.domain.CarMake;
import com.recicar.marketplace.domain.CarModel;
import com.recicar.marketplace.domain.CarTrim;
import com.recicar.marketplace.service.CarDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/car-data")
public class CarDataApiController {

    @Autowired
    private CarDataService carDataService;

    @GetMapping("/makes")
    public List<CarMake> getMakes() {
        return carDataService.getAllMakes();
    }

    @GetMapping("/models")
    public List<CarModel> getModels(@RequestParam("makeId") Long makeId) {
        return carDataService.getModelsByMake(makeId);
    }

    @GetMapping("/trims")
    public List<CarTrim> getTrims(@RequestParam("modelId") Long modelId) {
        return carDataService.getTrimsByModel(modelId);
    }
}
