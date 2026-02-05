package com.miniats.repository;

import com.miniats.domain.enums.ApplicationStatus;
import com.miniats.domain.model.Application;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Application entity.
 * Defines contract for application data access operations.
 * Critical for Kanban board functionality.
 */
public interface ApplicationRepository {

    /**
     * Find application by ID
     */
    Optional<Application> findById(UUID id);

    /**
     * Find application by job and candidate (unique combination)
     */
    Optional<Application> findByJobIdAndCandidateId(UUID jobId, UUID candidateId);

    /**
     * Find all applications for a job
     */
    List<Application> findByJobId(UUID jobId);

    /**
     * Find applications by job and status (for Kanban columns)
     */
    List<Application> findByJobIdAndStatus(UUID jobId, ApplicationStatus status);

    /**
     * Find all applications for a candidate
     */
    List<Application> findByCandidateId(UUID candidateId);

    /**
     * Find applications by organization (through job relationship)
     * This is critical for Kanban view filtered by organization
     */
    List<Application> findByOrganizationId(UUID organizationId);

    /**
     * Find applications by organization and job
     */
    List<Application> findByOrganizationIdAndJobId(UUID organizationId, UUID jobId);

    /**
     * Find applications by organization and status
     */
    List<Application> findByOrganizationIdAndStatus(UUID organizationId, ApplicationStatus status);

    /**
     * Search applications by candidate name within organization
     */
    List<Application> findByOrganizationIdAndCandidateNameContaining(
            UUID organizationId,
            String candidateNameKeyword
    );

    /**
     * Get all applications (admin only)
     */
    List<Application> findAll();

    /**
     * Create new application
     */
    Application save(Application application);

    /**
     * Update existing application
     */
    Application update(Application application);

    /**
     * Delete application by ID
     */
    void deleteById(UUID id);

    /**
     * Check if application exists
     */
    boolean existsById(UUID id);

    /**
     * Check if application exists for job and candidate
     */
    boolean existsByJobIdAndCandidateId(UUID jobId, UUID candidateId);

    /**
     * Count applications by job
     */
    long countByJobId(UUID jobId);

    /**
     * Count applications by status for a job
     */
    long countByJobIdAndStatus(UUID jobId, ApplicationStatus status);

    /**
     * Count applications by organization
     */
    long countByOrganizationId(UUID organizationId);
}