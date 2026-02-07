package com.miniats.domain.model;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

/**
 * Immutable Activity entity.
 * Represents an activity/event related to a candidate or application.
 */
public final class Activity {
    private final UUID id;
    private final UUID organizationId;
    private final UUID candidateId;
    private final UUID applicationId;
    private final String activityType; // candidate_added, status_changed, note_added, scorecard_updated
    private final String description;
    private final Map<String, Object> metadata;
    private final UUID createdBy;
    private final Instant createdAt;

    private Activity(Builder builder) {
        this.id = builder.id;
        this.organizationId = builder.organizationId;
        this.candidateId = builder.candidateId;
        this.applicationId = builder.applicationId;
        this.activityType = builder.activityType;
        this.description = builder.description;
        this.metadata = builder.metadata != null ? Map.copyOf(builder.metadata) : Map.of();
        this.createdBy = builder.createdBy;
        this.createdAt = builder.createdAt;
    }

    // Getters
    public UUID getId() {
        return id;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public UUID getCandidateId() {
        return candidateId;
    }

    public UUID getApplicationId() {
        return applicationId;
    }

    public String getActivityType() {
        return activityType;
    }

    public String getDescription() {
        return description;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder()
                .id(this.id)
                .organizationId(this.organizationId)
                .candidateId(this.candidateId)
                .applicationId(this.applicationId)
                .activityType(this.activityType)
                .description(this.description)
                .metadata(this.metadata)
                .createdBy(this.createdBy)
                .createdAt(this.createdAt);
    }

    public static final class Builder {
        private UUID id;
        private UUID organizationId;
        private UUID candidateId;
        private UUID applicationId;
        private String activityType;
        private String description;
        private Map<String, Object> metadata;
        private UUID createdBy;
        private Instant createdAt;

        private Builder() {
        }

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder organizationId(UUID organizationId) {
            this.organizationId = organizationId;
            return this;
        }

        public Builder candidateId(UUID candidateId) {
            this.candidateId = candidateId;
            return this;
        }

        public Builder applicationId(UUID applicationId) {
            this.applicationId = applicationId;
            return this;
        }

        public Builder activityType(String activityType) {
            this.activityType = activityType;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder metadata(Map<String, Object> metadata) {
            this.metadata = metadata;
            return this;
        }

        public Builder createdBy(UUID createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder createdAt(Instant createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Activity build() {
            if (organizationId == null) {
                throw new IllegalStateException("Activity must belong to an organization");
            }
            if (activityType == null || activityType.trim().isEmpty()) {
                throw new IllegalStateException("Activity type cannot be null or empty");
            }
            if (description == null || description.trim().isEmpty()) {
                throw new IllegalStateException("Activity description cannot be null or empty");
            }
            return new Activity(this);
        }
    }

    @Override
    public String toString() {
        return "Activity{" +
                "id=" + id +
                ", organizationId=" + organizationId +
                ", activityType='" + activityType + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}