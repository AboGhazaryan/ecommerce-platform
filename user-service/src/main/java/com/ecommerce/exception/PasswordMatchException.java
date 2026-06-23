package com.ecommerce.exception;

public class PasswordMatchException extends RuntimeException {
    public PasswordMatchException(String message) {
        super(message);
    }
}
