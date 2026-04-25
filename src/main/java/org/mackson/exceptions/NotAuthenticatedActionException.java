package org.mackson.exceptions;

public class NotAuthenticatedActionException extends ElectionExceptions {
    public NotAuthenticatedActionException(String youAreNotLoggedIn) {
        super(youAreNotLoggedIn);
    }
}
