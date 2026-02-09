package com.miniats.controller;

import com.miniats.dto.UserDTO;
import com.miniats.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for User management.
 * Base path: /users
 */
@RestController
@RequestMapping("/users")
public class UserController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * GET /api/users
     * Get all users (admin only)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllUsers() {
        logger.info("GET /api/users - Fetching all users");
        List<UserDTO> users = userService.getAllUsers();
        return success(users);
    }

    /**
     * GET /api/users/{id}
     * Get user by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable UUID id) {
        logger.info("GET /api/users/{} - Fetching user", id);
        UserDTO user = userService.getUserById(id);
        return success(user);
    }

    /**
     * GET /api/users/email/{email}
     * Get user by email
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<ApiResponse<UserDTO>> getUserByEmail(@PathVariable String email) {
        logger.info("GET /api/users/email/{} - Fetching user", email);
        UserDTO user = userService.getUserByEmail(email);
        return success(user);
    }

    /**
     * GET /api/users/organization/{organizationId}
     * Get all users in an organization
     */
    @GetMapping("/organization/{organizationId}")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getUsersByOrganization(
            @PathVariable UUID organizationId
    ) {
        logger.info("GET /api/users/organization/{} - Fetching users", organizationId);
        List<UserDTO> users = userService.getUsersByOrganization(organizationId);
        return success(users);
    }

    /**
     * GET /api/users/admins
     * Get all admin users (system-wide)
     */
    @GetMapping("/admins")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getAllAdmins() {
        logger.info("GET /api/users/admins - Fetching all admin users");
        List<UserDTO> admins = userService.getAllAdmins();
        return success(admins);
    }

    /**
     * POST /api/users
     * Create new user
     */
    @PostMapping
    public ResponseEntity<ApiResponse<UserDTO>> createUser(@RequestBody UserDTO userDTO) {
        logger.info("POST /api/users - Creating user: {}", userDTO.getEmail());
        UserDTO created = userService.createUser(userDTO);
        return created(created);
    }

    /**
     * POST /api/users/admin
     * Create new admin user
     */
    @PostMapping("/admin")
    public ResponseEntity<ApiResponse<UserDTO>> createAdminUser(
            @RequestBody CreateAdminRequest request
    ) {
        logger.info("POST /api/users/admin - Creating admin user: {}", request.email);
        UserDTO created = userService.createAdminUser(
                request.organizationId,
                request.email,
                request.fullName,
                request.password
        );
        return created(created);
    }

    /**
     * PUT /api/users/{id}
     * Update user
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(
            @PathVariable UUID id,
            @RequestBody UserDTO userDTO
    ) {
        logger.info("PUT /api/users/{} - Updating user", id);
        UserDTO updated = userService.updateUser(id, userDTO);
        return success(updated);
    }

    /**
     * DELETE /api/users/{id}
     * Delete user
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        logger.info("DELETE /api/users/{} - Deleting user", id);
        userService.deleteUser(id);
        return noContent();
    }

    /**
     * GET /api/users/{id}/is-admin
     * Check if user is admin
     */
    @GetMapping("/{id}/is-admin")
    public ResponseEntity<ApiResponse<Boolean>> isAdmin(@PathVariable UUID id) {
        logger.info("GET /api/users/{}/is-admin - Checking admin status", id);
        boolean isAdmin = userService.isAdmin(id);
        return success(isAdmin);
    }

    /**
     * GET /api/users/email/{email}/organization
     * Get organization ID for user (used for impersonation)
     */
    @GetMapping("/email/{email}/organization")
    public ResponseEntity<ApiResponse<UUID>> getOrganizationForUser(
            @PathVariable String email
    ) {
        logger.info("GET /api/users/email/{}/organization - Getting organization", email);
        UUID organizationId = userService.getOrganizationIdForUser(email);
        return success(organizationId);
    }

    /**
     * Request object for creating admin user
     */
    public record CreateAdminRequest(
            UUID organizationId,
            String email,
            String fullName,
            String password
    ) {}
}