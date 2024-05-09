package org.example.exceptions;

public class PaymentExistsException extends RuntimeException{
    public PaymentExistsException(String message) {
        super(message);
    }
}
