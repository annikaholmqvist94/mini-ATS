package com.miniats.service;

import com.miniats.domain.model.Candidate;
import com.miniats.dto.CandidateDTO;
import com.miniats.repository.CandidateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

/**
 * Service for Candidate entity operations.
 * Handles business logic for candidate management including LinkedIn URL processing.
 */
@Service
public class CandidateService {

    private static final Logger logger = LoggerFactory.getLogger(CandidateService.class);

    private final CandidateRepository candidateRepository;
    private final OrganizationService organizationService;
    private final UserService userService;
    private final ActivityService activityService;

    public CandidateService(
            CandidateRepository candidateRepository,
            OrganizationService organizationService,
            UserService userService,
            ActivityService activityService
    ) {
        this.candidateRepository = candidateRepository;
        this.organizationService = organizationService;
        this.userService = userService;
        this.activityService = activityService;
    }

    /**
     * Get candidate by ID
     */
    public CandidateDTO getCandidateById(UUID id) {
        logger.debug("Fetching candidate with ID: {}", id);

        return candidateRepository.findById(id)
                .map(CandidateDTO::fromEntity)
                .orElseThrow(() -> new RuntimeException("Candidate not found with ID: " + id));
    }

    /**
     * Get all candidates for an organization
     */
    public List<CandidateDTO> getCandidatesByOrganization(UUID organizationId) {
        logger.debug("Fetching candidates for organization: {}", organizationId);

        return candidateRepository.findByOrganizationId(organizationId).stream()
                .map(CandidateDTO::fromEntity)
                .toList();
    }

    /**
     * Search candidates by name within organization
     */
    public List<CandidateDTO> searchCandidatesByName(UUID organizationId, String nameKeyword) {
        logger.debug("Searching candidates in organization: {} with keyword: {}",
                organizationId, nameKeyword);

        return candidateRepository
                .findByOrganizationIdAndFullNameContaining(organizationId, nameKeyword)
                .stream()
                .map(CandidateDTO::fromEntity)
                .toList();
    }

    /**
     * Get candidate by email within organization
     */
    public CandidateDTO getCandidateByEmail(UUID organizationId, String email) {
        logger.debug("Fetching candidate with email: {} in organization: {}", email, organizationId);

        return candidateRepository.findByOrganizationIdAndEmail(organizationId, email)
                .map(CandidateDTO::fromEntity)
                .orElseThrow(() -> new RuntimeException(
                        "Candidate not found with email: " + email + " in organization: " + organizationId));
    }

    /**
     * Search candidates by LinkedIn URL within organization
     */
    public List<CandidateDTO> searchCandidatesByLinkedIn(UUID organizationId, String linkedinKeyword) {
        logger.debug("Searching candidates in organization: {} by LinkedIn keyword: {}",
                organizationId, linkedinKeyword);

        return candidateRepository
                .findByOrganizationIdAndLinkedinUrlContaining(organizationId, linkedinKeyword)
                .stream()
                .map(CandidateDTO::fromEntity)
                .toList();
    }

    /**
     * Get all candidates (admin only)
     */
    public List<CandidateDTO> getAllCandidates() {
        logger.debug("Fetching all candidates");

        return candidateRepository.findAll().stream()
                .map(CandidateDTO::fromEntity)
                .toList();
    }

    /**
     * Create new candidate
     */
    public CandidateDTO createCandidate(CandidateDTO candidateDTO) {
        logger.info("Creating new candidate: {} for organization: {}",
                candidateDTO.fullName(), candidateDTO.organizationId());

        // Validate organization exists
        if (!organizationService.organizationExists(candidateDTO.organizationId())) {
            throw new RuntimeException("Organization not found with ID: " + candidateDTO.organizationId());
        }

        // Validate email uniqueness within organization if email provided
        if (candidateDTO.email() != null &&
                candidateRepository.existsByOrganizationIdAndEmail(
                        candidateDTO.organizationId(),
                        candidateDTO.email()
                )) {
            throw new RuntimeException(
                    "Candidate with email '" + candidateDTO.email() +
                            "' already exists in this organization"
            );
        }

        // Validate LinkedIn URL format if provided
        if (candidateDTO.linkedinUrl() != null) {
            validateLinkedInUrl(candidateDTO.linkedinUrl());
        }

        // Create entity
        Candidate candidate = Candidate.builder()
                .organizationId(candidateDTO.organizationId())
                .fullName(candidateDTO.fullName())
                .email(candidateDTO.email())
                .phone(candidateDTO.phone())
                .linkedinUrl(candidateDTO.linkedinUrl())
                .resumeUrl(candidateDTO.resumeUrl())
                .notes(candidateDTO.notes())
                .city(candidateDTO.city())
                .availability(candidateDTO.availability())
                .educationLevel(candidateDTO.educationLevel())
                .isExperienced(candidateDTO.isExperienced())
                .skills(candidateDTO.skills())
                .avatarUrl(candidateDTO.avatarUrl())
                .summary(candidateDTO.summary())
                .build();

        // Save and return
        Candidate saved = candidateRepository.save(candidate);
        logger.info("Candidate created with ID: {}", saved.getId());

        return CandidateDTO.fromEntity(saved);
    }

    /**
     * Update candidate
     */
    public CandidateDTO updateCandidate(UUID id, CandidateDTO candidateDTO) {
        logger.info("Updating candidate with ID: {}", id);

        // Check if candidate exists
        Candidate existing = candidateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Candidate not found with ID: " + id));

        // Verify organization match (cannot move candidate to different organization)
        if (!existing.getOrganizationId().equals(candidateDTO.organizationId())) {
            throw new RuntimeException("Cannot change candidate organization");
        }

        // Check email uniqueness if changing email
        if (candidateDTO.email() != null &&
                !candidateDTO.email().equals(existing.getEmail()) &&
                candidateRepository.existsByOrganizationIdAndEmail(
                        candidateDTO.organizationId(),
                        candidateDTO.email()
                )) {
            throw new RuntimeException(
                    "Candidate with email '" + candidateDTO.email() +
                            "' already exists in this organization"
            );
        }

        // Validate LinkedIn URL format if provided
        if (candidateDTO.linkedinUrl() != null) {
            validateLinkedInUrl(candidateDTO.linkedinUrl());
        }

        // Update entity
        Candidate updated = existing.toBuilder()
                .fullName(candidateDTO.fullName())
                .email(candidateDTO.email())
                .phone(candidateDTO.phone())
                .linkedinUrl(candidateDTO.linkedinUrl())
                .resumeUrl(candidateDTO.resumeUrl())
                .notes(candidateDTO.notes())
                .city(candidateDTO.city())
                .availability(candidateDTO.availability())
                .educationLevel(candidateDTO.educationLevel())
                .isExperienced(candidateDTO.isExperienced())
                .skills(candidateDTO.skills())
                .avatarUrl(candidateDTO.avatarUrl())
                .summary(candidateDTO.summary())
                .build();

        // Save and return
        Candidate saved = candidateRepository.update(updated);
        logger.info("Candidate updated: {}", saved.getId());

        // Log activity if notes were changed
        if (candidateDTO.notes() != null &&
                !candidateDTO.notes().equals(existing.getNotes())) {
            activityService.logActivity(
                    saved.getOrganizationId(),
                    saved.getId(),
                    "note_added",
                    "Internal notes updated"
            );
            logger.debug("Activity logged for notes update");
        }

        return CandidateDTO.fromEntity(saved);
    }

    /**
     * Delete candidate
     */
    public void deleteCandidate(UUID id) {
        logger.info("Deleting candidate with ID: {}", id);

        if (!candidateRepository.existsById(id)) {
            throw new RuntimeException("Candidate not found with ID: " + id);
        }

        candidateRepository.deleteById(id);
        logger.info("Candidate deleted: {}", id);
    }

    /**
     * Get candidate count for organization
     */
    public long getCandidateCount(UUID organizationId) {
        return candidateRepository.countByOrganizationId(organizationId);
    }

    /**
     * Verify user has access to candidate (through organization)
     */
    public boolean userHasAccessToCandidate(String userEmail, UUID candidateId) {
        Candidate candidate = candidateRepository.findById(candidateId)
                .orElseThrow(() -> new RuntimeException("Candidate not found"));

        return userService.hasAccessToOrganization(userEmail, candidate.getOrganizationId());
    }

    /**
     * Validate LinkedIn URL format
     */
    private void validateLinkedInUrl(String linkedinUrl) {
        if (linkedinUrl == null || linkedinUrl.trim().isEmpty()) {
            return;
        }

        String normalized = linkedinUrl.toLowerCase().trim();

        if (!normalized.startsWith("http://") && !normalized.startsWith("https://")) {
            throw new RuntimeException("LinkedIn URL must start with http:// or https://");
        }

        if (!normalized.contains("linkedin.com")) {
            throw new RuntimeException("Invalid LinkedIn URL format");
        }

        logger.debug("LinkedIn URL validated: {}", linkedinUrl);
    }
}