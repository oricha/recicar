package com.recicar.marketplace.repository;

import com.recicar.marketplace.entity.UserPasswordHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPasswordHistoryRepository extends JpaRepository<UserPasswordHistory, Long> {

    List<UserPasswordHistory> findByUser_IdOrderByCreatedAtDesc(Long userId);
}
