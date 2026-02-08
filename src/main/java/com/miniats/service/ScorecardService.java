package com.miniats.service;

import com.miniats.domain.model.Scorecard;
import com.miniats.dto.ScorecardDTO;
import com.miniats.exception.ResourceNotFoundException;
import com.miniats.repository.ScorecardRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/**
 * Service for managing scorecards.
 */
@Service
public class ScorecardService {

    private final ScorecardRepository scorecardRepository;
    private final ActivityService activityService;

    public ScorecardService(
            ScorecardRepository scorecardRepository,
            ActivityService activityService
    ) {
        this.scorecardRepository = scorecardRepository;
        this.activityService = activityService;
    }

    /**
     * Get scorecard for a candidate
     */
    public Optional<ScorecardDTO> getScorecardByCandidate(UUID candidateId) {
        return scorecardRepository.findByCandidateId(candidateId)
                .map(ScorecardDTO::fromEntity);
    }

    /**
     * Create or update scorecard for a candidate
     */
    public ScorecardDTO createOrUpdateScorecard(ScorecardDTO scorecardDTO) {
        boolean exists = scorecardRepository.existsByCandidateId(scorecardDTO.candidateId());

        Scorecard scorecard;
        if (exists) {
            // Update existing
            scorecard = Scorecard.builder()
                    .candidateId(scorecardDTO.candidateId())
                    .organizationId(scorecardDTO.organizationId())
                    .technicalSkills(scorecardDTO.technicalSkills())
                    .communication(scorecardDTO.communication())
                    .culturalFit(scorecardDTO.culturalFit())
                    .experienceLevel(scorecardDTO.experienceLevel())
                    .problemSolving(scorecardDTO.problemSolving())
                    //.evaluatedBy(scorecardDTO.evaluatedBy())
                    .notes(scorecardDTO.notes())
                    .updatedAt(Instant.now())
                    .build();

            scorecard = scorecardRepository.update(scorecard);

            // Log activity
            activityService.logActivity(
                    scorecardDTO.organizationId(),
                    scorecardDTO.candidateId(),
                    "scorecard_updated",
                    "Scorecard updated (Overall: " + scorecard.getOverallScore() + "/5.0)"
            );
        } else {
            // Create new
            scorecard = Scorecard.builder()
                    .candidateId(scorecardDTO.candidateId())
                    .organizationId(scorecardDTO.organizationId())
                    .technicalSkills(scorecardDTO.technicalSkills())
                    .communication(scorecardDTO.communication())
                    .culturalFit(scorecardDTO.culturalFit())
                    .experienceLevel(scorecardDTO.experienceLevel())
                    .problemSolving(scorecardDTO.problemSolving())
                    //.evaluatedBy(scorecardDTO.evaluatedBy())
                    .notes(scorecardDTO.notes())
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .build();

            scorecard = scorecardRepository.create(scorecard);

            // Log activity
            activityService.logActivity(
                    scorecardDTO.organizationId(),
                    scorecardDTO.candidateId(),
                    "scorecard_created",
                    "Scorecard created (Overall: " + scorecard.getOverallScore() + "/5.0)"
            );
        }

        return ScorecardDTO.fromEntity(scorecard);
    }

    /**
     * Delete scorecard for a candidate
     */
    public void deleteScorecard(UUID candidateId) {
        if (!scorecardRepository.existsByCandidateId(candidateId)) {
            throw new ResourceNotFoundException("Scorecard not found for candidate: " + candidateId);
        }
        scorecardRepository.deleteByCandidateId(candidateId);
    }
}