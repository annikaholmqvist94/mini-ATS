package com.miniats.domain.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Immutable Candidate entity.
 * Represents a job candidate within an organization.
 */
public final class Candidate {
    private final UUID id;
    private final UUID organizationId;
    private final String fullName;
    private final String email;
    private final String phone;
    private final String linkedinUrl;
    private final String resumeUrl;
    private final String notes;
    private final Instant createdAt;
    private final Instant updatedAt;

    private Candidate(Builder builder) {
        this.id = builder.id;
        this.organizationId = builder.organizationId;
        this.fullName = builder.fullName;
        this.email = builder.email;
        this.phone = builder.phone;
        this.linkedinUrl = builder.linkedinUrl;
        this.resumeUrl = builder.resumeUrl;
        this.notes = builder.notes;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getLinkedinUrl() {
        return linkedinUrl;
    }

    public String getResumeUrl() {
        return resumeUrl;
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
                .organizationId(this.organizationId)
                .fullName(this.fullName)
                .email(this.email)
                .phone(this.phone)
                .linkedinUrl(this.linkedinUrl)
                .resumeUrl(this.resumeUrl)
                .notes(this.notes)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt);
    }

    public static final class Builder {
        private UUID id;
        private UUID organizationId;
        private String fullName;
        private String email;
        private String phone;
        private String linkedinUrl;
        private String resumeUrl;
        private String notes;
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

        public Builder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder linkedinUrl(String linkedinUrl) {
            this.linkedinUrl = linkedinUrl;
            return this;
        }

        public Builder resumeUrl(String resumeUrl) {
            this.resumeUrl = resumeUrl;
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

        public Candidate build() {
            if (fullName == null || fullName.trim().isEmpty()) {
                throw new IllegalStateException("Candidate full name cannot be null or empty");
            }
            if (organizationId == null) {
                throw new IllegalStateException("Candidate must belong to an organization");
            }
            return new Candidate(this);
        }
    }

    @Override
    public String toString() {
        return "Candidate{" +
                "id=" + id +
                ", organizationId=" + organizationId +
                ", fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}

