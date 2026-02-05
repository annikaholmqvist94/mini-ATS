package com.miniats.domain.enums;


/**
 * Application status enumeration for Kanban board.
 * Fixed pipeline stages that cannot be configured by users.
 */
public enum ApplicationStatus {
    NEW(1),
    SCREENING(2),
    INTERVIEW(3),
    OFFER(4),
    REJECTED(5);

    private final int stageOrder;

    ApplicationStatus(int stageOrder) {
        this.stageOrder = stageOrder;
    }

    public int getStageOrder() {
        return stageOrder;
    }

    public static ApplicationStatus fromString(String status) {
        if (status == null) {
            return NEW;
        }
        try {
            return ApplicationStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            return NEW;
        }
    }

    public static ApplicationStatus fromStageOrder(int order) {
        for (ApplicationStatus status : values()) {
            if (status.stageOrder == order) {
                return status;
            }
        }
        return NEW;
    }
}