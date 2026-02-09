package com.miniats.dto;

import com.miniats.domain.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO for User entity.
 * Used for API requests and responses.
 */
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private UUID id;
    private String email;
    private String fullName;
    private String role;
    private UUID organizationId;
    private String organizationName;
    private Instant createdAt;
    private Instant updatedAt;
    private String password;  // Only for creation, never returned

    /**
     * Convert entity to DTO (without password)
     */
    public static UserDTO fromEntity(User user) {
        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole().name())
                .organizationId(user.getOrganizationId())
                .organizationName(null)
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .password(null)  // Never return password
                .build();
    }

    /**
     * Create request DTO (for creating users)
     */
    public static UserDTO createRequest(
            UUID organizationId,
            String email,
            String role,
            String fullName,
            String password
    ) {
        return UserDTO.builder()
                .organizationId(organizationId)
                .email(email)
                .role(role)
                .fullName(fullName)
                .password(password)
                .build();
    }
}