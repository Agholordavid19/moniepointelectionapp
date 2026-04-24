package org.mackson.service.utils;

import org.mackson.exceptions.InvalidEmailException;
import org.mackson.exceptions.InvalidInputException;
import org.mackson.exceptions.InvalidNameException;
import org.mackson.exceptions.InvalidYearOfBirthException;
import org.mackson.model.data.LocalGovernmentArea;
import org.mackson.model.dtos.signup.CitizenRequest;

import java.time.LocalDate;

import static jdk.vm.ci.meta.JavaKind.Char;

public class CitizenInputValidator {
    private final int MAXIMUM_AGE = 120;
    public CitizenRequest checkAllInputandValidate(CitizenRequest citizenRequest) {
        String email = validateCitizenEmail(citizenRequest.getEmail());
        String name = validateCitizenName(citizenRequest.getName());
        int year = validateCitizenYear(citizenRequest.getYear());
        String phoneNumber = validateCitizenPhoneNumber(citizenRequest.getPhoneNumber());
        LocalGovernmentArea localGovernment = validateCitizenLocalGovernmentArea(citizenRequest.getLocalGovernmentArea());
    }

    private LocalGovernmentArea validateCitizenLocalGovernmentArea(String localGovernmentArea) {
        if (localGovernmentArea == null) throw new InvalidInputException("local government cannot be null");
    }

    private String validateCitizenPhoneNumber(String phoneNumber) {
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
}
