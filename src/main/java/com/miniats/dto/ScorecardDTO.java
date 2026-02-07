package com.miniats.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.miniats.domain.model.Scorecard;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Immutable DTO for Scorecard entity.
 * Used for API requests and responses.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ScorecardDTO(
        UUID id,
        UUID candidateId,
        UUID organizationId,
        Integer technicalSkills,
        Integer communication,
        Integer culturalFit,
        Integer experienceLevel,
        Integer problemSolving,
        BigDecimal overallScore,
        UUID evaluatedBy,
        String notes,
        Instant createdAt,
        Instant updatedAt
) {

    /**
     * Convert domain entity to DTO
     */
    public static ScorecardDTO fromEntity(Scorecard scorecard) {
        if (scorecard == null) {
            return null;
        }
        return new ScorecardDTO(
                scorecard.getId(),
                scorecard.getCandidateId(),
                scorecard.getOrganizationId(),
                scorecard.getTechnicalSkills(),
                scorecard.getCommunication(),
                scorecard.getCulturalFit(),
                scorecard.getExperienceLevel(),
                scorecard.getProblemSolving(),
                scorecard.getOverallScore(),
                scorecard.getEvaluatedBy(),
                scorecard.getNotes(),
                scorecard.getCreatedAt(),
                scorecard.getUpdatedAt()
        );
    }

    /**
     * Convert DTO to domain entity
     */
    public Scorecard toEntity() {
        return Scorecard.builder()
                .id(this.id)
                .candidateId(this.candidateId)
                .organizationId(this.organizationId)
                .technicalSkills(this.technicalSkills)
                .communication(this.communication)
                .culturalFit(this.culturalFit)
                .experienceLevel(this.experienceLevel)
                .problemSolving(this.problemSolving)
                .evaluatedBy(this.evaluatedBy)
                .notes(this.notes)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }

    /**
     * Create request DTO for new scorecard
     */
    public static ScorecardDTO createRequest(
            UUID candidateId,
            UUID organizationId,
            Integer technicalSkills,
            Integer communication,
            Integer culturalFit,
            Integer experienceLevel,
            Integer problemSolving,
            UUID evaluatedBy
    ) {
        return new ScorecardDTO(
                null,
                candidateId,
                organizationId,
                technicalSkills,
                communication,
                culturalFit,
                experienceLevel,
                problemSolving,
                null, // overallScore calculated automatically
                evaluatedBy,
                null,
                null,
                null
        );
    }
}