package com.recicar.marketplace.service;

import com.recicar.marketplace.dto.VendorRegistrationRequest;
import com.recicar.marketplace.entity.Vendor;
import java.util.Optional;
import java.util.List;

public interface VendorService {

    Vendor registerVendor(VendorRegistrationRequest request);

    Vendor approveVendor(Long vendorId);

    Vendor suspendVendor(Long vendorId);

    Optional<Vendor> findById(Long vendorId);

    List<Vendor> findAllApproved();

    // Other vendor management methods
}
