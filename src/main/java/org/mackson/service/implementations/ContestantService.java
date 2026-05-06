package org.mackson.service.implementations;

import lombok.RequiredArgsConstructor;
import org.mackson.exceptions.ContestantExistAlreadyException;
import org.mackson.exceptions.InvalidEmailException;
import org.mackson.exceptions.InvalidInputException;
import org.mackson.model.data.ElectionPosition;
import org.mackson.model.dtos.WinnerResponse;
import org.mackson.model.dtos.contestant.ContestantResponse;
import org.mackson.model.dtos.contestant.ContestantValidated;
import org.mackson.model.dtos.contestant.ContestantRequest;
import org.mackson.model.dtos.contestant.ContestantVoterResponse;
import org.mackson.model.entity.Citizen;
import org.mackson.model.entity.Contestant;
import org.mackson.repository.ContestantRepository;
import org.mackson.service.ContestantServiceInterface;
import org.mackson.service.utils.ContestantValidator;
import org.mackson.service.utils.MapperTool;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
public class ContestantService implements ContestantServiceInterface {
    private final CitizenService citizenService;
    private final ContestantRepository contestantRepository;
    private final ContestantValidator contestantValidator;
    private final MapperTool mapper;

    public ContestantResponse resgisterContestant(ContestantRequest newContestant, String email) {
       Citizen foundCitizen = citizenService.verifyCitizenLogStatus(email);
       if(contestantRepository.existsByEmail(email)) throw new ContestantExistAlreadyException("this contestant with this email exists already ");
       citizenService.getCitizenAge(foundCitizen);
       ContestantValidated contestantValidated = contestantValidator.validateContesantInput(newContestant);
       Contestant transformedContestant = mapper.mapToContestant(contestantValidated, email, foundCitizen.getName());
       Contestant savedContestant =contestantRepository.save(transformedContestant);
       return ContestantResponse.builder()
               .email(savedContestant.getEmail())
               .name(foundCitizen.getName())
               .party(savedContestant.getParty().name())
               .reisteredAt(savedContestant.getReisteredAt())
               .position(savedContestant.getPosition().name())
               .build();
    }

    public ContestantResponse updateContestant(String email, ContestantRequest updateRequest) {
       Citizen foundCitizen = citizenService.verifyCitizenLogStatus(email);
        Contestant foundContestant = contestantRepository.findByEmail(email).orElseThrow(() -> new  InvalidEmailException("No contestant with such email"));
        ContestantValidated contestantValidated = contestantValidator.validateContesantInput(updateRequest);
        Contestant savedContestant =contestantRepository.save(mapper.updateContestant(contestantValidated, foundContestant, foundContestant.getName()));
        return ContestantResponse.builder()
                .email(savedContestant.getEmail())
                .name(foundCitizen.getName())
                .party(savedContestant.getParty().name())
                .reisteredAt(savedContestant.getReisteredAt())
                .position(savedContestant.getPosition().name())
                .build();
    }

    public ContestantVoterResponse getContestantVotersNumber(String email){
        citizenService.verifyCitizenLogStatus(email);
        Contestant foundContestant = contestantRepository.findByEmail(email).orElseThrow(() -> new  InvalidEmailException("No contestant with such email"));
        return ContestantVoterResponse.builder()
                .voters(foundContestant.getNumberOfVotes())
                .post(foundContestant.getPosition().name())
                .build();

    }

    public void deleteContestant(String email){
        citizenService.verifyCitizenLogStatus(email);
        Contestant foundContestant = contestantRepository.findByEmail(email).orElseThrow(()->new InvalidEmailException("contestant email doesnt exists"));
        contestantRepository.delete(foundContestant);
    }

    @Override
    public List<ContestantResponse> getContestantsBasedOnPosition(String position) {
        List<Contestant> contestantsPositions = contestantRepository.findByPosition(parsePosition(position)).orElseThrow(() -> new InvalidInputException("Not Found"));
        return mapper.mapToListContestantPositions(contestantsPositions);
    }


    public ContestantResponse getContestantByEmail(String email) {
            Contestant found = contestantRepository.findByEmailIgnoreCase(email)
                    .orElseThrow(() -> new InvalidEmailException("Not a contestant"));
            return ContestantResponse.builder()
                    .email(found.getEmail())
                    .name(found.getName())
                    .party(found.getParty().name())
                    .position(found.getPosition().name())
                    .reisteredAt(found.getReisteredAt())
                    .build();
    }

    @Override
    public List<WinnerResponse> getWinners() {
        return Arrays.stream(ElectionPosition.values())
                .map(position -> contestantRepository
                        .findTopByPositionOrderByNumberOfVotesDesc(position)
                        .map(c -> WinnerResponse.builder()
                                .position(position.name())
                                .name(c.getName())
                                .party(c.getParty().name())
                                .votes(c.getNumberOfVotes())
                                .build())
                        .orElse(null))
                .filter(Objects::nonNull)
                .toList();
    }

    private ElectionPosition parsePosition(String position) {
        try {
            return ElectionPosition.valueOf(position.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Invalid election position: " + position);
        }
    }
}
