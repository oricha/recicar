package com.recicar.marketplace.controller;

import com.recicar.marketplace.dto.ConversationSummaryResponse;
import com.recicar.marketplace.dto.SendVendorMessageRequest;
import com.recicar.marketplace.dto.StartConversationRequest;
import com.recicar.marketplace.dto.VendorMessageResponse;
import com.recicar.marketplace.entity.User;
import com.recicar.marketplace.entity.UserRole;
import com.recicar.marketplace.entity.VendorConversation;
import com.recicar.marketplace.entity.VendorMessage;
import com.recicar.marketplace.repository.UserRepository;
import com.recicar.marketplace.service.VendorConversationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Customer–vendor messaging for logged-in users.
 */
@RestController
@RequestMapping("/api/v1/user/conversations")
public class UserConversationApiController {

    private final UserRepository userRepository;
    private final VendorConversationService conversationService;

    public UserConversationApiController(UserRepository userRepository, VendorConversationService conversationService) {
        this.userRepository = userRepository;
        this.conversationService = conversationService;
    }

    @GetMapping
    public ResponseEntity<?> list(@AuthenticationPrincipal UserDetails userDetails) {
        return resolveUser(userDetails)
                .map(user -> {
                    List<ConversationSummaryResponse> list = conversationService.listConversationsForUser(user)
                            .stream()
                            .map(this::toSummary)
                            .collect(Collectors.toList());
                    return ResponseEntity.<Object>ok(list);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Authentication required")));
    }

    @PostMapping
    public ResponseEntity<?> start(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody StartConversationRequest body) {
        if (body.getVendorId() == null) {
            return ResponseEntity.badRequest().body(Map.of("message", "vendorId is required"));
        }
        return resolveUser(userDetails)
                .map(user -> {
                    if (user.getRole() != UserRole.CUSTOMER) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .<Object>body(Map.of("message", "Only customers can start a conversation with a vendor"));
                    }
                    try {
                        VendorConversation c = conversationService.getOrCreate(user, body.getVendorId(), body.getProductId());
                        return ResponseEntity.status(HttpStatus.CREATED)
                                .body(toSummary(c));
                    } catch (IllegalArgumentException e) {
                        return ResponseEntity.badRequest()
                                .<Object>body(Map.of("message", e.getMessage()));
                    }
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Authentication required")));
    }

    @GetMapping("/{conversationId}/messages")
    public ResponseEntity<?> listMessages(
            @PathVariable Long conversationId,
            @AuthenticationPrincipal UserDetails userDetails) {
        return resolveUser(userDetails)
                .map(user -> {
                    Optional<VendorConversation> op = conversationService.getConversationIfParticipant(
                            conversationId, user);
                    if (op.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .<Object>body(Map.of("message", "Conversation not found"));
                    }
                    List<VendorMessageResponse> list = conversationService.listMessages(op.get())
                            .stream()
                            .map(this::toMessage)
                            .collect(Collectors.toList());
                    return ResponseEntity.<Object>ok(list);
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Authentication required")));
    }

    @PostMapping("/{conversationId}/messages")
    public ResponseEntity<?> send(
            @PathVariable Long conversationId,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody SendVendorMessageRequest body) {
        return resolveUser(userDetails)
                .map(user -> {
                    Optional<VendorConversation> op = conversationService.getConversationIfParticipant(
                            conversationId, user);
                    if (op.isEmpty()) {
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .<Object>body(Map.of("message", "Conversation not found"));
                    }
                    try {
                        VendorMessage m = conversationService.sendMessage(op.get(), user, body.getBody());
                        return ResponseEntity.status(HttpStatus.CREATED)
                                .<Object>body(toMessage(m));
                    } catch (IllegalArgumentException e) {
                        return ResponseEntity.badRequest()
                                .<Object>body(Map.of("message", e.getMessage()));
                    }
                })
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("message", "Authentication required")));
    }

    private VendorMessageResponse toMessage(VendorMessage m) {
        return new VendorMessageResponse(
                m.getId(),
                m.getSender().getId(),
                m.getBody(),
                m.getCreatedAt()
        );
    }

    private ConversationSummaryResponse toSummary(VendorConversation c) {
        return new ConversationSummaryResponse(
                c.getId(),
                c.getVendor().getId(),
                c.getVendor().getBusinessName(),
                c.getCustomer().getId(),
                c.getCustomer().getFirstName() + " " + c.getCustomer().getLastName(),
                c.getProduct() != null ? c.getProduct().getId() : null,
                c.getProduct() != null ? c.getProduct().getName() : null,
                c.getUpdatedAt()
        );
    }

    private Optional<User> resolveUser(UserDetails userDetails) {
        if (userDetails == null) {
            return Optional.empty();
        }
        return userRepository.findByEmailIgnoreCase(userDetails.getUsername());
    }
}
