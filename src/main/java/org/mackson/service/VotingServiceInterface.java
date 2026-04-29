package org.mackson.service;

import org.mackson.model.dtos.VoteRequest;
import org.mackson.model.dtos.VoteResponse;
import org.mackson.model.dtos.contestant.ContestantResponse;

import java.util.List;

public interface VotingServiceInterface {

    VoteResponse castVote(VoteRequest voteRequest, String email);

}
