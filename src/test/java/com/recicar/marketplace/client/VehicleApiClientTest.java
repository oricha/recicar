package com.recicar.marketplace.client;

import com.recicar.marketplace.dto.VehicleInfo;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class VehicleApiClientTest {

    private VehicleApiClient vehicleApiClient = new VehicleApiClient();

    @Test
    public void testLookupLicensePlate_found() {
        VehicleInfo vehicleInfo = vehicleApiClient.lookupLicensePlate("ABC123");
        assertNotNull(vehicleInfo);
        assertEquals("Toyota", vehicleInfo.getMake());
        assertEquals("Camry", vehicleInfo.getModel());
        assertEquals(2020, vehicleInfo.getYear());
    }

    @Test
    public void testLookupLicensePlate_notFound() {
        VehicleInfo vehicleInfo = vehicleApiClient.lookupLicensePlate("NONEXISTENT");
        assertNull(vehicleInfo);
    }
}
