package org.mackson.repository;

import org.mackson.model.data.ElectionPosition;
import org.mackson.model.dtos.contestant.ContestantResponse;
import org.mackson.model.entity.Contestant;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContestantRepository extends MongoRepository<Contestant, String> {
    Optional<Contestant> findByEmail(String email);

    Optional<List<Contestant>> findByPosition(ElectionPosition electionPosition);

    Optional<Contestant> findByEmailIgnoreCase(String email);

    boolean existsByEmail(String email);

    Optional<Contestant> findTopByPositionOrderByNumberOfVotesDesc(ElectionPosition position);
}
