package org.mackson.service.implementations;

import lombok.RequiredArgsConstructor;
import org.mackson.exceptions.InvalidEmailException;
import org.mackson.exceptions.InvalidInputException;
import org.mackson.exceptions.VotingException;
import org.mackson.model.data.ElectionPosition;
import org.mackson.model.dtos.VoteRequest;
import org.mackson.model.dtos.VoteResponse;
import org.mackson.model.dtos.WinnerResponse;
import org.mackson.model.entity.Citizen;
import org.mackson.model.entity.Contestant;
import org.mackson.repository.CitizenRepository;
import org.mackson.repository.ContestantRepository;
import org.mackson.service.VotingServiceInterface;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VotingService implements VotingServiceInterface {

    private final CitizenService citizenService;
    private final ContestantRepository contestantRepository;
    private final CitizenRepository citizenRepository;

    @Override
    @Transactional
    public VoteResponse castVote(VoteRequest voteRequest, String citizenEmail) {
        if (voteRequest == null) {
            throw new InvalidInputException("Vote request cannot be null");
        }

        Citizen citizen = citizenService.verifyCitizenLogStatus(citizenEmail);
        voterEligibility(citizen);

        if (citizen.isHasVoted()) {
            throw new VotingException("You have already cast your vote");
        }

        Contestant governor = resolveContestant(voteRequest.getGovernorEmail(), ElectionPosition.GOVERNOR);
        Contestant deputyGovernor = resolveContestant(voteRequest.getDeputyGovernorEmail(), ElectionPosition.DEPUTY_GOVERNOR);
        Contestant speaker = resolveContestant(voteRequest.getSpeakerEmail(), ElectionPosition.SPEAKER);
        Contestant deputySpeaker = resolveContestant(voteRequest.getDeputySpeakerEmail(), ElectionPosition.DEPUTY_SPEAKER);

        List<Contestant> voted = List.of(governor, deputyGovernor, speaker, deputySpeaker);
        voted.forEach(c -> c.setNumberOfVotes(c.getNumberOfVotes() + 1));
        contestantRepository.saveAll(voted);

        citizen.setHasVoted(true);
        citizenRepository.save(citizen);

        return VoteResponse.builder()
                .message("Your votes have been cast successfully")
                .citizenEmail(citizenEmail)
                .hasVoted(true)
                .build();
    }

    private void voterEligibility(Citizen citizen) {
        if (citizen.getVotingId() == null) {
            throw new VotingException("You do not have a valid voter ID. Please register your voter ID first.");
        }
        if (citizen.getVoterIdExpiringAt() != null && Instant.now().isAfter(citizen.getVoterIdExpiringAt())) {
            throw new VotingException("Your voter ID has expired. Please renew your voter ID.");
        }
    }

    private Contestant resolveContestant(String email, ElectionPosition expectedPosition) {
        if (email == null || email.trim().isEmpty()) {
            throw new InvalidInputException(expectedPosition.name() + " contestant email cannot be null or empty");
        }
        Contestant contestant = contestantRepository
                .findByEmail(email.trim())
                .orElseThrow(() -> new InvalidEmailException("No contestant found with email: " + email));
        if (contestant.getPosition() != expectedPosition) {
            throw new InvalidInputException(
                    "Contestant with email " + email + " is not running for " + expectedPosition.name());
        }
        return contestant;
    }

    @Override
    public List<WinnerResponse> getWinner() {
        return Arrays.stream(ElectionPosition.values())
                .map(position -> contestantRepository
                        .findTopByPositionOrderByNumberOfVotesDesc(position)
                        .map(c -> WinnerResponse.builder()
                                .position(position.name())
                                .name(c.getName())
                                .party(c.getParty().name())
                                .votes(c.getNumberOfVotes())
                                .build())
                        .orElse(WinnerResponse.builder()
                                .position(position.name())
                                .name("No contestant")
                                .party("N/A")
                                .votes(0)
                                .build()))
                .toList();
    }
}