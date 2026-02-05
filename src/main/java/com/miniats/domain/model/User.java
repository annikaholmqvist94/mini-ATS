package com.miniats.domain.model;

import com.miniats.domain.enums.UserRole;
import java.time.Instant;
import java.util.UUID;

/**
 * Immutable User entity.
 * Represents both Admin and regular Customer users.
 */
public final class User {
    private final UUID id;
    private final UUID organizationId;
    private final String email;
    private final UserRole role;
    private final String fullName;
    private final Instant createdAt;
    private final Instant updatedAt;

    private User(Builder builder) {
        this.id = builder.id;
        this.organizationId = builder.organizationId;
        this.email = builder.email;
        this.role = builder.role;
        this.fullName = builder.fullName;
        this.createdAt = builder.createdAt;
        this.updatedAt = builder.updatedAt;
    }

    public UUID getId() {
        return id;
    }

    public UUID getOrganizationId() {
        return organizationId;
    }

    public String getEmail() {
        return email;
    }

    public UserRole getRole() {
        return role;
    }

    public String getFullName() {
        return fullName;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public boolean isAdmin() {
        return UserRole.ADMIN.equals(role);
    }

    public static Builder builder() {
        return new Builder();
    }

    public Builder toBuilder() {
        return new Builder()
                .id(this.id)
                .organizationId(this.organizationId)
                .email(this.email)
                .role(this.role)
                .fullName(this.fullName)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt);
    }

    public static final class Builder {
        private UUID id;
        private UUID organizationId;
        private String email;
        private UserRole role;
        private String fullName;
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

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder role(UserRole role) {
            this.role = role;
            return this;
        }

        public Builder fullName(String fullName) {
            this.fullName = fullName;
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

        public User build() {
            if (email == null || email.trim().isEmpty()) {
                throw new IllegalStateException("User email cannot be null or empty");
            }
            if (organizationId == null) {
                throw new IllegalStateException("User must belong to an organization");
            }
            if (role == null) {
                this.role = UserRole.USER;
            }
            return new User(this);
        }
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", organizationId=" + organizationId +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", fullName='" + fullName + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
