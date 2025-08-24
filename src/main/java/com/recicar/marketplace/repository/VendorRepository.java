package com.recicar.marketplace.repository;

import com.recicar.marketplace.entity.Vendor;
import com.recicar.marketplace.entity.VendorStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendorRepository extends JpaRepository<Vendor, Long> {

    /**
     * Find vendor by business name
     */
    Optional<Vendor> findByBusinessNameIgnoreCase(String businessName);

    /**
     * Find vendor by tax ID
     */
    Optional<Vendor> findByTaxIdIgnoreCase(String taxId);

    /**
     * Find vendors by status
     */
    List<Vendor> findByStatus(VendorStatus status);

    /**
     * Find vendors by user ID
     */
    Optional<Vendor> findByUserId(Long userId);

    /**
     * Check if business name exists
     */
    boolean existsByBusinessNameIgnoreCase(String businessName);

    /**
     * Check if tax ID exists
     */
    boolean existsByTaxIdIgnoreCase(String taxId);
}

