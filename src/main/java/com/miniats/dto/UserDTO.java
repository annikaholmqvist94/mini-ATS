package com.miniats.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.miniats.domain.enums.UserRole;
import com.miniats.domain.model.User;
import java.time.Instant;
import java.util.UUID;

/**
 * Immutable DTO for User entity.
 * Used for API requests and responses.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserDTO(
        UUID id,
        UUID organizationId,
        String email,
        String role,
        String fullName,
        Instant createdAt,
        Instant updatedAt
) {

    /**
     * Convert domain entity to DTO
     */
    public static UserDTO fromEntity(User user) {
        if (user == null) {
            return null;
        }
        return new UserDTO(
                user.getId(),
                user.getOrganizationId(),
                user.getEmail(),
                user.getRole() != null ? user.getRole().name() : null,
                user.getFullName(),
                user.getCreatedAt(),
                user.getUpdatedAt()
        );
    }

    /**
     * Convert DTO to domain entity
     */
    public User toEntity() {
        return User.builder()
                .id(this.id)
                .organizationId(this.organizationId)
                .email(this.email)
                .role(this.role != null ? UserRole.fromString(this.role) : UserRole.USER)
                .fullName(this.fullName)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }

    /**
     * Create request DTO for new user
     */
    public static UserDTO createRequest(
            UUID organizationId,
            String email,
            String role,
            String fullName
    ) {
        return new UserDTO(null, organizationId, email, role, fullName, null, null);
    }

    /**
     * Check if user is admin
     */
    public boolean isAdmin() {
        return UserRole.ADMIN.name().equals(this.role);
    }
}

