package com.recicar.marketplace.service;

import com.recicar.marketplace.entity.User;
import com.recicar.marketplace.entity.UserRole;
import com.recicar.marketplace.entity.VendorConversation;
import com.recicar.marketplace.repository.ProductRepository;
import com.recicar.marketplace.repository.VendorConversationRepository;
import com.recicar.marketplace.repository.VendorMessageRepository;
import com.recicar.marketplace.repository.VendorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VendorConversationServiceTest {

    @Mock
    private VendorConversationRepository conversationRepository;

    @Mock
    private VendorMessageRepository messageRepository;

    @Mock
    private VendorRepository vendorRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private VendorConversationService conversationService;

    @Test
    void getConversationIfParticipant_returnsEmpty_whenStrangerCustomer() {
        User requester = new User();
        requester.setId(2L);
        requester.setRole(UserRole.CUSTOMER);

        User owner = new User();
        owner.setId(9L);

        VendorConversation c = mock(VendorConversation.class);
        lenient().when(c.getCustomer()).thenReturn(owner);
        when(conversationRepository.findById(50L)).thenReturn(Optional.of(c));
        when(vendorRepository.findByUserId(2L)).thenReturn(Optional.empty());

        assertThat(conversationService.getConversationIfParticipant(50L, requester)).isEmpty();
    }

    @Test
    void getConversationIfParticipant_returnsConversation_forOwnerCustomer() {
        User u = new User();
        u.setId(4L);
        u.setRole(UserRole.CUSTOMER);

        VendorConversation c = mock(VendorConversation.class);
        when(c.getCustomer()).thenReturn(u);
        when(conversationRepository.findById(1L)).thenReturn(Optional.of(c));

        assertThat(conversationService.getConversationIfParticipant(1L, u)).contains(c);
    }

    @Test
    void sendMessage_rejectsBlankBody() {
        VendorConversation c = new VendorConversation();
        User sender = new User();

        assertThatThrownBy(() -> conversationService.sendMessage(c, sender, "   "))
                .isInstanceOf(IllegalArgumentException.class);
        verify(messageRepository, never()).save(any());
    }
}
