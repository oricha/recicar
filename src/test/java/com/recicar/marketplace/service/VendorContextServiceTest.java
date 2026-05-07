package com.recicar.marketplace.service;

import com.recicar.marketplace.entity.User;
import com.recicar.marketplace.entity.Vendor;
import com.recicar.marketplace.repository.UserRepository;
import com.recicar.marketplace.repository.VendorRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VendorContextServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private VendorRepository vendorRepository;

    @InjectMocks
    private VendorContextService vendorContextService;

    @Test
    void findVendor_resolvesViaUserEmail() {
        User u = new User();
        u.setId(3L);
        u.setEmail("seller@biz.com");

        Vendor v = new Vendor();
        v.setId(77L);

        UserDetails ud = org.springframework.security.core.userdetails.User
                .withUsername("seller@biz.com")
                .password("-")
                .roles("VENDOR")
                .build();

        when(userRepository.findByEmailIgnoreCase("seller@biz.com")).thenReturn(Optional.of(u));
        when(vendorRepository.findByUserId(3L)).thenReturn(Optional.of(v));

        assertThat(vendorContextService.findVendorForUserDetails(ud)).contains(v);
    }

    @Test
    void findVendor_emptyWithoutLinkedVendorRow() {
        User u = new User();
        u.setId(4L);

        UserDetails ud = org.springframework.security.core.userdetails.User
                .withUsername("orphan@biz.com")
                .password("-")
                .roles("VENDOR")
                .build();

        when(userRepository.findByEmailIgnoreCase("orphan@biz.com")).thenReturn(Optional.of(u));
        when(vendorRepository.findByUserId(4L)).thenReturn(Optional.empty());

        assertThat(vendorContextService.findVendorForUserDetails(ud)).isEmpty();
    }
}
