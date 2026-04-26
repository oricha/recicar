package com.recicar.marketplace.repository;

import com.recicar.marketplace.entity.VendorConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VendorConversationRepository extends JpaRepository<VendorConversation, Long> {

    List<VendorConversation> findByCustomer_IdOrderByUpdatedAtDesc(Long customerId);

    List<VendorConversation> findByVendor_IdOrderByUpdatedAtDesc(Long vendorId);

    Optional<VendorConversation> findByCustomer_IdAndVendor_Id(Long customerId, Long vendorId);
}
