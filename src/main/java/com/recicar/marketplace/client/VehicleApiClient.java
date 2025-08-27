package com.recicar.marketplace.client;

import com.recicar.marketplace.dto.VehicleInfo;
import org.springframework.stereotype.Component;

@Component
public class VehicleApiClient {

    public VehicleInfo lookupLicensePlate(String licensePlate) {
        // Simulate API call to external vehicle database
        if ("ABC123".equals(licensePlate)) {
            VehicleInfo vehicleInfo = new VehicleInfo();
            vehicleInfo.setMake("Toyota");
            vehicleInfo.setModel("Camry");
            vehicleInfo.setYear(2020);
            return vehicleInfo;
        } else if ("XYZ789".equals(licensePlate)) {
            VehicleInfo vehicleInfo = new VehicleInfo();
            vehicleInfo.setMake("Honda");
            vehicleInfo.setModel("Civic");
            vehicleInfo.setYear(2018);
            return vehicleInfo;
        } else {
            return null; // Simulate not found
        }
    }
}
