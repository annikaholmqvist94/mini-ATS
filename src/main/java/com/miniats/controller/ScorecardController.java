package com.miniats.controller;

import com.miniats.dto.ScorecardDTO;
import com.miniats.service.ScorecardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

/**
 * REST Controller for Scorecard endpoints.
 */
@RestController
@RequestMapping("/scorecards")
public class ScorecardController extends BaseController {

    private final ScorecardService scorecardService;

    public ScorecardController(ScorecardService scorecardService) {
        this.scorecardService = scorecardService;
    }

    /**
     * Get scorecard for a candidate
     * GET /api/scorecards/candidate/{candidateId}
     */
    @GetMapping("/candidate/{candidateId}")
    public ResponseEntity<ApiResponse<ScorecardDTO>> getScorecardByCandidate(
            @PathVariable UUID candidateId
    ) {
        Optional<ScorecardDTO> scorecard = scorecardService.getScorecardByCandidate(candidateId);
        if (scorecard.isPresent()) {
            return success(scorecard.get());
        } else {
            // Return success with null data when scorecard doesn't exist
            return ResponseEntity.ok(new ApiResponse<>(true, null, null, java.time.Instant.now()));
        }
    }

    /**
     * Create or update scorecard for a candidate
     * POST /api/scorecards
     */
    @PostMapping
    public ResponseEntity<ApiResponse<ScorecardDTO>> createOrUpdateScorecard(
            @RequestBody ScorecardDTO scorecardDTO
    ) {
        ScorecardDTO saved = scorecardService.createOrUpdateScorecard(scorecardDTO);
        return success(saved);
    }

    /**
     * Delete scorecard for a candidate
     * DELETE /api/scorecards/candidate/{candidateId}
     */
    @DeleteMapping("/candidate/{candidateId}")
    public ResponseEntity<ApiResponse<Void>> deleteScorecard(
            @PathVariable UUID candidateId
    ) {
        scorecardService.deleteScorecard(candidateId);
        return ResponseEntity.ok(new ApiResponse<>(true, null, "Scorecard deleted successfully", java.time.Instant.now()));
    }
}