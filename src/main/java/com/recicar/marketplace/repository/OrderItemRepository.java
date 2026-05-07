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

    @Query("SELECT COALESCE(SUM(oi.totalPrice), 0) FROM OrderItem oi WHERE oi.vendorId = :vid "
            + "AND oi.order.status <> 'CANCELED' AND oi.order.createdAt >= :start AND oi.order.createdAt < :endExclusive")
    BigDecimal sumLineTotalForVendorBetween(
            @Param("vid") Long vendorId,
            @Param("start") LocalDateTime startInclusive,
            @Param("endExclusive") LocalDateTime endExclusive);

    @Query("SELECT COUNT(DISTINCT oi.order.id) FROM OrderItem oi WHERE oi.vendorId = :vid "
            + "AND oi.order.status <> 'CANCELED' AND oi.order.createdAt >= :start AND oi.order.createdAt < :endExclusive")
    long countDistinctOrdersForVendorBetween(
            @Param("vid") Long vendorId,
            @Param("start") LocalDateTime startInclusive,
            @Param("endExclusive") LocalDateTime endExclusive);

    @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.vendorId = :vid "
            + "AND oi.order.status <> 'CANCELED' AND oi.order.createdAt >= :start AND oi.order.createdAt < :endExclusive")
    long countLinesForVendorBetween(
            @Param("vid") Long vendorId,
            @Param("start") LocalDateTime startInclusive,
            @Param("endExclusive") LocalDateTime endExclusive);
}
