package com.miniats.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.miniats.domain.enums.JobStatus;
import com.miniats.domain.model.Job;
import java.time.Instant;
import java.util.UUID;

/**
 * Immutable DTO for Job entity.
 * Used for API requests and responses.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record JobDTO(
        UUID id,
        UUID organizationId,
        String title,
        String description,
        String department,
        String location,
        String status,
        UUID createdBy,
        Instant createdAt,
        Instant updatedAt
) {

    /**
     * Convert domain entity to DTO
     */
    public static JobDTO fromEntity(Job job) {
        if (job == null) {
            return null;
        }
        return new JobDTO(
                job.getId(),
                job.getOrganizationId(),
                job.getTitle(),
                job.getDescription(),
                job.getDepartment(),
                job.getLocation(),
                job.getStatus() != null ? job.getStatus().name() : null,
                job.getCreatedBy(),
                job.getCreatedAt(),
                job.getUpdatedAt()
        );
    }

    /**
     * Convert DTO to domain entity
     */
    public Job toEntity() {
        return Job.builder()
                .id(this.id)
                .organizationId(this.organizationId)
                .title(this.title)
                .description(this.description)
                .department(this.department)
                .location(this.location)
                .status(this.status != null ? JobStatus.fromString(this.status) : JobStatus.ACTIVE)
                .createdBy(this.createdBy)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }

    /**
     * Create request DTO for new job
     */
    public static JobDTO createRequest(
            UUID organizationId,
            String title,
            String description,
            String department,
            String location,
            UUID createdBy
    ) {
        return new JobDTO(
                null,
                organizationId,
                title,
                description,
                department,
                location,
                JobStatus.ACTIVE.name(),
                createdBy,
                null,
                null
        );
    }

    /**
     * Update request DTO (preserves ID and organization)
     */
    public static JobDTO updateRequest(
            UUID id,
            UUID organizationId,
            String title,
            String description,
            String department,
            String location,
            String status
    ) {
        return new JobDTO(
                id,
                organizationId,
                title,
                description,
                department,
                location,
                status,
                null,
                null,
                null
        );
    }
}
