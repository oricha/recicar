package com.recicar.marketplace.repository;

import com.recicar.marketplace.entity.VendorMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VendorMessageRepository extends JpaRepository<VendorMessage, Long> {

    List<VendorMessage> findByConversation_IdOrderByCreatedAtAsc(Long conversationId);
}
