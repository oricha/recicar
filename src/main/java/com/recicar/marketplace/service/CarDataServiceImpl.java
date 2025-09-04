
package com.recicar.marketplace.service;

import com.recicar.marketplace.domain.CarMake;
import com.recicar.marketplace.domain.CarModel;
import com.recicar.marketplace.domain.CarTrim;
import com.recicar.marketplace.repository.CarMakeRepository;
import com.recicar.marketplace.repository.CarModelRepository;
import com.recicar.marketplace.repository.CarTrimRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarDataServiceImpl implements CarDataService {

    @Autowired
    private CarMakeRepository carMakeRepository;

    @Autowired
    private CarModelRepository carModelRepository;

    @Autowired
    private CarTrimRepository carTrimRepository;

    @Override
    public List<CarMake> getAllMakes() {
        return carMakeRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
    }

    @Override
    public List<CarModel> getModelsByMake(Long makeId) {
        return carModelRepository.findByMakeIdOrderByNameAsc(makeId);
    }

    @Override
    public List<CarTrim> getTrimsByModel(Long modelId) {
        return carTrimRepository.findByModelIdOrderByNameAsc(modelId);
    }
}
