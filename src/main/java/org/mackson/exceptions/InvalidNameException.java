package org.mackson.exceptions;

public class InvalidNameException extends ElectionExceptions {
    public InvalidNameException(String plsEnterAValidName) {
        super(plsEnterAValidName);
    }
}
