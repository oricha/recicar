package com.recicar.marketplace.service;

import com.recicar.marketplace.dto.UserRegistrationDto;
import com.recicar.marketplace.entity.User;
import com.recicar.marketplace.entity.UserRole;
import com.recicar.marketplace.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Register a new user
     */
    public User registerUser(UserRegistrationDto registrationDto) {
        if (existsByEmail(registrationDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        User user = new User();
        user.setEmail(registrationDto.getEmail().toLowerCase().trim());
        user.setPasswordHash(passwordEncoder.encode(registrationDto.getPassword()));
        user.setFirstName(registrationDto.getFirstName().trim());
        user.setLastName(registrationDto.getLastName().trim());
        user.setPhone(registrationDto.getPhone());
        user.setRole(UserRole.CUSTOMER);
        user.setActive(true);
        user.setEmailVerified(false); // Will be verified via email

        return userRepository.save(user);
    }

    /**
     * Find user by email
     */
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    /**
     * Check if user exists by email
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }

    /**
     * Find user by ID
     */
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Update user profile
     */
    public User updateUser(User user) {
        return userRepository.save(user);
    }

    /**
     * Change user password
     */
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * Verify user email
     */
    public void verifyEmail(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        user.setEmailVerified(true);
        userRepository.save(user);
    }

    /**
     * Initiate password reset (placeholder for now)
     */
    public void initiatePasswordReset(String email) {
        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(email);
        if (userOpt.isPresent()) {
            // TODO: Generate reset token and send email
            // For now, just log that reset was requested
            System.out.println("Password reset requested for: " + email);
        }
    }

    /**
     * Activate/Deactivate user
     */
    public void setUserActive(Long userId, boolean active) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        user.setActive(active);
        userRepository.save(user);
    }

    /**
     * Get all users by role
     */
    @Transactional(readOnly = true)
    public List<User> findByRole(UserRole role) {
        return userRepository.findByRole(role);
    }

    /**
     * Get all active users
     */
    @Transactional(readOnly = true)
    public List<User> findActiveUsers() {
        return userRepository.findByActiveTrue();
    }

    /**
     * Count users by role
     */
    @Transactional(readOnly = true)
    public long countByRole(UserRole role) {
        return userRepository.countByRole(role);
    }

    /**
     * Promote user to vendor
     */
    public void promoteToVendor(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        if (user.getRole() != UserRole.CUSTOMER) {
            throw new IllegalArgumentException("Only customers can be promoted to vendors");
        }
        
        user.setRole(UserRole.VENDOR);
        userRepository.save(user);
    }

    /**
     * Create admin user (for initial setup)
     */
    public User createAdminUser(String email, String password, String firstName, String lastName) {
        if (existsByEmail(email)) {
            throw new IllegalArgumentException("Admin email already exists");
        }

        User admin = new User();
        admin.setEmail(email.toLowerCase().trim());
        admin.setPasswordHash(passwordEncoder.encode(password));
        admin.setFirstName(firstName);
        admin.setLastName(lastName);
        admin.setRole(UserRole.ADMIN);
        admin.setActive(true);
        admin.setEmailVerified(true);

        return userRepository.save(admin);
    }

    public void dataInitializer(Long userId,  String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}