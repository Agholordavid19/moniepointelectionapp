package org.mackson.service.utils;

import org.mackson.exceptions.InvalidInputException;
import org.mackson.model.data.ElectionPosition;
import org.mackson.model.data.PolitcalParties;
import org.mackson.model.dtos.contestant.ContestantValidated;
import org.mackson.model.dtos.contestant.ContestantRequest;
import org.springframework.stereotype.Component;

@Component
public class ContestantValidator {
    public ContestantValidated validateContesantInput(ContestantRequest newContestant) {
        PolitcalParties contestantParty = validateContestantParty(newContestant.getParty());
        ElectionPosition post = validateContestantPosition(newContestant.getPosition());
        return ContestantValidated.builder()
                .party(contestantParty)
                .position(post)
                .build();
    }

    private ElectionPosition validateContestantPosition(String position) {
        if (position == null || position.trim().isEmpty()) {
            throw new InvalidInputException("Political position cannot be null or empty");
        }

        try {
            return ElectionPosition.valueOf(position.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Enter a valid political position");
        }
    }

    private PolitcalParties validateContestantParty(String party) {
        if (party == null || party.trim().isEmpty()) {
            throw new InvalidInputException("Party cannot be null or empty");
        }

        try {
            return PolitcalParties.valueOf(party.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Enter a valid Political Party");
        }
    }
}
