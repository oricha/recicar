package com.recicar.marketplace.service;

import com.recicar.marketplace.dto.VendorRegistrationRequest;
import com.recicar.marketplace.dto.ProfileUpdateRequest;
import com.recicar.marketplace.dto.PasswordChangeRequest;
import com.recicar.marketplace.dto.StoreInfoUpdateRequest;
import com.recicar.marketplace.entity.User;
import com.recicar.marketplace.entity.Vendor;
import com.recicar.marketplace.entity.UserRole;
import com.recicar.marketplace.entity.VendorStatus;
import com.recicar.marketplace.repository.UserRepository;
import com.recicar.marketplace.repository.VendorRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.Optional;
import java.util.List;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

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

    @Override
    @Transactional
    public void updateProfile(Long vendorId, ProfileUpdateRequest request) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
        
        User user = vendor.getUser();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        
        // TODO: Handle address update (create or update Address entity)
        
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void changePassword(Long vendorId, PasswordChangeRequest request) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
        
        User user = vendor.getUser();
        
        // Verify old password
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Old password is incorrect");
        }
        
        // Verify new password matches confirmation
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("New passwords do not match");
        }
        
        // Update password
        user.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void updateStoreInfo(Long vendorId, StoreInfoUpdateRequest request, MultipartFile logo, MultipartFile banner) {
        Vendor vendor = vendorRepository.findById(vendorId)
                .orElseThrow(() -> new RuntimeException("Vendor not found"));
        
        vendor.setBusinessName(request.getStoreName());
        vendor.setContactPhone(request.getStorePhone());
        vendor.setContactEmail(request.getStoreEmail());
        
        // Handle logo upload
        if (logo != null && !logo.isEmpty()) {
            String logoUrl = saveFile(logo, "logos");
            vendor.setLogoUrl(logoUrl);
        }
        
        // Handle banner upload
        if (banner != null && !banner.isEmpty()) {
            String bannerUrl = saveFile(banner, "banners");
            vendor.setBannerUrl(bannerUrl);
        }
        
        vendorRepository.save(vendor);
    }

    private String saveFile(MultipartFile file, String directory) {
        try {
            // Validate directory parameter - only allow specific values
            if (!directory.equals("logos") && !directory.equals("banners")) {
                throw new IllegalArgumentException("Invalid directory: " + directory);
            }
            
            // Validate file extension
            String originalFilename = file.getOriginalFilename();
            if (originalFilename == null || originalFilename.isEmpty()) {
                throw new IllegalArgumentException("Invalid filename");
            }
            
            // Only allow image files
            String lowerFilename = originalFilename.toLowerCase();
            if (!lowerFilename.endsWith(".jpg") && !lowerFilename.endsWith(".jpeg") && 
                !lowerFilename.endsWith(".png") && !lowerFilename.endsWith(".gif")) {
                throw new IllegalArgumentException("Only image files are allowed");
            }
            
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            
            // Create upload directory if it doesn't exist
            Path uploadPath = Paths.get("uploads", directory);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            
            // Generate unique filename with validated extension
            String filename = UUID.randomUUID().toString() + extension;
            
            // Resolve path and ensure it's within the upload directory
            Path filePath = uploadPath.resolve(filename).normalize();
            if (!filePath.startsWith(uploadPath.normalize())) {
                throw new SecurityException("Path traversal attempt detected");
            }
            
            // Save file
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            
            // Return URL path
            return "/uploads/" + directory + "/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file: " + e.getMessage(), e);
        }
    }
}
