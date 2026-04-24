package org.mackson.exceptions;

public class InvalidInputException extends ElectionExceptions {
    public InvalidInputException(String localGovernmentCannotBeNull) {
        super(localGovernmentCannotBeNull);
    }
}
