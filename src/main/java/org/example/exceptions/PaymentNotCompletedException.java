package org.example.exceptions;

public class PaymentNotCompletedException extends RuntimeException {
    public PaymentNotCompletedException(String message) {
        super(message);
    }
}
