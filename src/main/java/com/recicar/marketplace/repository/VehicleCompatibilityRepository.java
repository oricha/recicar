package com.recicar.marketplace.repository;

import com.recicar.marketplace.entity.VehicleCompatibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VehicleCompatibilityRepository extends JpaRepository<VehicleCompatibility, Long> {

    @Query("SELECT DISTINCT LOWER(vc.make) FROM VehicleCompatibility vc ORDER BY LOWER(vc.make)")
    List<String> findDistinctMakes();

    @Query("SELECT DISTINCT LOWER(vc.model) FROM VehicleCompatibility vc WHERE LOWER(vc.make) = LOWER(:make) ORDER BY LOWER(vc.model)")
    List<String> findDistinctModelsByMake(@Param("make") String make);

    @Query("SELECT DISTINCT LOWER(vc.engine) FROM VehicleCompatibility vc WHERE LOWER(vc.make) = LOWER(:make) AND LOWER(vc.model) = LOWER(:model) AND vc.engine IS NOT NULL ORDER BY LOWER(vc.engine)")
    List<String> findDistinctEnginesByMakeAndModel(@Param("make") String make, @Param("model") String model);

    @Query("SELECT DISTINCT vc.yearFrom, vc.yearTo FROM VehicleCompatibility vc WHERE LOWER(vc.make) = LOWER(:make) AND LOWER(vc.model) = LOWER(:model) AND LOWER(vc.engine) = LOWER(:engine) ORDER BY vc.yearFrom, vc.yearTo")
    List<Object[]> findDistinctYearRangesByMakeModelAndEngine(@Param("make") String make, @Param("model") String model, @Param("engine") String engine);
}

