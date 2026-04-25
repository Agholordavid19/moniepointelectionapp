package org.mackson.service.implementations;

import lombok.RequiredArgsConstructor;
import org.mackson.exceptions.InvalidEmailException;
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

@Service
@RequiredArgsConstructor
public class ContestantService implements ContestantServiceInterface {
    private final CitizenService citizenService;
    private final ContestantRepository contestantRepository;
    private final ContestantValidator contestantValidator;
    private final MapperTool mapper;

    public ContestantResponse resgisterContestant(ContestantRequest newContestant, String email) {
       Citizen foundCitizen = citizenService.verifyCitizenLogStatus(email);
       citizenService.getCitizenAge(foundCitizen);
       ContestantValidated contestantValidated = contestantValidator.validateContesantInput(newContestant);
       Contestant transformedContestant = mapper.mapToContestant(contestantValidated, email);
       Contestant savedContestant =contestantRepository.save(transformedContestant);
       return ContestantResponse.builder()
               .party(savedContestant.getParty().name())
               .reisteredAt(savedContestant.getReisteredAt())
               .position(savedContestant.getPosition().name())
               .build();
    }

    public ContestantResponse updateContestant(String email, ContestantRequest updateRequest) {
        citizenService.verifyCitizenLogStatus(email);
        Contestant foundContestant = contestantRepository.findByEmail(email).orElseThrow(() -> new  InvalidEmailException("No contestant with such email"));
        ContestantValidated contestantValidated = contestantValidator.validateContesantInput(updateRequest);
        Contestant savedContestant =contestantRepository.save(mapper.updateContestant(contestantValidated, foundContestant));
        return ContestantResponse.builder()
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
}
