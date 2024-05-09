package org.example.exceptions;

public class NoUserUpdateException extends RuntimeException{
    public NoUserUpdateException(String message) {
        super(message);
    }
}
