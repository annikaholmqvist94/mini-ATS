package com.miniats.domain.model;

import com.miniats.domain.enums.ApplicationStatus;
import java.time.Instant;
import java.util.UUID;

/**
 * Immutable Application entity.
 * Represents the many-to-many relationship between Job and Candidate.
 * Tracks the candidate's progress through the hiring pipeline.
 */
public final class Application {
    private final UUID id;
    private final UUID jobId;
    private final UUID candidateId;
    private final ApplicationStatus status;
    private final int stageOrder;
    private final Instant appliedAt;
    private final Instant updatedAt;
    private final String notes;

    private Application(Builder builder) {
        this.id = builder.id;
        this.jobId = builder.jobId;
        this.candidateId = builder.candidateId;
        this.status = builder.status;
        this.stageOrder = builder.stageOrder;
        this.appliedAt = builder.appliedAt;
        this.updatedAt = builder.updatedAt;
        this.notes = builder.notes;
    }

    public UUID getId() {
        return id;
    }

    public UUID getJobId() {
        return jobId;
    }

    public UUID getCandidateId() {
        return candidateId;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public int getStageOrder() {
        return stageOrder;
    }

    public Instant getAppliedAt() {
        return appliedAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public String getNotes() {
        return notes;
    }

    public boolean isRejected() {
        return ApplicationStatus.REJECTED.equals(status);
    }

    public boolean canAdvance() {
        return !ApplicationStatus.OFFER.equals(status) && !ApplicationStatus.REJECTED.equals(status);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder()
                .id(this.id)
                .jobId(this.jobId)
                .candidateId(this.candidateId)
                .status(this.status)
                .stageOrder(this.stageOrder)
                .appliedAt(this.appliedAt)
                .updatedAt(this.updatedAt)
                .notes(this.notes);
    }

    public static final class Builder {
        private UUID id;
        private UUID jobId;
        private UUID candidateId;
        private ApplicationStatus status;
        private int stageOrder;
        private Instant appliedAt;
        private Instant updatedAt;
        private String notes;

        private Builder() {
        }

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder jobId(UUID jobId) {
            this.jobId = jobId;
            return this;
        }

        public Builder candidateId(UUID candidateId) {
            this.candidateId = candidateId;
            return this;
        }

        public Builder status(ApplicationStatus status) {
            this.status = status;
            // Auto-sync stage order with status
            if (status != null) {
                this.stageOrder = status.getStageOrder();
            }
            return this;
        }

        public Builder stageOrder(int stageOrder) {
            this.stageOrder = stageOrder;
            return this;
        }

        public Builder appliedAt(Instant appliedAt) {
            this.appliedAt = appliedAt;
            return this;
        }

        public Builder updatedAt(Instant updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        public Application build() {
            if (jobId == null) {
                throw new IllegalStateException("Application must be associated with a job");
            }
            if (candidateId == null) {
                throw new IllegalStateException("Application must be associated with a candidate");
            }
            if (status == null) {
                this.status = ApplicationStatus.NEW;
                this.stageOrder = ApplicationStatus.NEW.getStageOrder();
            }
            // Ensure stage order matches status
            if (this.stageOrder == 0) {
                this.stageOrder = this.status.getStageOrder();
            }
            return new Application(this);
        }
    }

    @Override
    public String toString() {
        return "Application{" +
                "id=" + id +
                ", jobId=" + jobId +
                ", candidateId=" + candidateId +
                ", status=" + status +
                ", stageOrder=" + stageOrder +
                ", appliedAt=" + appliedAt +
                '}';
    }
}
