package com.recicar.marketplace.service;

import com.recicar.marketplace.dto.ContactRequest;
import com.recicar.marketplace.entity.ContactMessage;
import com.recicar.marketplace.repository.ContactMessageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ContactMessageService {

    private final ContactMessageRepository contactMessageRepository;
    private final NotificationService notificationService;

    public ContactMessageService(ContactMessageRepository contactMessageRepository, NotificationService notificationService) {
        this.contactMessageRepository = contactMessageRepository;
        this.notificationService = notificationService;
    }

    @Transactional
    public void submit(ContactRequest request, String supportInboxAddress) {
        ContactMessage m = new ContactMessage();
        m.setName(request.getName().trim());
        m.setEmail(request.getEmail().trim());
        m.setSubject(request.getSubject().trim());
        m.setMessage(request.getMessage().trim());
        contactMessageRepository.save(m);
        notificationService.sendSupportContactInquiry(
                m.getName(), m.getEmail(), m.getSubject(), m.getMessage(), supportInboxAddress);
    }
}
