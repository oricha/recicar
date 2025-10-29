package com.recicar.marketplace.service;

import com.recicar.marketplace.dto.VendorRegistrationRequest;
import com.recicar.marketplace.dto.ProfileUpdateRequest;
import com.recicar.marketplace.dto.PasswordChangeRequest;
import com.recicar.marketplace.dto.StoreInfoUpdateRequest;
import com.recicar.marketplace.entity.Vendor;
import org.springframework.web.multipart.MultipartFile;
import java.util.Optional;
import java.util.List;

public interface VendorService {

    Vendor registerVendor(VendorRegistrationRequest request);

    Vendor approveVendor(Long vendorId);

    Vendor suspendVendor(Long vendorId);

    Optional<Vendor> findById(Long vendorId);

    List<Vendor> findAllApproved();

    void updateProfile(Long vendorId, ProfileUpdateRequest request);

    void changePassword(Long vendorId, PasswordChangeRequest request);

    void updateStoreInfo(Long vendorId, StoreInfoUpdateRequest request, MultipartFile logo, MultipartFile banner);

    // Other vendor management methods
}
