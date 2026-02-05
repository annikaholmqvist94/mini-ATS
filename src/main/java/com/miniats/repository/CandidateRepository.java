package com.miniats.repository;

import com.miniats.domain.model.Candidate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Candidate entity.
 * Defines contract for candidate data access operations.
 */
public interface CandidateRepository {

    /**
     * Find candidate by ID
     */
    Optional<Candidate> findById(UUID id);

    /**
     * Find all candidates in an organization
     */
    List<Candidate> findByOrganizationId(UUID organizationId);

    /**
     * Find candidates by name (search within organization)
     */
    List<Candidate> findByOrganizationIdAndFullNameContaining(UUID organizationId, String nameKeyword);

    /**
     * Find candidate by email within organization
     */
    Optional<Candidate> findByOrganizationIdAndEmail(UUID organizationId, String email);

    /**
     * Find candidates by LinkedIn URL pattern
     */
    List<Candidate> findByOrganizationIdAndLinkedinUrlContaining(UUID organizationId, String linkedinKeyword);

    /**
     * Get all candidates (admin only)
     */
    List<Candidate> findAll();

    /**
     * Create new candidate
     */
    Candidate save(Candidate candidate);

    /**
     * Update existing candidate
     */
    Candidate update(Candidate candidate);

    /**
     * Delete candidate by ID
     */
    void deleteById(UUID id);

    /**
     * Check if candidate exists by ID
     */
    boolean existsById(UUID id);

    /**
     * Check if candidate belongs to organization
     */
    boolean existsByIdAndOrganizationId(UUID id, UUID organizationId);

    /**
     * Check if candidate exists by email in organization
     */
    boolean existsByOrganizationIdAndEmail(UUID organizationId, String email);

    /**
     * Count candidates by organization
     */
    long countByOrganizationId(UUID organizationId);
}