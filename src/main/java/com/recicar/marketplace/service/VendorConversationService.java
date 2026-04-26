package com.recicar.marketplace.service;

import com.recicar.marketplace.entity.*;
import com.recicar.marketplace.repository.ProductRepository;
import com.recicar.marketplace.repository.VendorConversationRepository;
import com.recicar.marketplace.repository.VendorMessageRepository;
import com.recicar.marketplace.repository.VendorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Message threads between customers and vendors.
 */
@Service
public class VendorConversationService {

    private final VendorConversationRepository conversationRepository;
    private final VendorMessageRepository messageRepository;
    private final VendorRepository vendorRepository;
    private final ProductRepository productRepository;

    public VendorConversationService(VendorConversationRepository conversationRepository,
            VendorMessageRepository messageRepository, VendorRepository vendorRepository,
            ProductRepository productRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.vendorRepository = vendorRepository;
        this.productRepository = productRepository;
    }

    @Transactional(readOnly = true)
    public List<VendorConversation> listConversationsForUser(User user) {
        if (user.getRole() == UserRole.CUSTOMER) {
            return conversationRepository.findByCustomer_IdOrderByUpdatedAtDesc(user.getId());
        }
        if (user.getRole() == UserRole.VENDOR || user.getRole() == UserRole.ADMIN) {
            return vendorRepository.findByUserId(user.getId())
                    .map(v -> conversationRepository.findByVendor_IdOrderByUpdatedAtDesc(v.getId()))
                    .orElse(List.of());
        }
        return List.of();
    }

    @Transactional(readOnly = true)
    public Optional<VendorConversation> getConversationIfParticipant(Long conversationId, User user) {
        Optional<VendorConversation> op = conversationRepository.findById(conversationId);
        if (op.isEmpty()) {
            return Optional.empty();
        }
        VendorConversation c = op.get();
        if (user.getId().equals(c.getCustomer().getId())) {
            return op;
        }
        Optional<Vendor> vendor = vendorRepository.findByUserId(user.getId());
        if (vendor.isPresent() && vendor.get().getId().equals(c.getVendor().getId())) {
            return op;
        }
        if (user.getRole() == UserRole.ADMIN) {
            return op;
        }
        return Optional.empty();
    }

    @Transactional
    public VendorConversation getOrCreate(User customer, Long vendorId, Long productId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new IllegalArgumentException("Vendor not found"));
        if (vendor.getUser() != null && vendor.getUser().getId().equals(customer.getId())) {
            throw new IllegalArgumentException("Cannot start a thread with your own business");
        }
        return conversationRepository.findByCustomer_IdAndVendor_Id(customer.getId(), vendorId)
                .orElseGet(() -> {
                    VendorConversation c = new VendorConversation();
                    c.setCustomer(customer);
                    c.setVendor(vendor);
                    if (productId != null && productRepository.existsById(productId)) {
                        c.setProduct(productRepository.getReferenceById(productId));
                    }
                    return conversationRepository.save(c);
                });
    }

    @Transactional
    public VendorMessage sendMessage(VendorConversation conversation, User sender, String body) {
        if (body == null || body.isBlank()) {
            throw new IllegalArgumentException("Message body is required");
        }
        String trimmed = body.trim();
        if (trimmed.length() > 8000) {
            throw new IllegalArgumentException("Message too long");
        }
        VendorMessage m = new VendorMessage();
        m.setConversation(conversation);
        m.setSender(sender);
        m.setBody(trimmed);
        VendorMessage saved = messageRepository.save(m);
        conversation.setUpdatedAt(java.time.LocalDateTime.now());
        conversationRepository.save(conversation);
        return saved;
    }

    @Transactional(readOnly = true)
    public List<VendorMessage> listMessages(VendorConversation conversation) {
        return messageRepository.findByConversation_IdOrderByCreatedAtAsc(conversation.getId());
    }
}
