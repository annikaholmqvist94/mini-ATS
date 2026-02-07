package com.miniats.domain.model;

import java.time.Instant;
import java.util.List;
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
    private final String city;
    private final String availability; // available, unavailable, notice_period
    private final String educationLevel; // high_school, bachelor, master, phd, other
    private final Boolean isExperienced;
    private final List<String> skills;
    private final String avatarUrl;
    private final String summary;
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
        this.city = builder.city;
        this.availability = builder.availability;
        this.educationLevel = builder.educationLevel;
        this.isExperienced = builder.isExperienced;
        this.skills = builder.skills != null ? List.copyOf(builder.skills) : List.of();
        this.avatarUrl = builder.avatarUrl;
        this.summary = builder.summary;
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

    public String getCity() {
        return city;
    }

    public String getAvailability() {
        return availability;
    }
    public String getEducationLevel() {
        return educationLevel;
    }

    public Boolean getIsExperienced() {
        return isExperienced;
    }

    public List<String> getSkills() {
        return skills;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getSummary() {
        return summary;
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
                .city(this.city)
                .availability(this.availability)
                .educationLevel(this.educationLevel)
                .isExperienced(this.isExperienced)
                .skills(this.skills)
                .avatarUrl(this.avatarUrl)
                .summary(this.summary)
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
        private String city;
        private String availability;
        private String educationLevel;
        private Boolean isExperienced;
        private List<String> skills;
        private String avatarUrl;
        private String summary;
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
        public Builder city(String city) { this.city = city; return this; }
        public Builder availability(String availability) { this.availability = availability; return this; }
        public Builder educationLevel(String educationLevel) { this.educationLevel = educationLevel; return this; }
        public Builder isExperienced(Boolean isExperienced) { this.isExperienced = isExperienced; return this; }
        public Builder skills(List<String> skills) { this.skills = skills; return this; }
        public Builder avatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; return this; }
        public Builder summary(String summary) { this.summary = summary; return this; }

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

