package com.recicar.marketplace.repository;

import com.recicar.marketplace.entity.VehicleInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehicleInfoRepository extends JpaRepository<VehicleInfo, Long> {

    Optional<VehicleInfo> findByProductId(Long productId);
}
