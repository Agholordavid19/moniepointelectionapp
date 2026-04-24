package org.mackson.service.implementations;

import org.mackson.model.dtos.signup.CitizenRequest;
import org.mackson.model.dtos.signup.CitizenResponse;
import org.mackson.service.utils.CitizenInputValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CitizenService {

    @Autowired
    private CitizenInputValidator citizenInputValidator;

    public CitizenResponse createCitizenThenSave(CitizenRequest citizenRequest) {
        citizenInputValidator.checkAllInputandValidate(citizenRequest);
    }
}
