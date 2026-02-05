package com.miniats.service;

import com.miniats.domain.enums.UserRole;
import com.miniats.domain.model.User;
import com.miniats.dto.UserDTO;
import com.miniats.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Service for User entity operations.
 * Handles business logic for user management including admin impersonation.
 */
@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final OrganizationService organizationService;

    public UserService(
            UserRepository userRepository,
            OrganizationService organizationService
    ) {
        this.userRepository = userRepository;
        this.organizationService = organizationService;
    }

    /**
     * Get user by ID
     */
    public UserDTO getUserById(UUID id) {
        logger.debug("Fetching user with ID: {}", id);

        return userRepository.findById(id)
                .map(UserDTO::fromEntity)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));
    }

    /**
     * Get user by email
     */
    public UserDTO getUserByEmail(String email) {
        logger.debug("Fetching user with email: {}", email);

        return userRepository.findByEmail(email)
                .map(UserDTO::fromEntity)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    /**
     * Get all users in an organization
     */
    public List<UserDTO> getUsersByOrganization(UUID organizationId) {
        logger.debug("Fetching users for organization: {}", organizationId);

        return userRepository.findByOrganizationId(organizationId).stream()
                .map(UserDTO::fromEntity)
                .toList();
    }

    /**
     * Get all admin users (system-wide)
     */
    public List<UserDTO> getAllAdmins() {
        logger.debug("Fetching all admin users");

        return userRepository.findByRole(UserRole.ADMIN).stream()
                .map(UserDTO::fromEntity)
                .toList();
    }

    /**
     * Get all users (admin only)
     */
    public List<UserDTO> getAllUsers() {
        logger.debug("Fetching all users");

        return userRepository.findAll().stream()
                .map(UserDTO::fromEntity)
                .toList();
    }

    /**
     * Create new user
     */
    public UserDTO createUser(UserDTO userDTO) {
        logger.info("Creating new user: {}", userDTO.email());

        // Validate email uniqueness
        if (userRepository.existsByEmail(userDTO.email())) {
            throw new RuntimeException("User with email '" + userDTO.email() + "' already exists");
        }

        // Validate organization exists
        if (!organizationService.organizationExists(userDTO.organizationId())) {
            throw new RuntimeException("Organization not found with ID: " + userDTO.organizationId());
        }

        // Create entity
        User user = User.builder()
                .organizationId(userDTO.organizationId())
                .email(userDTO.email())
                .role(UserRole.fromString(userDTO.role()))
                .fullName(userDTO.fullName())
                .build();

        // Save and return
        User saved = userRepository.save(user);
        logger.info("User created with ID: {}", saved.getId());

        return UserDTO.fromEntity(saved);
    }

    /**
     * Create admin user (system-level operation)
     */
    public UserDTO createAdminUser(UUID organizationId, String email, String fullName) {
        logger.info("Creating admin user: {}", email);

        UserDTO adminDTO = UserDTO.createRequest(
                organizationId,
                email,
                UserRole.ADMIN.name(),
                fullName
        );

        return createUser(adminDTO);
    }

    /**
     * Update user
     */
    public UserDTO updateUser(UUID id, UserDTO userDTO) {
        logger.info("Updating user with ID: {}", id);

        // Check if user exists
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + id));

        // Check email uniqueness if changing email
        if (!existing.getEmail().equals(userDTO.email()) &&
                userRepository.existsByEmail(userDTO.email())) {
            throw new RuntimeException("User with email '" + userDTO.email() + "' already exists");
        }

        // Update entity (cannot change organization)
        User updated = existing.toBuilder()
                .email(userDTO.email())
                .role(UserRole.fromString(userDTO.role()))
                .fullName(userDTO.fullName())
                .build();

        // Save and return
        User saved = userRepository.update(updated);
        logger.info("User updated: {}", saved.getId());

        return UserDTO.fromEntity(saved);
    }

    /**
     * Delete user
     */
    public void deleteUser(UUID id) {
        logger.info("Deleting user with ID: {}", id);

        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with ID: " + id);
        }

        userRepository.deleteById(id);
        logger.info("User deleted: {}", id);
    }

    /**
     * Check if user is admin
     */
    public boolean isAdmin(UUID userId) {
        return userRepository.findById(userId)
                .map(User::isAdmin)
                .orElse(false);
    }

    /**
     * Check if user is admin by email
     */
    public boolean isAdminByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(User::isAdmin)
                .orElse(false);
    }

    /**
     * Admin impersonation: Get organization ID for a user
     * Admins can impersonate by acting as if they belong to target organization
     */
    public UUID getOrganizationIdForUser(String email) {
        return userRepository.findByEmail(email)
                .map(User::getOrganizationId)
                .orElseThrow(() -> new RuntimeException("User not found with email: " + email));
    }

    /**
     * Verify user has access to organization
     * Admins have access to all organizations
     */
    public boolean hasAccessToOrganization(String email, UUID organizationId) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Admins have access to all organizations
        if (user.isAdmin()) {
            return true;
        }

        // Regular users only access their own organization
        return user.getOrganizationId().equals(organizationId);
    }
}