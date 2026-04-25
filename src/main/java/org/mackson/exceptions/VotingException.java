package org.mackson.exceptions;

public class VotingException extends ElectionExceptions {
    public VotingException(String notOfAgeToVote) {
        super(notOfAgeToVote);
    }
}
