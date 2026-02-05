package com.miniats.domain.model;

import com.miniats.domain.enums.JobStatus;
import java.time.Instant;
import java.util.UUID;

/**
 * Immutable Job entity.
 * Represents a job opening within an organization.
 */
public final class Job {
    private final UUID id;
    private final UUID organizationId;
    private final String title;
    private final String description;
    private final String department;
    private final String location;
    private final JobStatus status;
    private final UUID createdBy;
    private final Instant createdAt;
    private final Instant updatedAt;

    private Job(Builder builder) {
        this.id = builder.id;
        this.organizationId = builder.organizationId;
        this.title = builder.title;
        this.description = builder.description;
        this.department = builder.department;
        this.location = builder.location;
        this.status = builder.status;
        this.createdBy = builder.createdBy;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getDepartment() {
        return department;
    }

    public String getLocation() {
        return location;
    }

    public JobStatus getStatus() {
        return status;
    }

    public UUID getCreatedBy() {
        return createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public boolean isActive() {
        return JobStatus.ACTIVE.equals(status);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder()
                .id(this.id)
                .organizationId(this.organizationId)
                .title(this.title)
                .description(this.description)
                .department(this.department)
                .location(this.location)
                .status(this.status)
                .createdBy(this.createdBy)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt);
    }

    public static final class Builder {
        private UUID id;
        private UUID organizationId;
        private String title;
        private String description;
        private String department;
        private String location;
        private JobStatus status;
        private UUID createdBy;
        private Instant createdAt;
        private Instant updatedAt;

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

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder department(String department) {
            this.department = department;
            return this;
        }

        public Builder location(String location) {
            this.location = location;
            return this;
        }

        public Builder status(JobStatus status) {
            this.status = status;
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

        public Builder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Job build() {
            if (title == null || title.trim().isEmpty()) {
                throw new IllegalStateException("Job title cannot be null or empty");
            }
            if (organizationId == null) {
                throw new IllegalStateException("Job must belong to an organization");
            }
            if (status == null) {
                this.status = JobStatus.ACTIVE;
            }
            return new Job(this);
        }
    }

    @Override
    public String toString() {
        return "Job{" +
                "id=" + id +
                ", organizationId=" + organizationId +
                ", title='" + title + '\'' +
                ", department='" + department + '\'' +
                ", location='" + location + '\'' +
                ", status=" + status +
                ", createdAt=" + createdAt +
                '}';
    }
}