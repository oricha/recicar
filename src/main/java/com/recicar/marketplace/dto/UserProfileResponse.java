package com.recicar.marketplace.dto;

import com.recicar.marketplace.entity.UserRole;

/**
 * Public account fields for the authenticated user.
 */
public record UserProfileResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        String phone,
        UserRole role
) { }
