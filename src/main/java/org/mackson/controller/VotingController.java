
package org.mackson.controller;

import lombok.RequiredArgsConstructor;
import org.mackson.model.dtos.VoteRequest;
import org.mackson.model.dtos.VoteResponse;
import org.mackson.model.dtos.WinnerResponse;
import org.mackson.service.VotingServiceInterface;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/voting")
@RequiredArgsConstructor
public class VotingController {

    private final VotingServiceInterface votingService;

    @PostMapping("/castVotes")
    public ResponseEntity<VoteResponse> castVote(
            @RequestBody VoteRequest voteRequest,
            @RequestParam String email) {
        VoteResponse response = votingService.castVote(voteRequest, email);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/winners")
    public ResponseEntity<List<WinnerResponse>> getWinners() {
        return ResponseEntity.ok(votingService.getWinner());
    }
}