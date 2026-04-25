package org.mackson.service;

import org.mackson.model.dtos.citizendto.citizenvotingdetails.VotingIdDetails;
import org.mackson.model.dtos.citizendto.login.CitizenLoginRequest;
import org.mackson.model.dtos.citizendto.login.CitizenLoginResponse;
import org.mackson.model.dtos.citizendto.signup.CitizenRequest;
import org.mackson.model.dtos.citizendto.signup.CitizenResponse;
import org.mackson.model.dtos.citizendto.updatecitizen.CitizenUpdateRequest;
import org.mackson.model.dtos.citizendto.updatecitizen.CitizenUpdatedResponse;
import org.springframework.stereotype.Service;

@Service
public interface CitizenServiceInterface {
    CitizenResponse createCitizenThenSave(CitizenRequest citizenRequest);
    CitizenLoginResponse logUserWithEmailAndPassword(CitizenLoginRequest citizenLoginRequest);
    CitizenUpdatedResponse updateCitizenDetails(CitizenUpdateRequest citizenRequest, String email);
    VotingIdDetails validateUserThenGenerateVotingId(String email);
    void logCitizenOut(String email);
}
