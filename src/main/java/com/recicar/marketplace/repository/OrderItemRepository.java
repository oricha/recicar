package com.recicar.marketplace.repository;

import com.recicar.marketplace.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("SELECT COALESCE(SUM(oi.totalPrice), 0) FROM OrderItem oi WHERE oi.vendorId = :vid AND oi.order.status <> 'CANCELED' AND oi.order.createdAt >= :start")
    BigDecimal sumLineTotalForVendorSince(
            @Param("vid") Long vendorId,
            @Param("start") LocalDateTime startInclusive);
}
