package com.miniats.repository;

import com.miniats.domain.enums.UserRole;
import com.miniats.domain.model.User;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for User entity.
 * Defines contract for user data access operations.
 */
public interface UserRepository {

    /**
     * Find user by ID
     */
    Optional<User> findById(UUID id);

    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);

    /**
     * Find all users in an organization
     */
    List<User> findByOrganizationId(UUID organizationId);

    /**
     * Find users by role
     */
    List<User> findByRole(UserRole role);

    /**
     * Find users by organization and role
     */
    List<User> findByOrganizationIdAndRole(UUID organizationId, UserRole role);

    /**
     * Get all users
     */
    List<User> findAll();

    /**
     * Create new user
     */
    User save(User user);

    /**
     * Update existing user
     */
    User update(User user);

    /**
     * Delete user by ID
     */
    void deleteById(UUID id);

    /**
     * Check if user exists by ID
     */
    boolean existsById(UUID id);

    /**
     * Check if user exists by email
     */
    boolean existsByEmail(String email);

    /**
     * Check if user exists in organization
     */
    boolean existsByEmailAndOrganizationId(String email, UUID organizationId);
}