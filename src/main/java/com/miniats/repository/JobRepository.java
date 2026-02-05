package com.miniats.repository;

import com.miniats.domain.enums.JobStatus;
import com.miniats.domain.model.Job;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Job entity.
 * Defines contract for job data access operations.
 */
public interface JobRepository {

    /**
     * Find job by ID
     */
    Optional<Job> findById(UUID id);

    /**
     * Find all jobs in an organization
     */
    List<Job> findByOrganizationId(UUID organizationId);

    /**
     * Find jobs by organization and status
     */
    List<Job> findByOrganizationIdAndStatus(UUID organizationId, JobStatus status);

    /**
     * Find active jobs in an organization
     */
    List<Job> findActiveJobsByOrganizationId(UUID organizationId);

    /**
     * Find jobs by title (search within organization)
     */
    List<Job> findByOrganizationIdAndTitleContaining(UUID organizationId, String titleKeyword);

    /**
     * Find jobs by department
     */
    List<Job> findByOrganizationIdAndDepartment(UUID organizationId, String department);

    /**
     * Get all jobs (admin only)
     */
    List<Job> findAll();

    /**
     * Create new job
     */
    Job save(Job job);

    /**
     * Update existing job
     */
    Job update(Job job);

    /**
     * Delete job by ID
     */
    void deleteById(UUID id);

    /**
     * Check if job exists by ID
     */
    boolean existsById(UUID id);

    /**
     * Check if job belongs to organization
     */
    boolean existsByIdAndOrganizationId(UUID id, UUID organizationId);

    /**
     * Count jobs by organization
     */
    long countByOrganizationId(UUID organizationId);

    /**
     * Count active jobs by organization
     */
    long countActiveJobsByOrganizationId(UUID organizationId);
}