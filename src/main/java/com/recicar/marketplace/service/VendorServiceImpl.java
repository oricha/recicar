package com.recicar.marketplace.service;

import com.recicar.marketplace.dto.VendorRegistrationRequest;
import com.recicar.marketplace.entity.User;
import com.recicar.marketplace.entity.Vendor;
import com.recicar.marketplace.entity.UserRole;
import com.recicar.marketplace.entity.VendorStatus;
import com.recicar.marketplace.repository.UserRepository;
import com.recicar.marketplace.repository.VendorRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.List;

@Service
public class VendorServiceImpl implements VendorService {

    private final VendorRepository vendorRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    public VendorServiceImpl(VendorRepository vendorRepository, UserRepository userRepository, PasswordEncoder passwordEncoder, NotificationService notificationService) {
        this.vendorRepository = vendorRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.notificationService = notificationService;
    }

    @Override
    @Transactional
    public Vendor registerVendor(VendorRegistrationRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhone(request.getPhone());
        user.setRole(UserRole.VENDOR);
        user.setEmailVerified(false);
        user.setActive(true);
        userRepository.save(user);

        Vendor vendor = new Vendor();
        vendor.setUser(user);
        vendor.setBusinessName(request.getBusinessName());
        vendor.setTaxId(request.getTaxId());
        vendor.setDescription(request.getDescription());
        vendor.setStatus(VendorStatus.PENDING);
        vendorRepository.save(vendor);

        notificationService.sendAccountVerificationEmail(user);

        return vendor;
    }

    @Override
    @Transactional
    public Vendor approveVendor(Long vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
        vendor.setStatus(VendorStatus.APPROVED);
        vendor.getUser().setActive(true);
        vendorRepository.save(vendor);
        // TODO: Send approval email
        return vendor;
    }

    @Override
    @Transactional
    public Vendor suspendVendor(Long vendorId) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
        vendor.setStatus(VendorStatus.SUSPENDED);
        vendor.getUser().setActive(false);
        vendorRepository.save(vendor);
        // TODO: Send suspension email
        return vendor;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Vendor> findById(Long vendorId) {
        return vendorRepository.findById(vendorId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vendor> findAllApproved() {
        return vendorRepository.findByStatus(VendorStatus.APPROVED);
    }
}
