package com.miniats.service;

import com.miniats.domain.model.Organization;
import com.miniats.dto.OrganizationDTO;
import com.miniats.repository.OrganizationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Service for Organization entity operations.
 * Handles business logic for organization management.
 */
@Service
public class OrganizationService {

    private static final Logger logger = LoggerFactory.getLogger(OrganizationService.class);

    private final OrganizationRepository organizationRepository;

    public OrganizationService(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    /**
     * Get organization by ID
     */
    public OrganizationDTO getOrganizationById(UUID id) {
        logger.debug("Fetching organization with ID: {}", id);

        return organizationRepository.findById(id)
                .map(OrganizationDTO::fromEntity)
                .orElseThrow(() -> new RuntimeException("Organization not found with ID: " + id));
    }

    /**
     * Get organization by name
     */
    public OrganizationDTO getOrganizationByName(String name) {
        logger.debug("Fetching organization with name: {}", name);

        return organizationRepository.findByName(name)
                .map(OrganizationDTO::fromEntity)
                .orElseThrow(() -> new RuntimeException("Organization not found with name: " + name));
    }

    /**
     * Get all organizations (admin only)
     */
    public List<OrganizationDTO> getAllOrganizations() {
        logger.debug("Fetching all organizations");

        return organizationRepository.findAll().stream()
                .map(OrganizationDTO::fromEntity)
                .toList();
    }

    /**
     * Create new organization
     */
    public OrganizationDTO createOrganization(OrganizationDTO organizationDTO) {
        logger.info("Creating new organization: {}", organizationDTO.name());

        // Validate name uniqueness
        if (organizationRepository.existsByName(organizationDTO.name())) {
            throw new RuntimeException("Organization with name '" + organizationDTO.name() + "' already exists");
        }

        // Create entity
        Organization organization = Organization.builder()
                .name(organizationDTO.name())
                .build();

        // Save and return
        Organization saved = organizationRepository.save(organization);
        logger.info("Organization created with ID: {}", saved.getId());

        return OrganizationDTO.fromEntity(saved);
    }

    /**
     * Update organization
     */
    public OrganizationDTO updateOrganization(UUID id, OrganizationDTO organizationDTO) {
        logger.info("Updating organization with ID: {}", id);

        // Check if organization exists
        Organization existing = organizationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organization not found with ID: " + id));

        // Check name uniqueness if changing name
        if (!existing.getName().equals(organizationDTO.name()) &&
                organizationRepository.existsByName(organizationDTO.name())) {
            throw new RuntimeException("Organization with name '" + organizationDTO.name() + "' already exists");
        }

        // Update entity
        Organization updated = existing.toBuilder()
                .name(organizationDTO.name())
                .build();

        // Save and return
        Organization saved = organizationRepository.update(updated);
        logger.info("Organization updated: {}", saved.getId());

        return OrganizationDTO.fromEntity(saved);
    }

    /**
     * Delete organization
     */
    public void deleteOrganization(UUID id) {
        logger.info("Deleting organization with ID: {}", id);

        if (!organizationRepository.existsById(id)) {
            throw new RuntimeException("Organization not found with ID: " + id);
        }

        organizationRepository.deleteById(id);
        logger.info("Organization deleted: {}", id);
    }

    /**
     * Check if organization exists
     */
    public boolean organizationExists(UUID id) {
        return organizationRepository.existsById(id);
    }
}