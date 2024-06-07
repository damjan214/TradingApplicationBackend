package org.example.exceptions;

public class InvalidUsernameOrPasswordException extends ResourceNotFoundException {
    public InvalidUsernameOrPasswordException(String message) {
        super(message);
    }
}
