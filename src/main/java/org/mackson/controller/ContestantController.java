package org.mackson.controller;

import lombok.RequiredArgsConstructor;
import org.mackson.model.dtos.contestant.ContestantRequest;
import org.mackson.model.dtos.contestant.ContestantResponse;
import org.mackson.model.dtos.contestant.ContestantVoterResponse;
import org.mackson.service.ContestantServiceInterface;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/contestants")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ContestantController {

    private final ContestantServiceInterface contestantService;

    @PostMapping("/register/{email}")
    public ResponseEntity<ContestantResponse> registerContestant(
            @RequestBody ContestantRequest contestantRequest,
            @PathVariable("email") String email) {
        ContestantResponse response = contestantService.resgisterContestant(contestantRequest, email);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/update{email}")
    public ResponseEntity<ContestantResponse> updateContestant(
            @RequestBody ContestantRequest updateRequest,
            @PathVariable("email") String email) {
        ContestantResponse response = contestantService.updateContestant(email, updateRequest);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/votes/{email}")
    public ResponseEntity<ContestantVoterResponse> getVoterCount(@RequestParam String email) {
        ContestantVoterResponse response = contestantService.getContestantVotersNumber(email);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteContestant(@RequestParam String email) {
        contestantService.deleteContestant(email);
        return ResponseEntity.ok("Contestant deleted successfully");
    }

    @GetMapping("/profile/{email}")
    public ResponseEntity<ContestantResponse> getContestantByEmail(
            @PathVariable("email") String email) {
        ContestantResponse response = contestantService.getContestantByEmail(email);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/{position}")
    public ResponseEntity<List<ContestantResponse>> getContestantPosition(@PathVariable("position") String position){
        List<ContestantResponse> listResponses = contestantService.getContestantsBasedOnPosition(position);
        return ResponseEntity.ok(listResponses);
    }

}