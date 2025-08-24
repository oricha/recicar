package com.recicar.marketplace.repository;

import com.recicar.marketplace.entity.User;
import com.recicar.marketplace.entity.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by email address
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by email address (case insensitive)
     */
    Optional<User> findByEmailIgnoreCase(String email);

    /**
     * Check if user exists by email
     */
    boolean existsByEmail(String email);

    /**
     * Check if user exists by email (case insensitive)
     */
    boolean existsByEmailIgnoreCase(String email);

    /**
     * Find all users by role
     */
    List<User> findByRole(UserRole role);

    /**
     * Find all active users
     */
    List<User> findByActiveTrue();

    /**
     * Find all users by role and active status
     */
    List<User> findByRoleAndActive(UserRole role, boolean active);

    /**
     * Find all users with email verification status
     */
    List<User> findByEmailVerified(boolean emailVerified);

    /**
     * Find users by first name and last name (case insensitive)
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :firstName, '%')) " +
           "AND LOWER(u.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))")
    List<User> findByFirstNameAndLastNameContainingIgnoreCase(@Param("firstName") String firstName, 
                                                              @Param("lastName") String lastName);

    /**
     * Count users by role
     */
    long countByRole(UserRole role);

    /**
     * Count active users
     */
    long countByActiveTrue();
}