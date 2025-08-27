package com.recicar.marketplace.dto;

import lombok.Data;

@Data
public class VehicleCompatibilityRequest {
    private Long id;
    private String make;
    private String model;
    private Integer year;
}
