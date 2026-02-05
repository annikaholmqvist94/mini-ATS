package com.miniats.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.miniats.domain.model.Organization;
import java.time.Instant;
import java.util.UUID;

/**
 * Immutable DTO for Organization entity.
 * Used for API requests and responses.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrganizationDTO(
        UUID id,
        String name,
        Instant createdAt,
        Instant updatedAt
) {

    /**
     * Convert domain entity to DTO
     */
    public static OrganizationDTO fromEntity(Organization organization) {
        if (organization == null) {
            return null;
        }
        return new OrganizationDTO(
                organization.getId(),
                organization.getName(),
                organization.getCreatedAt(),
                organization.getUpdatedAt()
        );
    }

    /**
     * Convert DTO to domain entity
     */
    public Organization toEntity() {
        return Organization.builder()
                .id(this.id)
                .name(this.name)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }

    /**
     * Create request DTO (without ID and timestamps)
     */
    public static OrganizationDTO createRequest(String name) {
        return new OrganizationDTO(null, name, null, null);
    }
}
