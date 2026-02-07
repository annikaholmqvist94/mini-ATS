package com.miniats.repository;

import com.miniats.domain.model.Scorecard;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for Scorecard entity.
 */
public interface ScorecardRepository {

    /**
     * Create a new scorecard
     */
    Scorecard create(Scorecard scorecard);

    /**
     * Update an existing scorecard
     */
    Scorecard update(Scorecard scorecard);

    /**
     * Find scorecard by candidate ID
     */
    Optional<Scorecard> findByCandidateId(UUID candidateId);

    /**
     * Delete scorecard by candidate ID
     */
    void deleteByCandidateId(UUID candidateId);

    /**
     * Check if scorecard exists for candidate
     */
    boolean existsByCandidateId(UUID candidateId);
}