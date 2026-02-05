package com.miniats.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * Base controller with common response utilities.
 */
public abstract class BaseController {

    /**
     * Create success response with data
     */
    protected <T> ResponseEntity<ApiResponse<T>> success(T data) {
        return ResponseEntity.ok(new ApiResponse<>(true, data, null, Instant.now()));
    }

    /**
     * Create success response with message
     */
    protected ResponseEntity<ApiResponse<String>> success(String message) {
        return ResponseEntity.ok(new ApiResponse<>(true, message, null, Instant.now()));
    }

    /**
     * Create created response (201)
     */
    protected <T> ResponseEntity<ApiResponse<T>> created(T data) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ApiResponse<>(true, data, null, Instant.now()));
    }

    /**
     * Create no content response (204)
     */
    protected ResponseEntity<Void> noContent() {
        return ResponseEntity.noContent().build();
    }

    /**
     * Create error response
     */
    protected ResponseEntity<ApiResponse<Void>> error(String message, HttpStatus status) {
        return ResponseEntity.status(status)
                .body(new ApiResponse<>(false, null, message, Instant.now()));
    }

    /**
     * Standard API response wrapper
     */
    public record ApiResponse<T>(
            boolean success,
            T data,
            String error,
            Instant timestamp
    ) {}

    /**
     * Paginated response wrapper
     */
    public record PageResponse<T>(
            boolean success,
            T data,
            PageInfo pageInfo,
            Instant timestamp
    ) {}

    /**
     * Pagination metadata
     */
    public record PageInfo(
            int page,
            int size,
            long totalElements,
            int totalPages
    ) {}
}