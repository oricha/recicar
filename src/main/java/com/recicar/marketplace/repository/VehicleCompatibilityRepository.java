package com.recicar.marketplace.repository;

import com.recicar.marketplace.entity.VehicleCompatibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import com.recicar.marketplace.entity.Product;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleCompatibilityRepository extends JpaRepository<VehicleCompatibility, Long> {

    @Query("SELECT DISTINCT vc.make FROM VehicleCompatibility vc ORDER BY vc.make")
    List<String> findDistinctMakes();

    @Query("SELECT DISTINCT vc.model FROM VehicleCompatibility vc WHERE vc.make = ?1 ORDER BY vc.model")
    List<String> findDistinctModelsByMake(String make);

    @Query("SELECT DISTINCT vc.engine FROM VehicleCompatibility vc WHERE vc.make = ?1 AND vc.model = ?2 ORDER BY vc.engine")
    List<String> findDistinctEnginesByMakeAndModel(String make, String model);

    @Query("SELECT DISTINCT vc.yearFrom, vc.yearTo FROM VehicleCompatibility vc WHERE vc.make = ?1 AND vc.model = ?2 AND vc.engine = ?3 ORDER BY vc.yearFrom")
    List<Object[]> findDistinctYearRangesByMakeModelAndEngine(String make, String model, String engine);

    @Query("SELECT p FROM Product p JOIN p.compatibilities vc WHERE vc.make = ?1 AND vc.model = ?2 AND vc.engine = ?3 AND ?4 BETWEEN vc.yearFrom AND vc.yearTo")
    List<Product> findProductsByVehicleCompatibility(String make, String model, String engine, Integer year);
}
