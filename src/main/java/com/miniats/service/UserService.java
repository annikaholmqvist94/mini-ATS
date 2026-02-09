package com.miniats.service;

import com.miniats.domain.enums.UserRole;
import com.miniats.domain.model.User;
import com.miniats.dto.UserDTO;
import com.miniats.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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
    private final SupabaseAuthService supabaseAuthService;

    public UserService(
            UserRepository userRepository,
            OrganizationService organizationService,
            SupabaseAuthService supabaseAuthService
    ) {
        this.userRepository = userRepository;
        this.organizationService = organizationService;
        this.supabaseAuthService = supabaseAuthService;
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
     * Create new user (creates both in Supabase Auth AND public.users)
     *
     * @Transactional ensures rollback if anything fails
     */
    @Transactional
    public UserDTO createUser(UserDTO userDTO) {
        logger.info("Creating new user: {}", userDTO.getEmail());

        // 1. Validate email uniqueness in public.users
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            logger.error("❌ User already exists: {}", userDTO.getEmail());
            throw new RuntimeException("User with email '" + userDTO.getEmail() + "' already exists");
        }

        // 2. Validate organization exists
        if (!organizationService.organizationExists(userDTO.getOrganizationId())) {
            logger.error("❌ Organization not found: {}", userDTO.getOrganizationId());
            throw new RuntimeException("Organization not found with ID: " + userDTO.getOrganizationId());
        }

        // 3. Validate password is provided
        if (userDTO.getPassword() == null || userDTO.getPassword().isBlank()) {
            logger.error("❌ Password is required");
            throw new RuntimeException("Password is required for new users");
        }

        // 4. Create user in Supabase Auth FIRST
        String authUserId;
        try {
            logger.info("🔵 Creating auth user for: {}", userDTO.getEmail());
            authUserId = supabaseAuthService.createAuthUser(
                    userDTO.getEmail(),
                    userDTO.getPassword()
            );
            logger.info("✅ Auth user created with ID: {}", authUserId);
        } catch (Exception e) {
            logger.error("❌ Failed to create auth user: {}", e.getMessage());
            throw new RuntimeException("Failed to create user in authentication system: " + e.getMessage(), e);
        }

        // 5. Create user in public.users with SAME ID
        try {
            User user = User.builder()
                    .id(UUID.fromString(authUserId))  // ← Use Auth User ID!
                    .organizationId(userDTO.getOrganizationId())
                    .email(userDTO.getEmail())
                    .role(UserRole.fromString(userDTO.getRole()))
                    .fullName(userDTO.getFullName())
                    .build();

            User saved = userRepository.save(user);
            logger.info("✅ User created in database with ID: {}", saved.getId());

            return UserDTO.fromEntity(saved);

        } catch (Exception e) {
            // If database insert fails, delete auth user to keep consistency
            logger.error("❌ Failed to create user in database, rolling back auth user");
            try {
                supabaseAuthService.deleteAuthUser(authUserId);
                logger.info("✅ Auth user deleted (rollback)");
            } catch (Exception deleteError) {
                logger.error("❌ Failed to delete auth user during rollback: {}", deleteError.getMessage());
            }
            throw new RuntimeException("Failed to create user in database: " + e.getMessage(), e);
        }
    }

    /**
     * Create admin user (system-level operation)
     * Uses same createUser flow, just sets ADMIN role
     */
    public UserDTO createAdminUser(UUID organizationId, String email, String fullName, String password) {
        logger.info("Creating admin user: {}", email);

        UserDTO adminDTO = UserDTO.builder()
                .organizationId(organizationId)
                .email(email)
                .role(UserRole.ADMIN.name())
                .fullName(fullName)
                .password(password)  // ← VIKTIGT!
                .build();

        return createUser(adminDTO);  // ← Återanvänder main method!
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
        if (!existing.getEmail().equals(userDTO.getEmail()) &&
                userRepository.existsByEmail(userDTO.getEmail())) {
            throw new RuntimeException("User with email '" + userDTO.getEmail() + "' already exists");
        }

        // Update entity (cannot change organization)
        User updated = existing.toBuilder()
                .email(userDTO.getEmail())
                .role(UserRole.fromString(userDTO.getRole()))
                .fullName(userDTO.getFullName())
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