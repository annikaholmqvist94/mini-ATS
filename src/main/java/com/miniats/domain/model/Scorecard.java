package com.miniats.domain.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.UUID;

/**
 * Immutable Scorecard entity.
 * Represents a candidate evaluation with 5 scoring categories.
 */
public final class Scorecard {
    private final UUID id;
    private final UUID candidateId;
    private final UUID organizationId;

    // Evaluation scores (1-5)
    private final Integer technicalSkills;
    private final Integer communication;
    private final Integer culturalFit;
    private final Integer experienceLevel;
    private final Integer problemSolving;

    // Metadata
    private final BigDecimal overallScore; // Calculated average
    private final UUID evaluatedBy;
    private final String notes;

    private final Instant createdAt;
    private final Instant updatedAt;

    private Scorecard(Builder builder) {
        this.id = builder.id;
        this.candidateId = builder.candidateId;
        this.organizationId = builder.organizationId;
        this.technicalSkills = builder.technicalSkills;
        this.communication = builder.communication;
        this.culturalFit = builder.culturalFit;
        this.experienceLevel = builder.experienceLevel;
        this.problemSolving = builder.problemSolving;
        this.overallScore = calculateOverallScore(
                builder.technicalSkills,
                builder.communication,
                builder.culturalFit,
                builder.experienceLevel,
                builder.problemSolving
        );
        this.evaluatedBy = builder.evaluatedBy;
        this.notes = builder.notes;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
    }

    /**
     * Calculate overall score as average of all categories
     */
    private BigDecimal calculateOverallScore(
            Integer tech, Integer comm, Integer culture, Integer exp, Integer problem
    ) {
        if (tech == null && comm == null && culture == null && exp == null && problem == null) {
            return null;
        }

        int count = 0;
        int sum = 0;

        if (tech != null) { sum += tech; count++; }
        if (comm != null) { sum += comm; count++; }
        if (culture != null) { sum += culture; count++; }
        if (exp != null) { sum += exp; count++; }
        if (problem != null) { sum += problem; count++; }

        if (count == 0) return null;

        return BigDecimal.valueOf(sum)
                .divide(BigDecimal.valueOf(count), 1, RoundingMode.HALF_UP);
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public UUID getCandidateId() {
        return candidateId;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public Integer getTechnicalSkills() {
        return technicalSkills;
    }

    public Integer getCommunication() {
        return communication;
    }

    public Integer getCulturalFit() {
        return culturalFit;
    }

    public Integer getExperienceLevel() {
        return experienceLevel;
    }

    public Integer getProblemSolving() {
        return problemSolving;
    }

    public BigDecimal getOverallScore() {
        return overallScore;
    }

    public UUID getEvaluatedBy() {
        return evaluatedBy;
    }

    public String getNotes() {
        return notes;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder()
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
                .updatedAt(this.updatedAt);
    }

    public static final class Builder {
        private UUID id;
        private UUID candidateId;
        private UUID organizationId;
        private Integer technicalSkills;
        private Integer communication;
        private Integer culturalFit;
        private Integer experienceLevel;
        private Integer problemSolving;
        private UUID evaluatedBy;
        private String notes;
        private Instant createdAt;
        private Instant updatedAt;

        private Builder() {
        }

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder candidateId(UUID candidateId) {
            this.candidateId = candidateId;
            return this;
        }

        public Builder organizationId(UUID organizationId) {
            this.organizationId = organizationId;
            return this;
        }

        public Builder technicalSkills(Integer technicalSkills) {
            validateScore(technicalSkills, "Technical Skills");
            this.technicalSkills = technicalSkills;
            return this;
        }

        public Builder communication(Integer communication) {
            validateScore(communication, "Communication");
            this.communication = communication;
            return this;
        }

        public Builder culturalFit(Integer culturalFit) {
            validateScore(culturalFit, "Cultural Fit");
            this.culturalFit = culturalFit;
            return this;
        }

        public Builder experienceLevel(Integer experienceLevel) {
            validateScore(experienceLevel, "Experience Level");
            this.experienceLevel = experienceLevel;
            return this;
        }

        public Builder problemSolving(Integer problemSolving) {
            validateScore(problemSolving, "Problem Solving");
            this.problemSolving = problemSolving;
            return this;
        }

        public Builder evaluatedBy(UUID evaluatedBy) {
            this.evaluatedBy = evaluatedBy;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        private void validateScore(Integer score, String categoryName) {
            if (score != null && (score < 1 || score > 5)) {
                throw new IllegalArgumentException(
                        categoryName + " score must be between 1 and 5, got: " + score
                );
            }
        }

        public Scorecard build() {
            if (candidateId == null) {
                throw new IllegalStateException("Scorecard must be associated with a candidate");
            }
            if (organizationId == null) {
                throw new IllegalStateException("Scorecard must belong to an organization");
            }
            return new Scorecard(this);
        }
    }

    @Override
    public String toString() {
        return "Scorecard{" +
                "id=" + id +
                ", candidateId=" + candidateId +
                ", overallScore=" + overallScore +
                ", evaluatedBy=" + evaluatedBy +
                '}';
    }
}