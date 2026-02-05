package com.miniats.controller;

import com.miniats.dto.OrganizationDTO;
import com.miniats.service.OrganizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST Controller for Organization management.
 * Base path: /api/organizations
 */
@RestController
@RequestMapping("/api/organizations")
public class OrganizationController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(OrganizationController.class);

    private final OrganizationService organizationService;

    public OrganizationController(OrganizationService organizationService) {
        this.organizationService = organizationService;
    }

    /**
     * GET /api/organizations
     * Get all organizations (admin only)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrganizationDTO>>> getAllOrganizations() {
        logger.info("GET /api/organizations - Fetching all organizations");
        List<OrganizationDTO> organizations = organizationService.getAllOrganizations();
        return success(organizations);
    }

    /**
     * GET /api/organizations/{id}
     * Get organization by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrganizationDTO>> getOrganizationById(
            @PathVariable UUID id
    ) {
        logger.info("GET /api/organizations/{} - Fetching organization", id);
        OrganizationDTO organization = organizationService.getOrganizationById(id);
        return success(organization);
    }

    /**
     * GET /api/organizations/name/{name}
     * Get organization by name
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<ApiResponse<OrganizationDTO>> getOrganizationByName(
            @PathVariable String name
    ) {
        logger.info("GET /api/organizations/name/{} - Fetching organization", name);
        OrganizationDTO organization = organizationService.getOrganizationByName(name);
        return success(organization);
    }

    /**
     * POST /api/organizations
     * Create new organization
     */
    @PostMapping
    public ResponseEntity<ApiResponse<OrganizationDTO>> createOrganization(
            @RequestBody OrganizationDTO organizationDTO
    ) {
        logger.info("POST /api/organizations - Creating organization: {}", organizationDTO.name());
        OrganizationDTO created = organizationService.createOrganization(organizationDTO);
        return created(created);
    }

    /**
     * PUT /api/organizations/{id}
     * Update organization
     */
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<OrganizationDTO>> updateOrganization(
            @PathVariable UUID id,
            @RequestBody OrganizationDTO organizationDTO
    ) {
        logger.info("PUT /api/organizations/{} - Updating organization", id);
        OrganizationDTO updated = organizationService.updateOrganization(id, organizationDTO);
        return success(updated);
    }

    /**
     * DELETE /api/organizations/{id}
     * Delete organization
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganization(@PathVariable UUID id) {
        logger.info("DELETE /api/organizations/{} - Deleting organization", id);
        organizationService.deleteOrganization(id);
        return noContent();
    }
}