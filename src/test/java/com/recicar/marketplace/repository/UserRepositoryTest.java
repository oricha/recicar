package com.recicar.marketplace.repository;

import com.recicar.marketplace.entity.User;
import com.recicar.marketplace.entity.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void shouldSaveAndFindUser() {
        // Given
        User user = new User();
        user.setEmail("test@example.com");
        user.setPasswordHash("password123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setRole(UserRole.CUSTOMER);

        // When
        User savedUser = userRepository.save(user);
        entityManager.flush();

        // Then
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getCreatedAt()).isNotNull();
        assertThat(savedUser.getUpdatedAt()).isNotNull();

        Optional<User> foundUser = userRepository.findByEmail("test@example.com");
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getFirstName()).isEqualTo("John");
        assertThat(foundUser.get().getLastName()).isEqualTo("Doe");
        assertThat(foundUser.get().getRole()).isEqualTo(UserRole.CUSTOMER);
    }

    @Test
    void shouldFindUserByEmailIgnoreCase() {
        // Given
        User user = new User();
        user.setEmail("Test@Example.com");
        user.setPasswordHash("password123");
        user.setFirstName("Jane");
        user.setLastName("Smith");
        user.setRole(UserRole.VENDOR);

        userRepository.save(user);
        entityManager.flush();

        // When
        Optional<User> foundUser = userRepository.findByEmailIgnoreCase("test@example.com");

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getFirstName()).isEqualTo("Jane");
    }

    @Test
    void shouldCheckIfEmailExists() {
        // Given
        User user = new User();
        user.setEmail("existing@example.com");
        user.setPasswordHash("password123");
        user.setFirstName("Existing");
        user.setLastName("User");

        userRepository.save(user);
        entityManager.flush();

        // When & Then
        assertThat(userRepository.existsByEmail("existing@example.com")).isTrue();
        assertThat(userRepository.existsByEmail("nonexistent@example.com")).isFalse();
        assertThat(userRepository.existsByEmailIgnoreCase("EXISTING@EXAMPLE.COM")).isTrue();
    }

    @Test
    void shouldFindUsersByRole() {
        // Given
        User customer = new User("customer@example.com", "password123", "Customer", "User");
        customer.setRole(UserRole.CUSTOMER);

        User vendor = new User("vendor@example.com", "password123", "Vendor", "User");
        vendor.setRole(UserRole.VENDOR);

        userRepository.save(customer);
        userRepository.save(vendor);
        entityManager.flush();

        // When
        var customers = userRepository.findByRole(UserRole.CUSTOMER);
        var vendors = userRepository.findByRole(UserRole.VENDOR);

        // Then
        assertThat(customers).hasSize(1);
        assertThat(customers.get(0).getEmail()).isEqualTo("customer@example.com");

        assertThat(vendors).hasSize(1);
        assertThat(vendors.get(0).getEmail()).isEqualTo("vendor@example.com");
    }

    @Test
    void shouldCountUsersByRole() {
        // Given
        User customer1 = new User("customer1@example.com", "password123", "Customer", "One");
        customer1.setRole(UserRole.CUSTOMER);

        User customer2 = new User("customer2@example.com", "password123", "Customer", "Two");
        customer2.setRole(UserRole.CUSTOMER);

        User vendor = new User("vendor@example.com", "password123", "Vendor", "User");
        vendor.setRole(UserRole.VENDOR);

        userRepository.save(customer1);
        userRepository.save(customer2);
        userRepository.save(vendor);
        entityManager.flush();

        // When & Then
        assertThat(userRepository.countByRole(UserRole.CUSTOMER)).isEqualTo(2);
        assertThat(userRepository.countByRole(UserRole.VENDOR)).isEqualTo(1);
        assertThat(userRepository.countByRole(UserRole.ADMIN)).isEqualTo(0);
    }
}