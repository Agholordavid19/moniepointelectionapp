package org.mackson.repository;

import org.mackson.model.entity.Contestant;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContestantRepository extends MongoRepository<Contestant, String> {
    Optional<Contestant> findByEmail(String email);
}
