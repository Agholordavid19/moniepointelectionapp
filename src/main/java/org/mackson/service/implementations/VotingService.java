import org.mackson.exceptions.InvalidEmailException;
import org.mackson.exceptions.InvalidInputException;
import org.mackson.model.entity.Contestant;


public
@Override
@Transactional
public VoteResponse castVote(VoteRequest voteRequest, String citizenEmail) {
    // Validate request
    if (voteRequest == null) {
        throw new InvalidInputException("Vote request cannot be null");
    }

    Citizen citizen = citizenService.verifyCitizenLogStatus(citizenEmail);
    voterEligibility(citizen);

    if (citizen.isHasVoted()) {
        throw new VotingException("You have already cast your vote");
    }

    // Resolve each contestant by their email
    Contestant governor = resolveContestant(voteRequest.getGovernorEmail(), ElectionPosition.GOVERNOR);
    Contestant deputyGovernor = resolveContestant(voteRequest.getDeputyGovernorEmail(), ElectionPosition.DEPUTY_GOVERNOR);
    Contestant speaker = resolveContestant(voteRequest.getSpeakerEmail(), ElectionPosition.SPEAKER);
    Contestant deputySpeaker = resolveContestant(voteRequest.getDeputySpeakerEmail(), ElectionPosition.DEPUTY_SPEAKER);

    // Increment vote counts
    List<Contestant> voted = List.of(governor, deputyGovernor, speaker, deputySpeaker);
    voted.forEach(c -> c.setNumberOfVotes(c.getNumberOfVotes() + 1));
    contestantRepository.saveAll(voted);

    // Mark citizen as having voted
    citizen.setHasVoted(true);
    citizenRepository.save(citizen);

    return VoteResponse.builder()
            .message("Your votes have been cast successfully")
            .citizenEmail(citizenEmail)
            .hasVoted(true)
            .build();
}

private Contestant resolveContestant(String email, ElectionPosition expectedPosition) {
    if (email == null || email.trim().isEmpty()) {
        throw new InvalidInputException(
                expectedPosition.name() + " contestant email cannot be null or empty");
    }

    Contestant contestant = contestantRepository
            .findByEmail(email.trim())
            .orElseThrow(() -> new InvalidEmailException(
                    "No contestant found with email: " + email));

    if (contestant.getPosition() != expectedPosition) {
        throw new InvalidInputException(
                "Contestant with email " + email
                        + " is not running for " + expectedPosition.name());
    }

    return contestant;
}