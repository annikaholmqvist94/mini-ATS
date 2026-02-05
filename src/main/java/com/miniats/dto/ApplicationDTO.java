package com.miniats.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.miniats.domain.enums.ApplicationStatus;
import com.miniats.domain.model.Application;
import java.time.Instant;
import java.util.UUID;

/**
 * Immutable DTO for Application entity.
 * Used for API requests and responses, especially for Kanban board.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApplicationDTO(
        UUID id,
        UUID jobId,
        UUID candidateId,
        String status,
        Integer stageOrder,
        Instant appliedAt,
        Instant updatedAt,
        String notes,
        // Enriched fields for Kanban view
        CandidateDTO candidate,
        JobDTO job
) {

    /**
     * Convert domain entity to DTO (without enrichment)
     */
    public static ApplicationDTO fromEntity(Application application) {
        if (application == null) {
            return null;
        }
        return new ApplicationDTO(
                application.getId(),
                application.getJobId(),
                application.getCandidateId(),
                application.getStatus() != null ? application.getStatus().name() : null,
                application.getStageOrder(),
                application.getAppliedAt(),
                application.getUpdatedAt(),
                application.getNotes(),
                null,
                null
        );
    }

    /**
     * Convert domain entity to DTO with enriched candidate and job data
     */
    public static ApplicationDTO fromEntityEnriched(
            Application application,
            CandidateDTO candidate,
            JobDTO job
    ) {
        if (application == null) {
            return null;
        }
        return new ApplicationDTO(
                application.getId(),
                application.getJobId(),
                application.getCandidateId(),
                application.getStatus() != null ? application.getStatus().name() : null,
                application.getStageOrder(),
                application.getAppliedAt(),
                application.getUpdatedAt(),
                application.getNotes(),
                candidate,
                job
        );
    }

    /**
     * Convert DTO to domain entity
     */
    public Application toEntity() {
        return Application.builder()
                .id(this.id)
                .jobId(this.jobId)
                .candidateId(this.candidateId)
                .status(this.status != null ? ApplicationStatus.fromString(this.status) : ApplicationStatus.NEW)
                .stageOrder(this.stageOrder != null ? this.stageOrder : 1)
                .appliedAt(this.appliedAt)
                .updatedAt(this.updatedAt)
                .notes(this.notes)
                .build();
    }

    /**
     * Create request DTO for new application
     */
    public static ApplicationDTO createRequest(
            UUID jobId,
            UUID candidateId,
            String notes
    ) {
        return new ApplicationDTO(
                null,
                jobId,
                candidateId,
                ApplicationStatus.NEW.name(),
                ApplicationStatus.NEW.getStageOrder(),
                null,
                null,
                notes,
                null,
                null
        );
    }

    /**
     * Update status request DTO
     */
    public static ApplicationDTO updateStatusRequest(
            UUID id,
            String status,
            String notes
    ) {
        ApplicationStatus appStatus = ApplicationStatus.fromString(status);
        return new ApplicationDTO(
                id,
                null,
                null,
                appStatus.name(),
                appStatus.getStageOrder(),
                null,
                null,
                notes,
                null,
                null
        );
    }
}

