package com.miniats.repository;

import com.miniats.domain.model.Organization;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Organization entity.
 * Defines contract for data access operations.
 * Implementation is provided by Supabase adapter.
 */
public interface OrganizationRepository {

    /**
     * Find organization by ID
     */
    Optional<Organization> findById(UUID id);

    /**
     * Find organization by name
     */
    Optional<Organization> findByName(String name);

    /**
     * Get all organizations
     */
    List<Organization> findAll();

    /**
     * Create new organization
     */
    Organization save(Organization organization);

    /**
     * Update existing organization
     */
    Organization update(Organization organization);

    /**
     * Delete organization by ID
     */
    void deleteById(UUID id);

    /**
     * Check if organization exists by ID
     */
    boolean existsById(UUID id);

    /**
     * Check if organization exists by name
     */
    boolean existsByName(String name);
}
