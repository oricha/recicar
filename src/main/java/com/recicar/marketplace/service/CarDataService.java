
package com.recicar.marketplace.service;

import com.recicar.marketplace.domain.CarMake;
import com.recicar.marketplace.domain.CarModel;
import com.recicar.marketplace.domain.CarTrim;

import java.util.List;

public interface CarDataService {
    List<CarMake> getAllMakes();
    List<CarModel> getModelsByMake(Long makeId);
    List<CarTrim> getTrimsByModel(Long modelId);
}
