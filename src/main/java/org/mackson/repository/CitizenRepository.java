package org.mackson.repository;

import org.mackson.model.entity.Citizen;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CitizenRepository extends MongoRepository<Citizen, String> {
    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    Optional<Citizen> findByEmailOrPhoneNumber(String phoneNumber);

    boolean existsByVotingId(String votingId);
}

