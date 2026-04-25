package org.mackson.service;

import org.mackson.model.dtos.contestant.ContestantRequest;
import org.mackson.model.dtos.contestant.ContestantResponse;
import org.mackson.model.dtos.contestant.ContestantVoterResponse;

public interface ContestantServiceInterface {
    ContestantResponse resgisterContestant(ContestantRequest newContestant, String email);
    ContestantResponse updateContestant(String email, ContestantRequest updateRequest);
    ContestantVoterResponse getContestantVotersNumber(String email);
    void deleteContestant(String email);
}
