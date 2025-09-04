
package com.recicar.marketplace.repository;

import com.recicar.marketplace.domain.CarTrim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarTrimRepository extends JpaRepository<CarTrim, Long> {
    List<CarTrim> findByModelId(Long modelId);
    List<CarTrim> findByModelIdOrderByNameAsc(Long modelId);
}
