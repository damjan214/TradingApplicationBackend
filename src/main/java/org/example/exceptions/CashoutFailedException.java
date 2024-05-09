package org.example.exceptions;

public class CashoutFailedException extends RuntimeException {
    public CashoutFailedException(String message) {
        super(message);
    }
}
