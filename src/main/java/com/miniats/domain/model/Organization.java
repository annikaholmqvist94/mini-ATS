package com.miniats.domain.model;

import java.time.Instant;
import java.util.UUID;

/**
 * Immutable Organization entity.
 * Root entity for multi-tenant architecture.
 */
public final class Organization {
    private final UUID id;
    private final String name;
    private final Instant createdAt;
    private final Instant updatedAt;

    private Organization(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
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
                .name(this.name)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt);
    }

    public static final class Builder {
        private UUID id;
        private String name;
        private Instant createdAt;
        private Instant updatedAt;

        private Builder() {
        }

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
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

        public Organization build() {
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalStateException("Organization name cannot be null or empty");
            }
            return new Organization(this);
        }
    }

    @Override
    public String toString() {
        return "Organization{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
