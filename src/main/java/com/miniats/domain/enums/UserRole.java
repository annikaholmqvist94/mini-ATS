package com.miniats.domain.enums;

/**
 * User role enumeration for access control.
 * ADMIN - Full system access including impersonation
 * USER - Standard customer user with organization-scoped access
 */
public enum UserRole {
    ADMIN,
    USER;

    public static UserRole fromString(String role) {
        if (role == null) {
            return USER;
        }
        try {
            return UserRole.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return USER;
        }
    }
}
