package com.recicar.marketplace.service;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DpdShippingServiceTest {

    @Test
    void isEuDestination_acceptsCountryCodesAndSpanishNames() {
        DpdShippingService service = new DpdShippingService();

        assertTrue(service.isEuDestination("ES"));
        assertTrue(service.isEuDestination("España"));
        assertTrue(service.isEuDestination("spain"));
        assertFalse(service.isEuDestination("US"));
    }

    @Test
    void brandLabelAndSurcharge_areStableDefaults() {
        DpdShippingService service = new DpdShippingService();
        ReflectionTestUtils.setField(service, "brandLabel", "ReciCar · envío con DPD");

        assertEquals("ReciCar · envío con DPD", service.getBrandLabel());
        assertEquals(BigDecimal.ZERO, service.dpdSurcharge("ES"));
        assertEquals(BigDecimal.ZERO, service.dpdSurcharge("US"));
    }
}
