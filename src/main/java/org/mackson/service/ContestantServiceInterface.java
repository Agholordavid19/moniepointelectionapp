package org.mackson.service;

import org.mackson.model.data.ElectionPosition;
import org.mackson.model.dtos.WinnerResponse;
import org.mackson.model.dtos.contestant.ContestantRequest;
import org.mackson.model.dtos.contestant.ContestantResponse;
import org.mackson.model.dtos.contestant.ContestantVoterResponse;
import org.mackson.model.entity.Contestant;

import java.util.List;
import java.util.Optional;

public interface ContestantServiceInterface {
    ContestantResponse resgisterContestant(ContestantRequest newContestant, String email);
    ContestantResponse updateContestant(String email, ContestantRequest updateRequest);
    ContestantVoterResponse getContestantVotersNumber(String email);
    void deleteContestant(String email);
    List<ContestantResponse > getContestantsBasedOnPosition(String position);
    ContestantResponse getContestantByEmail(String email);
    List<WinnerResponse> getWinners();
}
