package com.recicar.marketplace.service;

import com.recicar.marketplace.dto.VendorRegistrationRequest;
import com.recicar.marketplace.entity.User;
import com.recicar.marketplace.entity.Vendor;
import com.recicar.marketplace.entity.VendorStatus;
import com.recicar.marketplace.repository.UserRepository;
import com.recicar.marketplace.repository.VendorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class VendorServiceTest {

    @InjectMocks
    private VendorServiceImpl vendorService;

    @Mock
    private VendorRepository vendorRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private NotificationService notificationService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testRegisterVendor() {
        VendorRegistrationRequest request = new VendorRegistrationRequest();
        request.setEmail("test@example.com");
        request.setPassword("password");
        request.setBusinessName("Test Business");
        request.setTaxId("12345");

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);
        when(vendorRepository.save(any(Vendor.class))).thenAnswer(i -> i.getArguments()[0]);

        Vendor vendor = vendorService.registerVendor(request);

        assertNotNull(vendor);
        assertEquals(VendorStatus.PENDING, vendor.getStatus());
        assertEquals("test@example.com", vendor.getUser().getEmail());
    }

    @Test
    public void testApproveVendor() {
        Vendor vendor = new Vendor();
        vendor.setId(1L);
        vendor.setStatus(VendorStatus.PENDING);
        vendor.setUser(new User());
        vendor.getUser().setActive(false);

        when(vendorRepository.findById(1L)).thenReturn(Optional.of(vendor));
        when(vendorRepository.save(any(Vendor.class))).thenAnswer(i -> i.getArguments()[0]);

        Vendor approvedVendor = vendorService.approveVendor(1L);

        assertEquals(VendorStatus.APPROVED, approvedVendor.getStatus());
        assertTrue(approvedVendor.getUser().isActive());
    }

    @Test
    public void testSuspendVendor() {
        Vendor vendor = new Vendor();
        vendor.setId(1L);
        vendor.setStatus(VendorStatus.APPROVED);
        vendor.setUser(new User());
        vendor.getUser().setActive(true);

        when(vendorRepository.findById(1L)).thenReturn(Optional.of(vendor));
        when(vendorRepository.save(any(Vendor.class))).thenAnswer(i -> i.getArguments()[0]);

        Vendor suspendedVendor = vendorService.suspendVendor(1L);

        assertEquals(VendorStatus.SUSPENDED, suspendedVendor.getStatus());
        assertFalse(suspendedVendor.getUser().isActive());
    }
}
