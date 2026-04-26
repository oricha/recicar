package com.recicar.marketplace.service;

import com.recicar.marketplace.entity.Vendor;
import com.recicar.marketplace.repository.VendorRepository;
import com.recicar.marketplace.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

/**
 * Resolves the {@link Vendor} for the current authenticated user (email from {@link UserDetails}).
 */
@Service
@Transactional(readOnly = true)
public class VendorContextService {

    private final UserRepository userRepository;
    private final VendorRepository vendorRepository;

    public VendorContextService(UserRepository userRepository, VendorRepository vendorRepository) {
        this.userRepository = userRepository;
        this.vendorRepository = vendorRepository;
    }

    public Optional<Vendor> findVendorForUserDetails(UserDetails userDetails) {
        if (userDetails == null) {
            return Optional.empty();
        }
        return userRepository.findByEmailIgnoreCase(userDetails.getUsername())
                .flatMap(u -> vendorRepository.findByUserId(u.getId()));
    }

    public Vendor requireCurrentVendor(UserDetails userDetails) {
        return findVendorForUserDetails(userDetails)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.FORBIDDEN, "No vendor account linked to this user"));
    }
}
