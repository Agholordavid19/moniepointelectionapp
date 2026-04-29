package org.mackson.service.utils;

import org.mackson.model.data.LocalGovernmentArea;
import org.mackson.model.dtos.citizendto.updatecitizen.CitizenRequestValidated;
import org.mackson.model.dtos.contestant.ContestantResponse;
import org.mackson.model.dtos.contestant.ContestantValidated;
import org.mackson.model.dtos.citizendto.login.CitizenLoginRequest;
import org.mackson.model.dtos.citizendto.signup.CitizenResponse;
import org.mackson.model.entity.Citizen;
import org.mackson.model.entity.Contestant;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class MapperTool {
    public CitizenRequestValidated mapToValidated(String email, String name, int year, String phoneNumber,String password, LocalGovernmentArea localGovernment) {
        CitizenRequestValidated validated = new CitizenRequestValidated();
        validated.setEmail(email);
        validated.setName(name);
        validated.setYear(year);
        validated.setPhoneNumber(phoneNumber);
        validated.setLocalGovernmentArea(localGovernment);
        validated.setPassword(password);
        return validated;
    }

    public Citizen mapToCitizen(CitizenRequestValidated validated) {
        Instant now = Instant.now();
        Citizen convertToCitizen = new Citizen();
        convertToCitizen.setCitizen(true);
        convertToCitizen.setPassword(validated.getPassword());
        convertToCitizen.setLocalGovernmentArea(validated.getLocalGovernmentArea());
        convertToCitizen.setPhoneNumber(validated.getPhoneNumber());
        convertToCitizen.setEmail(validated.getEmail());
        convertToCitizen.setName(validated.getName());
        convertToCitizen.setYearOfBirth(validated.getYear());
        convertToCitizen.setHasVoted(false);
        convertToCitizen.setLoggedIn(false);
        convertToCitizen.setVotingId(null);
        convertToCitizen.setCreatedAt(now);
        convertToCitizen.setUpdatedAt(now);
        convertToCitizen.setVoterIdRegisteredAt(null);
        convertToCitizen.setVoterIdExpiringAt(null);
        return convertToCitizen;
    }

    public CitizenResponse mapToCitizenResponse(Citizen savedCitizen) {
      return CitizenResponse.builder()
              .email(savedCitizen.getEmail())
              .status("user has been saved")
              .build();
    }

    public CitizenLoginRequest mapToLogInRequest(String email, String password) {
        return CitizenLoginRequest.builder()
                .email(email)
                .password(password)
                .build();
    }

    public Contestant mapToContestant(ContestantValidated contestantValidated, String email, String name) {
        Contestant newContestant = new Contestant();
        newContestant.setName(name);
        newContestant.setContestant(true);
        newContestant.setParty(contestantValidated.getParty());
        newContestant.setPosition(contestantValidated.getPosition());
        newContestant.setNumberOfVotes(0);
        newContestant.setEmail(email);
        newContestant.setReisteredAt(Instant.now());
        return newContestant;
    }

    public Contestant updateContestant(ContestantValidated contestantValidated, Contestant foundContestant, String name) {
        foundContestant.setParty(contestantValidated.getParty());
        foundContestant.setPosition(contestantValidated.getPosition());
        foundContestant.setUpdatedAt(Instant.now());
        foundContestant.setName(name);
        return foundContestant;
    }
    public List<ContestantResponse> mapToListContestantPositions(List<Contestant> contestantsPositions) {
        return contestantsPositions.stream()
                .map(this::mapToContestantResponse)
                .toList();
    }
    private ContestantResponse mapToContestantResponse(Contestant contestant) {
        return ContestantResponse.builder()
                .name(contestant.getName())
                .party(contestant.getParty().name())
                .reisteredAt(contestant.getReisteredAt())
                .position(contestant.getPosition().name())
                .build();
    }
}
