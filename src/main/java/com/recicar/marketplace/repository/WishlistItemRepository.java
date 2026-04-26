package com.recicar.marketplace.repository;

import com.recicar.marketplace.entity.WishlistItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, Long> {

    List<WishlistItem> findByUser_IdOrderByCreatedAtDesc(Long userId);

    Optional<WishlistItem> findByUser_IdAndProduct_Id(Long userId, Long productId);

    int countByUser_Id(Long userId);

    void deleteByUser_IdAndProduct_Id(Long userId, Long productId);

    @Query("SELECT w.product.id FROM WishlistItem w WHERE w.user.id = :userId")
    List<Long> findProductIdsByUserId(@Param("userId") Long userId);
}
