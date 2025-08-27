package com.recicar.marketplace.dto;

import lombok.Data;

@Data
public class VendorRegistrationRequest {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String phone;
    private String businessName;
    private String taxId;
    private String description;
}
