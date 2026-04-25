package org.mackson.service.utils;

import lombok.RequiredArgsConstructor;
import org.mackson.exceptions.*;
import org.mackson.model.data.LocalGovernmentArea;
import org.mackson.model.dtos.citizendto.updatecitizen.CitizenRequestValidated;
import org.mackson.model.dtos.citizendto.login.CitizenLoginRequest;
import org.mackson.model.dtos.citizendto.signup.CitizenRequest;
import org.mackson.model.dtos.citizendto.updatecitizen.CitizenUpdateRequest;
import org.mackson.model.dtos.citizendto.updatecitizen.CitizenUpdateValidated;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@RequiredArgsConstructor
@Component
public class CitizenInputValidator {
    private final MapperTool mapper;
    private final int MAXIMUM_AGE = 120;
    public CitizenRequestValidated checkAllInputandValidate(CitizenRequest citizenRequest) {
        String email = validateCitizenEmail(citizenRequest.getEmail());
        String name = validateCitizenName(citizenRequest.getName());
        int year = validateCitizenYear(citizenRequest.getYear());
        String phoneNumber = validateCitizenPhoneNumber(citizenRequest.getPhoneNumber());
        LocalGovernmentArea localGovernment = validateCitizenLocalGovernmentArea(citizenRequest.getLocalGovernmentArea());
        return mapper.mapToValidated(email,name,year,phoneNumber,citizenRequest.getPassword(), localGovernment);
    }

    public CitizenLoginRequest checkLoginFields(CitizenLoginRequest request){
        String email = validateCitizenEmail(request.getEmail());
        String password = request.getPassword();
        return mapper.mapToLogInRequest(email, password);
    }

    private LocalGovernmentArea validateCitizenLocalGovernmentArea(String localGovernmentArea) {

        if (localGovernmentArea == null || localGovernmentArea.trim().isEmpty()) {
            throw new InvalidInputException("Local government cannot be null or empty");
        }

        try {
            return LocalGovernmentArea.valueOf(localGovernmentArea.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new InvalidInputException("Enter a valid Local Government Area");
        }
    }

    private String validateCitizenPhoneNumber(String phoneNumber) {
            if (phoneNumber == null) {
                throw new PhoneNumberException("Phone number cannot be null");
            }
            String trimmed = phoneNumber.trim();
            if (trimmed.length() != 14) {
                throw new PhoneNumberException("Phone number must be exactly 14 characters");
            }
            if (!trimmed.startsWith("+234")) {
                throw new PhoneNumberException("Phone number must start with +234");
            }
            for (int i = 4; i < trimmed.length(); i++) {
                if (!Character.isDigit(trimmed.charAt(i))) {
                    throw new PhoneNumberException("Phone number must not contain letters or symbols");
                }
            }
            return trimmed;
    }

    private int validateCitizenYear(int year) {
        int presentYear = LocalDate.now().getYear();
        int minimumBirthYear = presentYear - MAXIMUM_AGE;
        if (year < minimumBirthYear || year > presentYear)
            throw new InvalidYearOfBirthException(
                    "Year of birth must be between " + minimumBirthYear + " and " + presentYear
            );
        return year;
    }

    private String validateCitizenName(String name) {
        if(name.chars().anyMatch(Character::isDigit)) throw new InvalidNameException("pls enter a valid name");
        return name.trim().toUpperCase();
    }

    private String validateCitizenEmail(String email) {
        if (email == null) throw new InvalidEmailException("Email cannot be null");
        if (!email.contains("@")) throw new InvalidEmailException("Email must contain @");
        if (!email.contains(".")) throw new InvalidEmailException("Email must have a domain name");
        return email.toLowerCase().replace(" ","");
    }

    public CitizenUpdateValidated validateUpdateInput(CitizenUpdateRequest citizenRequest) {
        int year = validateCitizenYear(citizenRequest.getYear());
        LocalGovernmentArea localGovernmentArea = validateCitizenLocalGovernmentArea(citizenRequest.getLocalGovernment());
        String name = validateCitizenName(citizenRequest.getName());
        String phoneNumber = validateCitizenPhoneNumber(citizenRequest.getPhoneNumber());
        return CitizenUpdateValidated.builder()
                .localGovernment(localGovernmentArea)
                .phoneNumber(phoneNumber)
                .year(year)
                .name(name)
                .build();
    }
}
