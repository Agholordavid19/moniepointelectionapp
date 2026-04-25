package org.mackson.service.implementations;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.mackson.exceptions.*;
import org.mackson.model.dtos.citizendto.updatecitizen.CitizenRequestValidated;
import org.mackson.model.dtos.citizendto.citizenvotingdetails.VotingIdDetails;
import org.mackson.model.dtos.citizendto.login.CitizenLoginRequest;
import org.mackson.model.dtos.citizendto.login.CitizenLoginResponse;
import org.mackson.model.dtos.citizendto.signup.CitizenRequest;
import org.mackson.model.dtos.citizendto.signup.CitizenResponse;
import org.mackson.model.dtos.citizendto.updatecitizen.CitizenUpdateRequest;
import org.mackson.model.dtos.citizendto.updatecitizen.CitizenUpdateValidated;
import org.mackson.model.dtos.citizendto.updatecitizen.CitizenUpdatedResponse;
import org.mackson.model.entity.Citizen;
import org.mackson.repository.CitizenRepository;
import org.mackson.service.utils.CitizenInputValidator;
import org.mackson.service.utils.MapperTool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CitizenService {
    private final CitizenInputValidator citizenInputValidator;
    private final CitizenRepository citizenRepository;
    private final MapperTool mapper;

    public CitizenResponse createCitizenThenSave(CitizenRequest citizenRequest) {
        CitizenRequestValidated validated = citizenInputValidator.checkAllInputandValidate(citizenRequest);
        if(citizenRepository.existsByEmail(validated.getEmail())) throw new EmailExistsAlreadyException("This Email Exists Already " + validated.getEmail());
        if (citizenRepository.existsByPhoneNumber(validated.getPhoneNumber())) throw new PhoneNumberException("Phone Number Exists Already");
        Citizen transformedCitizen = mapper.mapToCitizen(validated);
        Citizen savedCitizen = citizenRepository.save(transformedCitizen);
        return mapper.mapToCitizenResponse(savedCitizen);
    }

    public CitizenLoginResponse logUserWithEmailAndPassword(CitizenLoginRequest citizenLoginRequest) {
        Citizen foundCitizen = citizenRepository.findByEmailOrPhoneNumber(citizenLoginRequest.getEmail()).orElseThrow(() -> new InvalidEmailException("Invalid user"));
        if (!foundCitizen.getPassword().equals(citizenLoginRequest.getPassword())) throw new InvalidInputException("wrong password");
        foundCitizen.setLoggedIn(true);
        citizenRepository.save(foundCitizen);
        return CitizenLoginResponse.builder()
                .email(citizenLoginRequest.getEmail())
                .isLoggedIn(true)
                .build();
    }

    public CitizenUpdatedResponse updateCitizenDetails(CitizenUpdateRequest citizenRequest, String email) {
        Citizen foundCitizen =verifyCitizenLogStatus(email);
        CitizenUpdateValidated validatedUpdateInput = citizenInputValidator.validateUpdateInput(citizenRequest);
        Instant timeNow = Instant.now();
        foundCitizen.setName(validatedUpdateInput.getName());
        foundCitizen.setUpdatedAt(timeNow);
        foundCitizen.setYearOfBirth(validatedUpdateInput.getYear());
        foundCitizen.setLocalGovernmentArea(validatedUpdateInput.getLocalGovernment());
        foundCitizen.setPhoneNumber(validatedUpdateInput.getPhoneNumber());
        Citizen savedCitizen = citizenRepository.save(foundCitizen);
        return CitizenUpdatedResponse.builder()
                .year(savedCitizen.getYearOfBirth())
                .name(savedCitizen.getName())
                .localGovernment(savedCitizen.getLocalGovernmentArea().name())
                .phoneNumber(savedCitizen.getPhoneNumber())
                .votingId(savedCitizen.getVotingId())
                .email(savedCitizen.getEmail())
                .build();
    }

    public VotingIdDetails validateUserThenGenerateVotingId(String email) {
        Citizen foundCitizen =verifyCitizenLogStatus(email);
        if (foundCitizen.getVotingId() != null) return VotingIdDetails.builder()
                .votingId(foundCitizen.getVotingId())
                .createdAt(foundCitizen.getVoterIdRegisteredAt())
                .expiresAt(foundCitizen.getVoterIdExpiringAt())
                .build();
        Citizen savedCitizen = updateVotingIdAndSave(foundCitizen);
        return VotingIdDetails.builder()
               .createdAt(savedCitizen.getVoterIdRegisteredAt())
               .expiresAt(savedCitizen.getVoterIdExpiringAt())
               .votingId(savedCitizen.getVotingId())
               .build();
    }

    private @NonNull Citizen updateVotingIdAndSave(Citizen foundCitizen) {
        String votingId = verifyAgeThenGenerteVotingID(foundCitizen);
        foundCitizen.setVotingId(votingId);
        Instant presentTime = Instant.now();
        foundCitizen.setVoterIdRegisteredAt(presentTime);
        foundCitizen.setVoterIdExpiringAt(presentTime.plusSeconds(92_275_200L));
        Citizen savedCitizen = citizenRepository.save(foundCitizen);
        return savedCitizen;
    }

    private String verifyAgeThenGenerteVotingID(Citizen foundCitizen) {
        int presentYear = getCitizenAge(foundCitizen);
        boolean value = true;
        String votingId = "";
        do {
            UUID generatedId = UUID.randomUUID();
            votingId = String.valueOf(presentYear) + generatedId.toString().substring(5, 11);
            if (!citizenRepository.existsByVotingId(votingId)) value = false;
        }while (value);
        return votingId;
    }

    public int getCitizenAge(Citizen foundCitizen) {
        int year = LocalDate.now().getYear();
        int minimumAge = 18;
        int citizenAge = year - foundCitizen.getYearOfBirth() ;
        if (citizenAge < minimumAge) throw new VotingException("Not Of Age To Vote");
        return year;
    }

    public Citizen verifyCitizenLogStatus(String email) {
        Citizen foundCitizen = citizenRepository.findByEmailOrPhoneNumber(email).orElseThrow(() -> new InvalidEmailException("Email does not exists"));
        if (!foundCitizen.isLoggedIn()) throw new NotAuthenticatedActionException("You are not logged in");
        return foundCitizen;
    }

    public void logCitizenOut(String email) {
        Citizen foundCitizen = verifyCitizenLogStatus(email);
        foundCitizen.setLoggedIn(false);
        citizenRepository.save(foundCitizen);
    }
}
