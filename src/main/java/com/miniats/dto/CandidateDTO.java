package com.miniats.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.miniats.domain.model.Candidate;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Immutable DTO for Candidate entity.
 * Used for API requests and responses.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CandidateDTO(
        UUID id,
        UUID organizationId,
        String fullName,
        String email,
        String phone,
        String linkedinUrl,
        String resumeUrl,
        String notes,
        String city,
        String availability,
        String educationLevel,
        Boolean isExperienced,
        List<String> skills,
        String avatarUrl,
        String summary,
        Instant createdAt,
        Instant updatedAt
) {

    /**
     * Convert domain entity to DTO
     */
    public static CandidateDTO fromEntity(Candidate candidate) {
        if (candidate == null) {
            return null;
        }
        return new CandidateDTO(
                candidate.getId(),
                candidate.getOrganizationId(),
                candidate.getFullName(),
                candidate.getEmail(),
                candidate.getPhone(),
                candidate.getLinkedinUrl(),
                candidate.getResumeUrl(),
                candidate.getNotes(),
                candidate.getCity(),
                candidate.getAvailability(),
                candidate.getEducationLevel(),
                candidate.getIsExperienced(),
                candidate.getSkills(),
                candidate.getAvatarUrl(),
                candidate.getSummary(),
                candidate.getCreatedAt(),
                candidate.getUpdatedAt()
        );
    }

    /**
     * Convert DTO to domain entity
     */
    public Candidate toEntity() {
        return Candidate.builder()
                .id(this.id)
                .organizationId(this.organizationId)
                .fullName(this.fullName)
                .email(this.email)
                .phone(this.phone)
                .linkedinUrl(this.linkedinUrl)
                .resumeUrl(this.resumeUrl)
                .notes(this.notes)
                .city(this.city)
                .availability(this.availability)
                .educationLevel(this.educationLevel)
                .isExperienced(this.isExperienced)
                .skills(this.skills)
                .avatarUrl(this.avatarUrl)
                .summary(this.summary)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }

    /**
     * Create request DTO for new candidate
     */
    public static CandidateDTO createRequest(
            UUID organizationId,
            String fullName,
            String email,
            String phone,
            String linkedinUrl,
            String notes,
            String city,
            String availability,
            String educationLevel,
            Boolean isExperienced,
            List<String> skills,
            String avatarUrl,
            String summary
    ) {
        return new CandidateDTO(
                null,
                organizationId,
                fullName,
                email,
                phone,
                linkedinUrl,
                null, // resumeUrl handled separately via file upload
                notes,
                city,
                availability,
                educationLevel,
                isExperienced,
                skills,
                avatarUrl,
                summary,
                null,
                null
        );
    }

    /**
     * Update request DTO
     */
    public static CandidateDTO updateRequest(
            UUID id,
            UUID organizationId,
            String fullName,
            String email,
            String phone,
            String linkedinUrl,
            String resumeUrl,
            String notes,
            String city,
            String availability,
            String educationLevel,
            Boolean isExperienced,
            List<String> skills,
            String avatarUrl,
            String summary
    ) {
        return new CandidateDTO(
                id,
                organizationId,
                fullName,
                email,
                phone,
                linkedinUrl,
                resumeUrl,
                notes,
                city,
                availability,
                educationLevel,
                isExperienced,
                skills,
                avatarUrl,
                summary,
                null,
                null
        );
    }
}