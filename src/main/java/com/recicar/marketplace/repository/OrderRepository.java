package com.recicar.marketplace.repository;

import com.recicar.marketplace.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByOrderNumber(String orderNumber);

    List<Order> findByCustomer_IdOrderByCreatedAtDesc(Long customerId, Pageable pageable);

    /**
     * Orders that include at least one line item for the given vendor.
     */
    @Query(
            value = "SELECT DISTINCT o FROM Order o JOIN o.items i WHERE i.vendorId = :vid ORDER BY o.createdAt DESC",
            countQuery = "SELECT COUNT(DISTINCT o) FROM Order o JOIN o.items i WHERE i.vendorId = :vid")
    Page<Order> findPageForVendor(@Param("vid") Long vendorId, Pageable pageable);

    /**
     * Distinct orders that include vendor lines and are still awaiting shipment/processing.
     */
    @Query("SELECT COUNT(DISTINCT o) FROM Order o JOIN o.items i WHERE i.vendorId = :vid AND o.status IN ('PENDING', 'PROCESSING')")
    long countPendingOrdersForVendor(@Param("vid") Long vendorId);
}
