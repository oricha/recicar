package com.recicar.marketplace.dto;

import lombok.Data;

@Data
public class CheckoutForm {
    private String firstName;
    private String lastName;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private String paymentMethod;
}
