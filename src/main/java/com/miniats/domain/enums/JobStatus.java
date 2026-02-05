package com.miniats.domain.enums;

/**
 * Job status enumeration.
 * ACTIVE - Currently accepting applications
 * CLOSED - No longer accepting applications
 * DRAFT - Not yet published
 */
public enum JobStatus {
    ACTIVE,
    CLOSED,
    DRAFT;

    public static JobStatus fromString(String status) {
        if (status == null) {
            return ACTIVE;
        }
        try {
            return JobStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ACTIVE;
        }
    }
}

