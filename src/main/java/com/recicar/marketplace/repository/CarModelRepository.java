
package com.recicar.marketplace.repository;

import com.recicar.marketplace.domain.CarModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarModelRepository extends JpaRepository<CarModel, Long> {
    List<CarModel> findByMakeId(Long makeId);
    List<CarModel> findByMakeIdOrderByNameAsc(Long makeId);
}
