package com.miniats.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.miniats.domain.model.Activity;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Immutable DTO for Activity entity.
 * Used for API requests and responses.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ActivityDTO(
        UUID id,
        UUID organizationId,
        UUID candidateId,
        UUID applicationId,
        String activityType,
        String description,
        Map<String, Object> metadata,
        UUID createdBy,
        Instant createdAt
) {

    /**
     * Convert domain entity to DTO
     */
    public static ActivityDTO fromEntity(Activity activity) {
        if (activity == null) {
            return null;
        }
        return new ActivityDTO(
                activity.getId(),
                activity.getOrganizationId(),
                activity.getCandidateId(),
                activity.getApplicationId(),
                activity.getActivityType(),
                activity.getDescription(),
                activity.getMetadata(),
                activity.getCreatedBy(),
                activity.getCreatedAt()
        );
    }

    /**
     * Convert DTO to domain entity
     */
    public Activity toEntity() {
        return Activity.builder()
                .id(this.id)
                .organizationId(this.organizationId)
                .candidateId(this.candidateId)
                .applicationId(this.applicationId)
                .activityType(this.activityType)
                .description(this.description)
                .metadata(this.metadata)
                .createdBy(this.createdBy)
                .createdAt(this.createdAt)
                .build();
    }

    /**
     * Create request DTO for new activity
     */
    public static ActivityDTO createRequest(
            UUID organizationId,
            UUID candidateId,
            String activityType,
            String description
    ) {
        return new ActivityDTO(
                null,
                organizationId,
                candidateId,
                null,
                activityType,
                description,
                Map.of(),
                null,
                null
        );
    }
}