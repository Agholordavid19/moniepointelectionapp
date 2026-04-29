package org.mackson.controller;

import lombok.RequiredArgsConstructor;
import org.mackson.model.dtos.citizendto.citizenvotingdetails.VotingIdDetails;
import org.mackson.model.dtos.citizendto.login.CitizenLoginRequest;
import org.mackson.model.dtos.citizendto.login.CitizenLoginResponse;
import org.mackson.model.dtos.citizendto.signup.CitizenRequest;
import org.mackson.model.dtos.citizendto.signup.CitizenResponse;
import org.mackson.model.dtos.citizendto.updatecitizen.CitizenUpdateRequest;
import org.mackson.model.dtos.citizendto.updatecitizen.CitizenUpdatedResponse;
import org.mackson.service.CitizenServiceInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/citizens")
@RequiredArgsConstructor
@CrossOrigin("*")
public class CitizenController {

    private final CitizenServiceInterface citizenService;

    @PostMapping("/register")
    public ResponseEntity<CitizenResponse> registerCitizen(@RequestBody CitizenRequest citizenRequest) {
        CitizenResponse response = citizenService.createCitizenThenSave(citizenRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<CitizenLoginResponse> login(@RequestBody CitizenLoginRequest citizenLoginRequest) {
        CitizenLoginResponse response = citizenService.logUserWithEmailAndPassword(citizenLoginRequest);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/update/{email}")
    public ResponseEntity<CitizenUpdatedResponse> updateCitizen(
            @RequestBody CitizenUpdateRequest citizenUpdateRequest,
            @PathVariable String email) {
        CitizenUpdatedResponse response = citizenService.updateCitizenDetails(citizenUpdateRequest, email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/voting-id/{email}")
    public ResponseEntity<VotingIdDetails> generateVotingId(@PathVariable("email")String email) {
        VotingIdDetails response = citizenService.validateUserThenGenerateVotingId(email);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/logout/{email}")
    public ResponseEntity<String> logout(@PathVariable("email") String email) {
        citizenService.logCitizenOut(email);
        return ResponseEntity.ok("User logged out successfully");
    }
}